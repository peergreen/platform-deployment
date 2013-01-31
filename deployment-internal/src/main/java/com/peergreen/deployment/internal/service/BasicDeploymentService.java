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

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.ArtifactModelManager;
import com.peergreen.deployment.internal.model.Created;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.builder.TaskExecutionHolder;
import com.peergreen.deployment.internal.report.DefaultArtifactStatusReport;
import com.peergreen.deployment.internal.report.DefaultDeploymentStatusReport;
import com.peergreen.deployment.report.ArtifactStatusReport;
import com.peergreen.deployment.report.ArtifactStatusReportException;
import com.peergreen.deployment.report.DeploymentStatusReport;
import com.peergreen.tasks.context.ExecutionContext;
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


    private final ArtifactModelManager artifactModelManager;

    private final DeploymentServiceMonitor deploymentServiceTracker;

    public BasicDeploymentService() {
        this.artifactModelManager = new ArtifactModelManager();
        this.deploymentServiceTracker = new DeploymentServiceMonitor(this, artifactBuilder, artifactModelManager);
    }


    @Validate
    public void start() {
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
           @Override
           public Thread newThread(Runnable r) {
               Thread thread = threadFactory.newThread(r);
               thread.setName("[Peergreen] " + thread.getName());
               return thread;
           }
       });

        this.deploymentBuilder = new DeploymentBuilder(artifactModelManager, injectionContext);

        // Start thread
        deploymentServiceTracker.start();
    }

    @Invalidate
    public void stop() {
        deploymentServiceTracker.stopTracking();
        executorService.shutdown();
    }

    @Override
    public DefaultDeploymentStatusReport process(List<Artifact> artifacts, DeploymentMode deploymentMode) {
        long tStart = System.currentTimeMillis();

        TaskExecutionHolder holder = new TaskExecutionHolder();
        Task task = deploymentBuilder.buildTaskModel(artifacts, deploymentMode, holder, null);

        // Create executor
        ExecutorServiceBuilderManager executorServiceBuilderManager = new ExecutorServiceBuilderManager(holder.getTaskContextFactory(), executorService);
        TaskExecutorService executor = new TaskExecutorService(executorServiceBuilderManager);

        // Creates and register a tracker manager
        TrackerManager trackerManager = new TrackerManager();
        executorServiceBuilderManager.setTrackerManager(trackerManager);

        // Adds the time tracker that can check the total time used for a given deployment context
        TimeTaskTracker timeTracker = new TimeTaskTracker();
        trackerManager.registerTracker(timeTracker);

        // Gets the execution context
        ExecutionContext executionContext = executor.getExecutionContext();

        // Add report
        DefaultDeploymentStatusReport deploymentStatusReport = new DefaultDeploymentStatusReport(deploymentMode, artifacts);
        executionContext.add(deploymentStatusReport);

        Future<State> future = executor.execute(task);

        // Wait for task completion
        State state = null;
        try {
            state = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        long tEnd = System.currentTimeMillis();

        // populate report
        for (Artifact artifact : artifacts) {
            // Get model
            InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());
            DefaultArtifactStatusReport artifactStatusReport = new DefaultArtifactStatusReport(artifactModel.getFacetArtifact());
            deploymentStatusReport.addChild(artifactStatusReport);
            if (artifactModel.getFacetArtifact().getExceptions().size() > 0) {
                deploymentStatusReport.setFailure();
            }
            // add children that have been created by our node
            addCreatedNode(deploymentStatusReport, artifactStatusReport, artifactModel);
        }

        long elapsedTime = tEnd - tStart;
        deploymentStatusReport.setState(state);
        deploymentStatusReport.setElapsedTime(elapsedTime);
        LOGGER.info("Time elapsed ''{0}'' ms for processing artifacts ''{1}''" , elapsedTime, artifacts);


        /*Node<Task> root = new Node<Task>(new TaskNodeAdapter(), task);
        TaskRenderingVisitor taskRenderingVisitor = new TaskRenderingVisitor(System.out);
        taskRenderingVisitor.setGroups(holder.getGroups());
        root.walk(taskRenderingVisitor);*/
        return deploymentStatusReport;

    }

    protected void addCreatedNode(DefaultDeploymentStatusReport deploymentStatusReport, DefaultArtifactStatusReport artifactStatusReport, InternalArtifactModel artifactModel) {
        for (InternalWire fromWire : artifactModel.getInternalFromWires(Created.class)) {
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
        for (InternalWire fromWire : artifactModel.getInternalFromWires(Created.class)) {
            IFacetArtifact facetArtifact = fromWire.getInternalTo().getFacetArtifact();
            DefaultArtifactStatusReport childArtifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
            artifactStatusReport.addChild(childArtifactStatusReport);
            // proceed this new node
            InternalArtifactModel childArtifactModel = artifactModelManager.getArtifactModel(facetArtifact.uri());
            addChildArtifactStatusReport(childArtifactStatusReport, childArtifactModel);
        }
    }


    @Override
    public DeploymentStatusReport process(Artifact artifact, DeploymentMode mode) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(artifact);
        return process(artifacts, mode);
    }


    @Override
    public ArtifactStatusReport getReport(String uriPath) throws ArtifactStatusReportException {
        URI uri;
        try {
            uri = new URI(uriPath);
        } catch (URISyntaxException e) {
            throw new ArtifactStatusReportException("Unable to get a report for an invalid URI", e);
        }
        DefaultArtifactModel artifactModel = artifactModelManager.getArtifactModel(uri);
        if (artifactModel == null) {
            throw new ArtifactStatusReportException("No artifact model found for the given URI'");
        }
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
        DefaultArtifactStatusReport artifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
        addChildArtifactStatusReport(artifactStatusReport, artifactModel);

        return artifactStatusReport;
    }


}
