/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peergreen.deployment.internal.monitor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.ArtifactModelFilter;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelChangesView;
import com.peergreen.deployment.model.view.ArtifactModelDeploymentView;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;
import com.peergreen.deployment.monitor.URITrackerException;
import com.peergreen.deployment.monitor.URITrackerManager;

/**
 * This tracker will check for update/deleted artifacts<br />
 * If an artifact is updated, it will send the UPDATE order<br />
 * if an artifact is deleted, it will send the UNDEPLOY order.<br />
 * This class checks for bind/unbind on requires attributes as it's using Thread
 * so fields may be null while checking them in run() method.
 *
 * @author Florent Benoit
 */
@Component
@Instantiate(name = "Deployment Service artifact monitor tracker")
public class DeploymentServiceMonitor implements Runnable {

    /**
     * Deployment service.
     */
    private DeploymentService deploymentService;

    /**
     * Artifact model manager.
     */
    private InternalArtifactModelManager artifactModelManager;

    /**
     * Artifact builder.
     */
    private ArtifactBuilder artifactBuilder;

    /**
     * URI tracker.
     */
    private URITrackerManager uriTrackerManager;

    //FIXME : add configurable attribute
    private static final long checkInterval = 5000L;

    /**
     * Boolean that is checked for stopping the loop.
     */
    private final AtomicBoolean stopThread = new AtomicBoolean(false);

    /**
     * Group that will contain the Thread created to run this instance.
     */
    private ThreadGroup threadGroup;

    /**
     * Once all requirements are satisfied, thread is started to track the changes.
     */
    @Validate
    public void startTracking() {

        // reset flag
        this.stopThread.set(false);

        // Start a new thread
        Thread thread = new Thread(threadGroup, this);
        thread.setName("Peergreen Deployment artifact monitor");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Missing requirement, or being stopped so needs to tell the thread that we're stopping.
     */
    @Invalidate
    public void stopTracking() {
        this.stopThread.set(true);
    }

    /**
     * Start the thread of this class. <br>
     * It will search and deploy files to deploy.<br>
     * In development mode, it will check the changes.
     */
    @Override
    public void run() {

        for (; ; ) {
            if (stopThread.get()) {
                // Stop the thread
                return;
            }

            // Compute list of artifacts to UNDEPLOY or UPDATE
            List<ArtifactProcessRequest> artifactProcessRequests = new ArrayList<>();

            // Gets the tracked artifacts
            Collection<InternalArtifactModel> trackedArtifactModels = null;

            // Manager ?
            if (artifactModelManager != null) {
                trackedArtifactModels = artifactModelManager.getArtifacts(new DeployedRootAndNonPersistentFilter());
            } else {
                trackedArtifactModels = Collections.emptySet();
            }

            // Do a first update on the file length
            // This avoid to try to update file that are still being updated
            saveIntermediateFileLengths(trackedArtifactModels);

            // Wait
            try {
                Thread.sleep((long) (checkInterval * 0.15));
            } catch (InterruptedException e) {
                // Thread has been externally interrupted during sleep, stop the thread
                stopTracking();
                continue;
            }

            for (InternalArtifactModel artifactModel : trackedArtifactModels) {

                // Get URI
                IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
                URI uri = facetArtifact.uri();
                String name = facetArtifact.name();

                // No artifact builder
                if (artifactBuilder == null) {
                    continue;
                }

                // No uri TrackerManager
                if (uriTrackerManager == null) {
                    continue;
                }

                Artifact immutableArtifact = artifactBuilder.build(name, uri);

                // checks if the artifact still exists ?
                try {
                    if (!uriTrackerManager.exists(uri)) {
                        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(immutableArtifact);
                        artifactProcessRequest.setDeploymentMode(DeploymentMode.UNDEPLOY);
                        artifactProcessRequests.add(artifactProcessRequest);
                        continue;
                    }
                } catch (URITrackerException e) {
                    // Unable to check if the artifact still exists
                }

                // Check if the artifact has been updated
                if (artifactHasChanged(artifactModel)) {
                    ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(immutableArtifact);
                    artifactProcessRequest.setDeploymentMode(DeploymentMode.UPDATE);
                    artifactProcessRequests.add(artifactProcessRequest);
                }
            }

            // No deployment service
            if (deploymentService == null) {
                continue;
            }

            // Artifacts to send ?
            if (!artifactProcessRequests.isEmpty()) {
                deploymentService.process(artifactProcessRequests);
            }

            // Do not actively scan all deployed artifacts
            try {
                Thread.sleep((long) (checkInterval * 0.75));
            } catch (InterruptedException e) {
                // Thread has been externally interrupted during sleep, stop the thread
                stopTracking();
                continue;
            }

        }
    }

    /**
     * Checks if the artifact at the given artifact model has been updated since the last check.
     *
     * @param artifactModel the model representing the artifact
     * @return true if the artifact has been updated, else return false
     */
    protected boolean artifactHasChanged(final InternalArtifactModel artifactModel) {

        // Artifact length has changed: file copy is not finished ?
        if (lengthHasChanged(artifactModel)) {
            return true;
        }

        if (lastModifiedHasChanged(artifactModel)) {
            return true;
        }

        return false;
    }


    /**
     * Check if the file length has changed.
     *
     * @param file The given file
     * @return True if the file length has changed
     */
    private boolean lengthHasChanged(final InternalArtifactModel artifactModel) {
        InternalArtifactModelChangesView modelChanges = artifactModel.as(InternalArtifactModelChangesView.class);
        long previousLength = modelChanges.getArtifactLength();
        long intermediateLength = modelChanges.getCheckingArtifactLength();

        long currentLength = 0;
        try {
            currentLength = getLength(artifactModel.getFacetArtifact().uri());
        } catch (URITrackerException e) {
            // Error when trying to get the length so wait the next time
            return false;
        }

        // changed during the check so skip for now
        if (intermediateLength != currentLength) {
            return false;
        }

        // If the file has changed, return true
        return (previousLength != currentLength);
    }


    /**
     * Check if the last modified attribute has changed.
     *
     * @param artifactModel The given artifactModel
     * @return True if the last modified has changed
     */
    private boolean lastModifiedHasChanged(final InternalArtifactModel artifactModel) {
        InternalArtifactModelChangesView modelChanges = artifactModel.as(InternalArtifactModelChangesView.class);
        long previousLastModified = modelChanges.getLastModified();
        long currentLastModified = 0;
        try {
            currentLastModified = getLastModified(artifactModel.getFacetArtifact().uri());
        } catch (URITrackerException e) {
            // Error when trying to get the length so wait the next time
            return false;
        }

        // First check so no change
        if (previousLastModified == 0 || previousLastModified == -1) {
            return false;
        }

        return currentLastModified > previousLastModified;
    }

    /**
     * Gets the "file length" of the given URI.
     *
     * @param uri the URI on which we need to connect to get the length
     * @return the length
     * @throws URITrackerException if it's unable to get the length
     */
    protected long getLength(URI uri) throws URITrackerException {
        if (uriTrackerManager == null) {
            throw new URITrackerException("No uri tracker manager available");
        }
        return uriTrackerManager.getLength(uri);
    }

    /**
     * Gets the "last modified" of the given URI.
     *
     * @param uri the URI on which we need to connect to get the last modified
     * @return the lastModified timestamp
     * @throws URITrackerException if it's unable to get the length
     */
    protected long getLastModified(URI uri) throws URITrackerException {
        if (uriTrackerManager == null) {
            throw new URITrackerException("No uri tracker manager available");
        }
        return uriTrackerManager.getLastModified(uri);
    }

    /**
     * Save the intermediate file length of the given collection of artifacts.
     * This is used to detect if artifacts are currently bein updated.
     *
     * @param trackedArtifactModels the collection to check
     */
    protected void saveIntermediateFileLengths(Collection<InternalArtifactModel> trackedArtifactModels) {
        for (InternalArtifactModel artifactModel : trackedArtifactModels) {
            InternalArtifactModelChangesView modelChanges = artifactModel.as(InternalArtifactModelChangesView.class);
            try {
                long length = getLength(artifactModel.getFacetArtifact().uri());
                modelChanges.setCheckingArtifactLength(length);
            } catch (URITrackerException e) {
                modelChanges.setCheckingArtifactLength(-1);
            }

        }
    }

    @Bind
    public void bindURITrackerManager(URITrackerManager uriTrackerManager) {
        this.uriTrackerManager = uriTrackerManager;
    }

    @Unbind
    public void unbindURITrackerManager(URITrackerManager uriTrackerManager) {
        this.uriTrackerManager = null;
    }

    @Bind
    public void bindArtifactBuilder(ArtifactBuilder artifactBuilder) {
        this.artifactBuilder = artifactBuilder;
    }

    @Unbind
    public void unbindArtifactBuilder(ArtifactBuilder artifactBuilder) {
        this.artifactBuilder = null;
    }

    @Bind
    public void bindInternalArtifactModelManager(InternalArtifactModelManager artifactModelManager) {
        this.artifactModelManager = artifactModelManager;
    }

    @Unbind
    public void unbindInternalArtifactModelManager(InternalArtifactModelManager artifactModelManager) {
        this.artifactModelManager = null;
    }

    @Bind
    public void bindDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Unbind
    public void unbindDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = null;
    }


    @Bind(proxy=false, filter = "(group.name=peergreen)")
    public void bindThreadGroup(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    private static class DeployedRootAndNonPersistentFilter implements ArtifactModelFilter {

        @Override
        public boolean accept(final InternalArtifactModel model) {
            ArtifactModelDeploymentView deploymentView = model.as(ArtifactModelDeploymentView.class);
            ArtifactModelPersistenceView persistenceView = model.as(ArtifactModelPersistenceView.class);
            return deploymentView.isDeploymentRoot()
                    && deploymentView.isDeployed()
                    && !persistenceView.isPersistent();
        }
    }
}
