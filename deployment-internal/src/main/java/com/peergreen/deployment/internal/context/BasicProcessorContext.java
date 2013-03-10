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

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.model.ArtifactModel;

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
        addFacet(facetClass, facet, null, null);
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet, FacetCapabilityAdapter<Facet> facetAdapter) {
        addFacet(facetClass, facet, facetAdapter, null);
    }

    @Override
    public <F> void addFacet(Class<F> facetClass, F facet, String facetBuilderId) {
        addFacet(facetClass, facet, null, facetBuilderId);
    }

    @Override
    public <F> void addFacet(Class<F> facetClass, F facet, FacetCapabilityAdapter<F> facetAdapter, String facetBuilderId) {
        deploymentContext.addFacet(facetClass, facet, facetAdapter, facetBuilderId);
    }

    @Override
    public void addArtifact(Artifact artifact, boolean isPersistent) {
        this.deploymentContext.addArtifact(artifact, isPersistent);

    }


    @Override
    public Artifact build(String name, URI uri) {
        return deploymentContext.get(ArtifactBuilder.class).build(name, uri);
    }

    @Override
    public ArtifactModel getArtifactModel() {;
        return deploymentContext.get(ArtifactModel.class);
    }

}
