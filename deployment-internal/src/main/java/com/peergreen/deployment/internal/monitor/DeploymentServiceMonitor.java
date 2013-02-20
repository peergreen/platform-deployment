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
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelChangesView;
import com.peergreen.deployment.monitor.URITrackerException;
import com.peergreen.deployment.monitor.URITrackerManager;

/**
 * This tracker will check for update/deleted artifacts
 * If an artifact is updated, it will send the UPDATE order
 * if an artifact is deleted, it will send the UNDEPLOY order
 * @author Florent Benoit
 */
@Component
@Instantiate(name="Deployment Service artifact monitor tracker")
public class DeploymentServiceMonitor implements Runnable {

    @Requires
    private DeploymentService deploymentService;

    @Requires
    private InternalArtifactModelManager artifactModelManager;

    @Requires
    private ArtifactBuilder artifactBuilder;

    /**
     * URI tracker.
     */
    @Requires
    private URITrackerManager uriTrackerManager;

    //FIXME : add configurable attribute
    private final long checkInterval = 5000L;

    private boolean stopThread = false;

    @Validate
    public void startTracking() {

        // reset flag
        this.stopThread = false;

        // Start a new thread
        Thread thread = new Thread(this);
        thread.setName("Peergreen Deployment artifact monitor");
        thread.start();
    }

    @Invalidate
    public void stopTracking() {
        this.stopThread = true;
    }

    /**
     * Start the thread of this class. <br>
     * It will search and deploy files to deploy.<br>
     * In development mode, it will check the changes.
     */
    @Override
    public void run() {

        for (;;) {
            if (stopThread) {
                // Stop the thread
                return;
            }

            // Compute list of artifacts to UNDEPLOY or UPDATE
            List<Artifact> toUndeploy = new ArrayList<Artifact>();
            List<Artifact> toUpdate = new ArrayList<Artifact>();


            // Gets the tracked artifacts
            Collection<InternalArtifactModel> trackedArtifactModels = artifactModelManager.getDeployedRootArtifacts();

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

                Artifact immutableArtifact = artifactBuilder.build(name, uri);

                // checks if the artifact still exists ?
                try {
                    if (!uriTrackerManager.exists(uri)) {
                        toUndeploy.add(immutableArtifact);
                    }
                } catch (URITrackerException e) {
                    // Unable to check if the artifact still exists
                }

                // Check if the artifact has been updated
                if (artifactHasChanged(artifactModel)) {
                    toUpdate.add(immutableArtifact);
                }
            }


            // Artifacts to undeploy ?
            if (!toUndeploy.isEmpty()) {
                deploymentService.process(toUndeploy, DeploymentMode.UNDEPLOY);
            }

            // Artifacts to update ?
            if (!toUpdate.isEmpty()) {
                deploymentService.process(toUpdate, DeploymentMode.UPDATE);
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


    protected long getLength(URI uri) throws URITrackerException {
        return uriTrackerManager.getLength(uri);
    }

    protected long getLastModified(URI uri) throws URITrackerException {
        return uriTrackerManager.getLastModified(uri);
    }


    protected void saveIntermediateFileLengths( Collection<InternalArtifactModel> trackedArtifactModels) {
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
}
