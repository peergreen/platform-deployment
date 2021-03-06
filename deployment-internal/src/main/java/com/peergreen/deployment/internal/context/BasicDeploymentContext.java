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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.resource.Capability;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.FacetBuilderReference;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.internal.model.DefaultFacetBuilderInfo;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.deployment.internal.resource.ProviderResource;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;
import com.peergreen.tasks.context.ExecutionContext;

public class BasicDeploymentContext extends ProviderResource implements DeploymentContext  {

    private final ExecutionContext executionContext;

    private List<ArtifactProcessRequest> newArtifacts = null;

    private final IFacetArtifact currentArtifact;

    private final CurrentProcessor currentProcessor;

    private final ArtifactBuilder artifactBuilder;

    private boolean hasFailed = false;

    public BasicDeploymentContext(IFacetArtifact artifact, ExecutionContext executionContext) {
        super();
        this.currentArtifact = artifact;
        this.executionContext = executionContext;
        this.newArtifacts = new CopyOnWriteArrayList<ArtifactProcessRequest>();
        this.currentProcessor = executionContext.get(CurrentProcessor.class);
        this.artifactBuilder = executionContext.get(ArtifactBuilder.class);
    }


    @Override
    public void addArtifact(Artifact artifact) {
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);

        // Get current artifact model
        ArtifactModel artifactModel = this.get(ArtifactModel.class);
        ArtifactModelPersistenceView artifactModelPersistenceView = artifactModel.as(ArtifactModelPersistenceView.class);
        artifactProcessRequest.setPersistent(artifactModelPersistenceView.isPersistent());
        newArtifacts.add(artifactProcessRequest);
    }

    public List<ArtifactProcessRequest> getNewArtifacts() {
        return newArtifacts;
    }

    public void clearNewArtifacts() {
        newArtifacts.clear();
    }



    @Override
    public Artifact getArtifact() {
        return currentArtifact;
    }


    @Override
    public <Facet> Facet removeFacet(Class<Facet> facetClass) {
        return currentArtifact.removeFacet(facetClass);
    }


    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet) {
        addFacet(facetClass, facet, null);
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet, FacetCapabilityAdapter<Facet> facetAdapter) {
        addFacet(facetClass, facet, facetAdapter, null);
    }

    @Override
    public <F> void addFacet(Class<F> facetClass, F facet, FacetCapabilityAdapter<F> facetAdapter, String facetBuilderName) {
        // Try to build capability based on the facet
        if (facetAdapter != null) {
            Capability capability = facetAdapter.getCapability(currentArtifact, facet);
            if (capability != null) {
                currentArtifact.addCapability(capability);
            }
        }

        // Add the facet capability in all cases
        currentArtifact.addCapability(new FacetCapabilityImpl(currentArtifact, facetClass));

        // Store the facetBuilderId
        // Id provided as a direct method parameter is preferred over the ones found in the annotations
        String builderName = facetBuilderName;
        if (facetBuilderName == null) {
            FacetBuilderReference annotation = facet.getClass().getAnnotation(FacetBuilderReference.class);
            if (annotation != null) {
                builderName = annotation.value().getName();
            }
        }

        if (builderName != null) {
            // Build a facet builder with the given facet that is provided by this builder
            DefaultFacetBuilderInfo facetBuilderInfo = new DefaultFacetBuilderInfo();
            facetBuilderInfo.setName(builderName);
            facetBuilderInfo.setProvides(facetClass.getName());

            if (!currentArtifact.getFacetBuilders().contains(facetBuilderInfo)) {
                currentArtifact.getFacetBuilders().add(facetBuilderInfo);
            }
        }

        // Add the facet
        currentArtifact.addFacet(facetClass, facet, currentProcessor.getCurrent());

    }

    @Override
    public <F> F getFacet(final Class<F> facetClass) {
        return facetClass.cast(currentArtifact.getFacets().get(facetClass));
    }

    @Override
    public ArtifactBuilder getArtifactBuilder() {
        return artifactBuilder;
    }

    @Override
    public <T> T get(Class<T> type) {
        return executionContext.get(type);
    }

    @Override
    public void remove(Object instance) {
        executionContext.remove(instance);
    }

    @Override
    public void add(Object instance) {
        executionContext.add(instance);
    }


    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public IFacetArtifact getFacetArtifact() {
        return currentArtifact;
    }


    @Override
    public void setProperty(String name, Object value) {
        executionContext.setProperty(name, value);
    }


    @Override
    public Object getProperty(String name) {
        return executionContext.getProperty(name);
    }


    @Override
    public List<Capability> getCapabilities(String namespace) {
        // add deployment context and artifact capabilities
        List<Capability> capabilities = new ArrayList<Capability>();
        capabilities.addAll(super.getCapabilities(namespace));
        capabilities.addAll(currentArtifact.getCapabilities(namespace));
        return capabilities;
    }


    @Override
    public boolean hasFailed() {
        return hasFailed;
    }

    public void setFailed() {
        this.hasFailed = true;
    }


}
