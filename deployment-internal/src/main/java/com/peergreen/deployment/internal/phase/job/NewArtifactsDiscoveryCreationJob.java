/**
 * Copyright 2012 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.phase.job;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.builder.TaskExecutionHolder;
import com.peergreen.deployment.internal.phase.builder.TaskModelParameters;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Container;
import com.peergreen.tasks.model.Job;
import com.peergreen.tasks.model.Task;

/**
 * This job is checking if DEPENDENCY_FINDER processors or previous processors have added new artifacts on the deployment context.
 * For all these new artifacts, the discovery phases will be executed.
 * Once all Disovery phases will be called, the deployment phase of all the deployment context/artifacts will occur.
 * @author Florent Benoit
 */
public class NewArtifactsDiscoveryCreationJob implements Job {

    private final Container findersContainerTasks;

    public NewArtifactsDiscoveryCreationJob(Container findersContainerTasks) {
        this.findersContainerTasks = findersContainerTasks;
    }

    @Override
    public void execute(TaskContext context) throws Exception {

        DeploymentBuilder deploymentBuilder = context.get(DeploymentBuilder.class);
        DeploymentMode deploymentMode = context.get(DeploymentMode.class);
        TaskExecutionHolder taskExecutionHolder = context.get(TaskExecutionHolder.class);


        TaskExecutionHolder holderDeployment = new TaskExecutionHolder();
        holderDeployment.setOnlyDiscoveryPhases(true);
        holderDeployment.setGroups(taskExecutionHolder.getGroups());
        holderDeployment.setTaskContextFactory(taskExecutionHolder.getTaskContextFactory());
        holderDeployment.setSubstituteExecutionContextProvider(taskExecutionHolder.getSubstituteExecutionContextProvider());
        holderDeployment.setDeploymentContexts(taskExecutionHolder.getDeploymentContexts());

        // First, get all deployment contexts
        List<BasicDeploymentContext> deploymentContexts = new ArrayList<BasicDeploymentContext>();
        deploymentContexts.addAll(taskExecutionHolder.getDeploymentContexts());


        // Now, for each deployment contexts, try to see if there are new artifacts on them.
        // If we have new artifacts, we've to add a new discovery phase with the new artifacts
        for (BasicDeploymentContext deploymentContext : deploymentContexts) {
            List<ArtifactProcessRequest> newArtifactProcessRequests = deploymentContext.getNewArtifacts();
            if (newArtifactProcessRequests.size() > 0) {
                TaskModelParameters taskModelParameters = new TaskModelParameters();
                taskModelParameters.setArtifactProcessRequests(newArtifactProcessRequests);
                taskModelParameters.setDeploymentMode(deploymentMode);
                taskModelParameters.setTaskExecutionHolder(holderDeployment);
                taskModelParameters.setRootArtifactModel(deploymentContext.get(InternalArtifactModel.class));

                Task discoveryTask = deploymentBuilder.buildTaskModel(taskModelParameters);
                findersContainerTasks.add(discoveryTask);
            }
            deploymentContext.clearNewArtifacts();
        }


    }




}
