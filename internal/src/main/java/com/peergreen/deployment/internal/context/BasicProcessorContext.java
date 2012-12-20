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
package com.peergreen.deployment.internal.context;

import java.net.URI;
import java.util.List;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;

public class BasicProcessorContext implements ProcessorContext {

    private final BasicDeploymentContext deploymentContext;

    public BasicProcessorContext(BasicDeploymentContext deploymentContext) {
        this.deploymentContext = deploymentContext;
    }

    @Override
    public <Facet> Facet removeFacet(Class<Facet> facetClass) {
        return deploymentContext.removeFacet(facetClass);
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet) {
        addFacet(facetClass, facet, null);
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet, FacetCapabilityAdapter<Facet> facetAdapter) {
        deploymentContext.addFacet(facetClass, facet, facetAdapter);
    }

    @Override
    public void addArtifact(Artifact artifact) {
        this.deploymentContext.addArtifact(artifact);

    }

    @Override
    public void addArtifact(List<Artifact> artifacts) {
        this.deploymentContext.addArtifact(artifacts);
    }

    @Override
    public Artifact build(String name, URI uri) {
        return deploymentContext.get(ArtifactBuilder.class).build(name, uri);
    }


}
