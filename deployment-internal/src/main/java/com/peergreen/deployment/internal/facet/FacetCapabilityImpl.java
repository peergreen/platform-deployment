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
package com.peergreen.deployment.internal.facet;

import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.resource.AbsDeploymentCapability;
import com.peergreen.deployment.resource.facet.FacetCapability;
import com.peergreen.deployment.resource.facet.FacetNamespace;

/**
 * Provides a facet capability for a given resource.
 * @author Florent Benoit
 */
public class FacetCapabilityImpl extends AbsDeploymentCapability implements FacetCapability {

    public FacetCapabilityImpl(Resource resource, Class<?> facetClass) {
        super(resource, FacetNamespace.FACET_NAMESPACE, facetClass.getName());
    }

    public FacetCapabilityImpl(Resource resource, String facetClassName) {
        super(resource, FacetNamespace.FACET_NAMESPACE, facetClassName);
    }

}
