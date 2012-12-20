package com.peergreen.deployment.internal.processor;

import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.internal.context.Job;
import com.peergreen.tasks.context.TaskContext;

public class WrapDeploymentContextJob implements com.peergreen.tasks.model.Job {

    private final Job deploymentContextJob;

    public WrapDeploymentContextJob(Job deploymentContextJob) {
        this.deploymentContextJob = deploymentContextJob;
    }

    @Override
    public void execute(TaskContext context) {

        try {
            DeploymentContext deploymentContext = context.get(DeploymentContext.class);
            deploymentContextJob.execute(deploymentContext);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }

    }


}
