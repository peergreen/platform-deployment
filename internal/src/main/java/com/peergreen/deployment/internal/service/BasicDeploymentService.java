package com.peergreen.deployment.internal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.DeploymentStatusReport;
import com.peergreen.deployment.internal.model.ArtifactModelManager;
import com.peergreen.deployment.internal.model.Created;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.builder.TaskExecutionHolder;
import com.peergreen.deployment.internal.report.ArtifactStatusReport;
import com.peergreen.deployment.internal.report.DefaultDeploymentStatusReport;
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

    private ExecutorService executorService = null;

    private final DeploymentBuilder deploymentBuilder;

    @Requires
    private final InjectionContext injectionContext = null;


    private final ArtifactModelManager artifactModelManager;


    public BasicDeploymentService() {
       this.executorService = Executors.newFixedThreadPool(10);
       this.artifactModelManager = new ArtifactModelManager();
       // this.executorService = Executors.newCachedThreadPool();
        //this.executorService = Executors.newScheduledThreadPool(200);
        this.deploymentBuilder = new DeploymentBuilder(artifactModelManager, injectionContext);
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
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        long tEnd = System.currentTimeMillis();

        // populate report
        for (Artifact artifact : artifacts) {
            // Get model
            InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());
            ArtifactStatusReport artifactStatusReport = new ArtifactStatusReport(artifactModel.getFacetArtifact());
            deploymentStatusReport.addChild(artifactStatusReport);
            if (artifactModel.getFacetArtifact().getExceptions().size() > 0) {
                deploymentStatusReport.setFailure();
            }
            // add children that have been created by our node
            for (InternalWire toWire : artifactModel.getInternalToWires(Created.class)) {
                ArtifactStatusReport childArtifactStatusReport = new ArtifactStatusReport(toWire.getInternalTo().getFacetArtifact());
                if (toWire.getInternalTo().getFacetArtifact().getExceptions().size() > 0) {
                    deploymentStatusReport.setFailure();
                }
                artifactStatusReport.addChild(childArtifactStatusReport);
            }
        }




        long elapsedTime = tEnd - tStart;
        deploymentStatusReport.setState(state);
        deploymentStatusReport.setElapsedTime(elapsedTime);
        LOGGER.info("Time elapsed ''{0}'' ms" , elapsedTime);

        return deploymentStatusReport;

    }

    @Override
    public DeploymentStatusReport process(Artifact artifact, DeploymentMode mode) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(artifact);
        return process(artifacts, mode);
    }

}
