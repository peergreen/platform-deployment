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
package com.peergreen.deployment.deploymentplan.ow2util.processor;

import java.io.File;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.deploymentplan.ow2util.DeploymentPlan;

/**
 * Deployment plan scanner.
 * @author Florent Benoit
 */
public class DeploymentPlanDeployProcessor implements Processor<DeploymentPlan> {


    @Override
    public void handle(DeploymentPlan deploymentPlan, ProcessorContext processorContext) throws ProcessorException {

        Artifact af = processorContext.build("florent", new File("/Users/benoitf/Documents/workspace/deployment/client/src/main/resources/bundle2.jar").toURI());

        processorContext.addArtifact(af);

        // add all artifacts
        processorContext.addArtifact(deploymentPlan.getArtifacts());
    }

}
