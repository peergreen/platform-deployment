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

import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.InternalFacetLifeCyclePhaseProvider;
import com.peergreen.deployment.internal.phase.builder.DeploymentBuilder;
import com.peergreen.deployment.internal.phase.lifecycle.FacetLifeCycleManager;
import com.peergreen.tasks.context.TaskContext;
import com.peergreen.tasks.model.Container;
import com.peergreen.tasks.model.Job;
import com.peergreen.tasks.model.group.Group;

public class DeployerCreationJob implements Job {

    private final Container deployersContainerTasks;

    public DeployerCreationJob(Container deployersContainerTasks) {
        this.deployersContainerTasks = deployersContainerTasks;
    }

    @Override
    public void execute(TaskContext context) throws Exception {

        // Get list of artifacts
        @SuppressWarnings("unchecked")
        List<DeploymentContext> deploymentContexts = (List<DeploymentContext>) context.getProperty("deploymentContexts");

        DeploymentBuilder deploymentBuilder = context.get(DeploymentBuilder.class);



        FacetLifeCycleManager facetLifeCycleManager = context.get(FacetLifeCycleManager.class);
        if (facetLifeCycleManager == null) {
            throw new IllegalStateException("Facet Life Cycle Manager should have been injected");
        }

        Iterable<InternalFacetLifeCyclePhaseProvider> iterableLifeCyclePhases = facetLifeCycleManager.getProviders();

        // No provider
        if (iterableLifeCyclePhases == null) {
            return;
        }


        //FIXME:  Should use capabilities/requirement in order to make the matching
        // Order should be done through requirements
        for (InternalFacetLifeCyclePhaseProvider provider : iterableLifeCyclePhases) {
            Class<?> type = provider.getFacetType();
            Iterable<String> phases = provider.getLifeCyclePhases(context.get(DeploymentMode.class));

            //FIXME : should be ordered !!! like Bundles after deployment plan, etc.

            // build a group for the facet provided
            List<Group> facetGroup = new ArrayList<Group>();
            for (DeploymentContext deploymentContext : deploymentContexts) {
                if (deploymentContext.getArtifact().as(type) != null) {
                    // this is an expected type
                    Group group = deploymentContext.get(Group.class);
                    facetGroup.add(group);
                }
            }

            // We have something to deploy ?
            if (facetGroup.size() > 0) {
                deployersContainerTasks.add(deploymentBuilder.getDeploymentPhases(type.getName(), phases , facetGroup, false));
            }

        }


    }




}
