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
package com.peergreen.deployment.internal.model.persistence;

import javax.xml.stream.XMLStreamReader;

import org.osgi.resource.Capability;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.internal.processor.NamedProcessor;

public class BuilderContextFactory {

    public <Facet> BuilderContext<Facet> build(final Class<Facet> clazz, final IFacetArtifact facetArtifact, final String name/*, final XMLStreamReader xmlStreamReader*/) {

        return new BuilderContext<Facet>() {

            @Override
            public Artifact getArtifact() {
                return facetArtifact;
            }

            @Override
            public void addFacet(Facet facet) {
                addFacet(facet, null);

            }

            @Override
            public void addFacet(Facet facet, FacetCapabilityAdapter<Facet> adapter) {
             // Try to build capability based on the facet
                if (adapter != null) {
                    Capability capability = adapter.getCapability(facetArtifact, facet);
                    if (capability != null) {
                        facetArtifact.addCapability(capability);
                    }
                }

                // Add the facet capability in all cases
                facetArtifact.addCapability(new FacetCapabilityImpl(facetArtifact, clazz));

                // Add facet
                facetArtifact.addFacet(clazz, facet, new NamedProcessor() {
                    @Override
                    public String getName() {
                        return name;
                    }
                });
            }

            @Override
            public XMLStreamReader getXMLStreamReader() {
                throw new UnsupportedOperationException();
                //return xmlStreamReader;
            }
        };


    }
}
