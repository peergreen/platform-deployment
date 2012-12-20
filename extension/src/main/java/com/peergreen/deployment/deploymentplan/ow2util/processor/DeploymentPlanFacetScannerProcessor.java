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

import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.deploymentplan.ow2util.DeploymentPlan;
import com.peergreen.deployment.facet.content.Content;

/**
 * Deployment plan scanner.
 * @author Florent Benoit
 */
public class DeploymentPlanFacetScannerProcessor implements Processor<Content> {


    @Override
    public void handle(Content content, ProcessorContext processorContext) throws ProcessorException {

        // Parse the content of the XML file



        // Add facet
        processorContext.addFacet(DeploymentPlan.class, new DeploymentPlan());
    }

}
