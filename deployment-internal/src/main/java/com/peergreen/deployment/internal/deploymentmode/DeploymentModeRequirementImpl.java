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
package com.peergreen.deployment.internal.deploymentmode;

import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.resource.AbsDeploymentRequirement;
import com.peergreen.deployment.resource.facet.FacetNamespace;
import com.peergreen.deployment.resource.facet.FacetRequirement;

/**
 * Requires a facet for a given resource.
 * @author Florent Benoit
 */
public class DeploymentModeRequirementImpl extends AbsDeploymentRequirement implements FacetRequirement {

    public DeploymentModeRequirementImpl(Resource resource, Class<?> facetClass) {
        super(resource, FacetNamespace.FACET_NAMESPACE, facetClass.getName());
    }

}
