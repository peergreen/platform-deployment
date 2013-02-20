/**
 * Copyright 2013 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.model;

import org.osgi.resource.Resource;

import com.peergreen.deployment.FacetBuilderInfo;
import com.peergreen.deployment.InternalFacetBuilder;
import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.internal.resource.ProviderAndConsumerResource;

/**
 * This facet builder will be used to know what facet builders can be invoked on a given artifact
 * Capabilities are extracter from the FacetArtifact data <br />
 * Requirements are extracted from the published facet builder.
 * @author Florent Benoit
 */
public class ResolutionFacetBuilder extends ProviderAndConsumerResource implements Resource {

    private final String toString;
    private final InternalFacetBuilder<?> facetBuilder;

    public ResolutionFacetBuilder(FacetBuilderInfo facetBuilderInfo, InternalFacetBuilder<?> facetBuilder) {
        this.facetBuilder = facetBuilder;
        StringBuilder sb = new StringBuilder(ResolutionFacetBuilder.class.getName());
        sb.append("[name=");
        sb.append(facetBuilderInfo.getName());
        sb.append(", provides=");
        sb.append(facetBuilderInfo.getProvides());
        sb.append(", requirements=");
        sb.append(facetBuilder.getRequirements(null));
        sb.append("]");
        this.toString = sb.toString();


        // Extract capabilities
        this.addCapability(new FacetCapabilityImpl(facetBuilder, facetBuilderInfo.getProvides()));

        // Extract requirements
        this.getInnerRequirements().addAll(facetBuilder.getRequirements(null));

    }

    public InternalFacetBuilder<?> getFacetBuilder() {
        return facetBuilder;
    }

    @Override
    public String toString() {
       return toString;
    }



}
