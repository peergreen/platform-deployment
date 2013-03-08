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
package com.peergreen.deployment.internal.processor.undeploy;

import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.internal.model.InternalArtifactModel;

/**
 * Undeploy post config processor
 * @author Florent Benoit
 */
public class UndeployPostConfigProcessor implements Processor<DeploymentContext> {

    /**
     * Remove any added facets.
     */
    @Override
    public void handle(DeploymentContext deploymentContext, ProcessorContext processorContext) throws ProcessorException {
        InternalArtifactModel artifactModel  = deploymentContext.get(InternalArtifactModel.class);
        artifactModel.getFacetArtifact().reset();
    }
}
