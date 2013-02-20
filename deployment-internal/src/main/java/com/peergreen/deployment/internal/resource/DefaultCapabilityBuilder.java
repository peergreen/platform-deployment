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
package com.peergreen.deployment.internal.resource;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.resource.builder.CapabilityBuilder;
import com.peergreen.deployment.resource.facet.FacetCapability;

@Component
@Provides
@Instantiate(name="Capability Builder")
public class DefaultCapabilityBuilder implements CapabilityBuilder {

    @Override
    public FacetCapability buildFacetCapability(Resource resource, Class<?> facetClass) {
        return new FacetCapabilityImpl(resource, facetClass);
    }

}
