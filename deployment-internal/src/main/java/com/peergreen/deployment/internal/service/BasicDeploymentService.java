/**
 * Copyright 2012-2013 Peergreen S.A.S.
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

package com.peergreen.deployment.internal.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.builder.TaskExecutionHolder;
import com.peergreen.deployment.internal.phase.builder.TaskModelParameters;
import com.peergreen.deployment.internal.report.DefaultArtifactStatusReport;
import com.peergreen.deployment.internal.report.DefaultDeploymentStatusReport;
import com.peergreen.deployment.internal.thread.PeergreenThreadFactory;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.flag.Created;
import com.peergreen.deployment.report.ArtifactStatusReport;
import com.peergreen.deployment.report.ArtifactStatusReportException;
import com.peergreen.deployment.report.DeploymentStatusReport;
import com.peergreen.tasks.execution.helper.ExecutorServiceBuilderManager;
import com.peergreen.tasks.execution.helper.TaskExecutorService;
import com.peergreen.tasks.execution.tracker.TrackerManager;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.Task;


@Component
@Provides
@Instantiate(name="Deployment Service")
public class BasicDeploymentService implements DeploymentService {

    private static final Log LOGGER = LogFactory.getLog(BasicDeploymentService.class);

    private ExecutorService executorService;

    private DeploymentBuilder deploymentBuilder;

    @Requires
    private InjectionContext injectionContext;

    @Requires
    private ArtifactBuilder artifactBuilder;

    @Requires
    private InternalArtifactModelManager artifactModelManager;

    private final ThreadFactory threadFactory;


    public BasicDeploymentService() {
        this.threadFactory = new PeergreenThreadFactory(Executors.defaultThreadFactory(), "Deployment Executor");
    }


    @Validate
    public void start() {
        this.executorService = Executors.newFixedThreadPool(10, threadFactory);
        this.deploymentBuilder = new DeploymentBuilder(artifactModelManager, injectionContext);
    }

    @Invalidate
    public void stop() {
        // stop the threads
        executorService.shutdown();
    }


    /**
     * Process of the deployment of the given list of requests.
     * It accepts a collection, so the order it accepts is based on the underlying collection.
     * For an ordered deployment, a list should be given.
     * @param artifactProcessRequests the list of the requests.
     * @return a report for the given request
     */
    @Override
    public DeploymentStatusReport process(Collection<ArtifactProcessRequest> artifactProcessRequests) {
        long tStart = System.currentTimeMillis();

        // Create report
        DefaultDeploymentStatusReport deploymentStatusReport = new DefaultDeploymentStatusReport();


        // split for each deployment mode
        List<ArtifactProcessRequest> deployRequests = new ArrayList<>();
        List<ArtifactProcessRequest> updateRequests = new ArrayList<>();
        List<ArtifactProcessRequest> undeployRequests = new ArrayList<>();

        for (ArtifactProcessRequest artifactProcessRequest : artifactProcessRequests) {
            if (artifactProcessRequest.getDeploymentMode() == DeploymentMode.DEPLOY) {
                deployRequests.add(artifactProcessRequest);
            } else if (artifactProcessRequest.getDeploymentMode() == DeploymentMode.UPDATE) {
                updateRequests.add(artifactProcessRequest);
            } else if (artifactProcessRequest.getDeploymentMode() == DeploymentMode.UNDEPLOY) {
                undeployRequests.add(artifactProcessRequest);
            }
        }

        // Send list with items
        if (deployRequests.size() > 0) {
            process(deployRequests, DeploymentMode.DEPLOY);
        }
        if (updateRequests.size() > 0) {
            process(updateRequests, DeploymentMode.UPDATE);
        }
        if (undeployRequests.size() > 0) {
            process(undeployRequests, DeploymentMode.UNDEPLOY);
        }

        // end time
        long tEnd = System.currentTimeMillis();

        // populate report
        for (ArtifactProcessRequest artifactProcessRequest : artifactProcessRequests) {
            // Get model
            InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifactProcessRequest.getArtifact().uri());
            DefaultArtifactStatusReport artifactStatusReport = new DefaultArtifactStatusReport(artifactModel.getFacetArtifact());
            deploymentStatusReport.addChild(artifactStatusReport);
            if (artifactModel.getFacetArtifact().getExceptions().size() > 0) {
                deploymentStatusReport.setFailure();
            }
            // add children that have been created by our node
            addCreatedNode(deploymentStatusReport, artifactStatusReport, artifactModel);
        }


        long elapsedTime = tEnd - tStart;
        deploymentStatusReport.setElapsedTime(elapsedTime);
        LOGGER.info("Artifacts ''{0}'' in ''{1}'' ms." , artifactProcessRequests, elapsedTime);

        return deploymentStatusReport;
    }


    /**
     * Process of the deployment of the given list of requests for a given deployment mode.
     * It accepts a collection, so the order it accepts is based on the underlying collection.
     * For an ordered deployment, a list should be given.
     * @param artifactProcessRequests the list of the requests.
     * @return a report for the given request
     */
    protected State process(Collection<ArtifactProcessRequest> artifactProcessRequests, DeploymentMode deploymentMode) {
        // No request, so abort
        if (artifactProcessRequests == null) {
            return null;
        }

        TaskExecutionHolder holder = new TaskExecutionHolder();

        TaskModelParameters taskModelParameters = new TaskModelParameters();
        taskModelParameters.setArtifactProcessRequests(artifactProcessRequests);
        taskModelParameters.setDeploymentMode(deploymentMode);
        taskModelParameters.setTaskExecutionHolder(holder);


        Task task = deploymentBuilder.buildTaskModel(taskModelParameters);

        // Create executor
        ExecutorServiceBuilderManager executorServiceBuilderManager = new ExecutorServiceBuilderManager(holder.getTaskContextFactory(), executorService);
        TaskExecutorService executor = new TaskExecutorService(executorServiceBuilderManager);

        // Creates and register a tracker manager
        TrackerManager trackerManager = new TrackerManager();
        executorServiceBuilderManager.setTrackerManager(trackerManager);

        // Adds the time tracker that can check the total time used for a given deployment context
        TimeTaskTracker timeTracker = new TimeTaskTracker();
        trackerManager.registerTracker(timeTracker);

        Future<State> future = executor.execute(task);

        // Wait for task completion
        State state = null;
        try {
            state = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // save state after a deployment
        artifactModelManager.save();

        return state;


    }

    protected void addCreatedNode(DefaultDeploymentStatusReport deploymentStatusReport, DefaultArtifactStatusReport artifactStatusReport, InternalArtifactModel artifactModel) {
        for (InternalWire fromWire : artifactModel.getInternalWires(WireScope.FROM, Created.class.getName())) {
            IFacetArtifact facetArtifact = fromWire.getInternalTo().getFacetArtifact();
            if (facetArtifact.getExceptions().size() > 0) {
                deploymentStatusReport.setFailure();
            }
            DefaultArtifactStatusReport childArtifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
            artifactStatusReport.addChild(childArtifactStatusReport);
            // proceed this new node
            InternalArtifactModel childArtifactModel = artifactModelManager.getArtifactModel(facetArtifact.uri());
            addCreatedNode(deploymentStatusReport, childArtifactStatusReport, childArtifactModel);
        }
    }

   /**
    * Add the artifacts for the given artifact status report.
    * @param artifactStatusReport
    * @param artifactModel
    */
    protected void addChildArtifactStatusReport(DefaultArtifactStatusReport artifactStatusReport, InternalArtifactModel artifactModel) {
        for (InternalWire fromWire : artifactModel.getInternalWires(WireScope.FROM, Created.class.getName())) {
            IFacetArtifact facetArtifact = fromWire.getInternalTo().getFacetArtifact();
            DefaultArtifactStatusReport childArtifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
            artifactStatusReport.addChild(childArtifactStatusReport);
            // proceed this new node
            InternalArtifactModel childArtifactModel = artifactModelManager.getArtifactModel(facetArtifact.uri());
            addChildArtifactStatusReport(childArtifactStatusReport, childArtifactModel);
        }
    }


    @Override
    public ArtifactStatusReport getReport(String uriPath) throws ArtifactStatusReportException {
        URI uri;
        try {
            uri = new URI(uriPath);
        } catch (URISyntaxException e) {
            throw new ArtifactStatusReportException("Unable to get a report for an invalid URI", e);
        }
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(uri);
        if (artifactModel == null) {
            throw new ArtifactStatusReportException("No artifact model found for the given URI'");
        }
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
        DefaultArtifactStatusReport artifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
        addChildArtifactStatusReport(artifactStatusReport, artifactModel);

        return artifactStatusReport;
    }


}
