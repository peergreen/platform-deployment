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
package com.peergreen.deployment.internal.phase.resource;

import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.resource.AbsDeploymentRequirement;
import com.peergreen.deployment.resource.phase.PhaseNamespace;
import com.peergreen.deployment.resource.phase.PhaseRequirement;

/**
 * Requires a phase for a given resource.
 * @author Florent Benoit
 */
public class PhaseRequirementImpl extends AbsDeploymentRequirement implements PhaseRequirement {

    public PhaseRequirementImpl(Resource resource, String phaseName) {
        super(resource, PhaseNamespace.PHASE_NAMESPACE, phaseName);
    }

}
