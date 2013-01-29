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

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.internal.resource.AbsDeploymentCapability;
import com.peergreen.deployment.resource.deploymentmode.DeploymentModeCapability;
import com.peergreen.deployment.resource.deploymentmode.DeploymentModeNamespace;

/**
 * Provides a deployment mode capability for a given resource.
 * @author Florent Benoit
 */
public class DeploymentModeCapabilityImpl extends AbsDeploymentCapability implements DeploymentModeCapability {

    public DeploymentModeCapabilityImpl(Resource resource, DeploymentMode deploymentMode) {
        super(resource, DeploymentModeNamespace.DEPLOYMENTMODE_NAMESPACE, deploymentMode.toString());
    }
}
