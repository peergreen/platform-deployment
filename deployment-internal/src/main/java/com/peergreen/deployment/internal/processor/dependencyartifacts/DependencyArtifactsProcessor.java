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
package com.peergreen.deployment.internal.processor.dependencyartifacts;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.builder.TaskExecutionHolder;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;

/**
 * Deploy, Undeploy, .. the dependency artifacts
 * @author Florent Benoit
 */
public class DependencyArtifactsProcessor implements Processor<BasicDeploymentContext> {

    @Override
    public void handle(BasicDeploymentContext deploymentContext, ProcessorContext processorContext) throws ProcessorException {

        DeploymentBuilder deploymentBuilder = deploymentContext.get(DeploymentBuilder.class);
        DeploymentMode deploymentMode = deploymentContext.get(DeploymentMode.class);
        TaskExecutionHolder taskExecutionHolder = deploymentContext.get(TaskExecutionHolder.class);
        InternalArtifactModel artifactModel = deploymentContext.get(InternalArtifactModel.class);



        // In deployment mode, we need to call the deployment of new artifacts
        if (deploymentMode == DeploymentMode.DEPLOY) {
            List<Artifact> newArtifacts = deploymentContext.getNewArtifacts();
            // We have new artifacts
            if (newArtifacts.size() > 0) {
                Task newArtifactsTask  = deploymentBuilder.buildTaskModel(newArtifacts, deploymentMode, taskExecutionHolder, artifactModel);

                // We get the pipeline associated to the POST phase
                Parallel containerTask = (Parallel) deploymentContext.getProperty("POST_DEPENDENCY_ARTIFACTS");

                // add the result of the deployment model
                containerTask.add(newArtifactsTask);

            }
        } else if (deploymentMode == DeploymentMode.UNDEPLOY) {

            // list of children
            List<Artifact> toUndeployArtifacts = new ArrayList<Artifact>();
            for (InternalWire wire : artifactModel.getInternalToWires()) {
                InternalArtifactModel child = wire.getInternalTo();
                toUndeployArtifacts.add(child.getFacetArtifact());
            }
            Task newArtifactsTask  = deploymentBuilder.buildTaskModel(toUndeployArtifacts, deploymentMode, taskExecutionHolder, artifactModel);

            // We get the pipeline associated to the POST phase
            Parallel containerTask = (Parallel) deploymentContext.getProperty("POST_DEPENDENCY_ARTIFACTS");

            // add the result of the deployment model
            containerTask.add(newArtifactsTask);

        }




    }

}
