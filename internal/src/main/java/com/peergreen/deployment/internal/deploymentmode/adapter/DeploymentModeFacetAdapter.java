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
package com.peergreen.deployment.internal.deploymentmode.adapter;

import org.osgi.resource.Resource;

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.internal.deploymentmode.DeploymentModeCapabilityImpl;
import com.peergreen.deployment.resource.deploymentmode.DeploymentModeCapability;

public class DeploymentModeFacetAdapter implements FacetCapabilityAdapter<DeploymentMode> {

    @Override
    public DeploymentModeCapability getCapability(Resource resource, DeploymentMode deploymentMode) {
        return new DeploymentModeCapabilityImpl(resource, deploymentMode);
    }

}
