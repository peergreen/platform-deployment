/*
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

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osgi.resource.Capability;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.internal.artifact.FacetBuilderReference;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.persistence.read.DeployedArtifactsParser;
import com.peergreen.deployment.internal.model.persistence.write.ModelWriter;
import com.peergreen.deployment.internal.processor.NamedProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 14/01/13
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class StAXArtifactModelPersistence {
    public static final String PG_NAMESPACE_URI = "http://www.peergreen.com/xmlns/deployment/1.0";
    private Map<FacetBuilderReference, FacetBuilder> builders = new HashMap<>();
    private DefaultArtifactModelManager manager;

    public StAXArtifactModelPersistence(DefaultArtifactModelManager manager) {
        this.manager = manager;
    }

    public Map<FacetBuilderReference, FacetBuilder> getBuilders() {
        return builders;
    }

    public void persist(Writer writer) throws PersistenceException {
        Set<InternalArtifactModel> artifacts = new HashSet<>();
        Set<InternalWire> wires = new HashSet<>();

        collect(artifacts, wires);

        new ModelWriter().writeDocument(writer, artifacts, wires);
    }

    public void load(Reader in) throws PersistenceException {
        try {
            Map<String,DefaultArtifactModel> models = null;
            XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(in);

            if (reader.hasNext()) {
                do {
                    switch (reader.next()) {
                        case XMLStreamConstants.START_ELEMENT:
                            if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI()) && "deployed-artifacts".equals(reader.getLocalName())) {
                                DeployedArtifactsParser parser = new DeployedArtifactsParser();
                                parser.build(reader);
                                models = parser.getModels();
                            }
                            break;
                    }
                } while (reader.hasNext());
            }

            reader.close();

            // TODO Artifact's facets should be created at this point
            for (DefaultArtifactModel model : models.values()) {

                // complete the artifact creation with facet-builders
                completeArtifact(model.getFacetArtifact());

                if (model.isDeploymentRoot()) {
                    URI uri = model.getFacetArtifact().uri();
                    manager.addArtifactModel(uri, model);
                }
            }

        } catch (XMLStreamException e) {
            throw new PersistenceException("Cannot un-marshall", e);
        } catch (FacetBuilderException e) {
            throw new PersistenceException("Cannot un-marshall", e);
        }
    }

    private void completeArtifact(final IFacetArtifact facetArtifact) throws FacetBuilderException {
        for (final FacetBuilderReference reference : facetArtifact.getFacetBuilders()) {
            FacetBuilder builder = builders.get(reference);
            if (builder != null) {
                builder.build(new BuilderContext() {
                    @Override
                    public Artifact getArtifact() {
                        return facetArtifact;
                    }

                    @Override
                    public <F> void addFacet(Class<F> facetType, F facet) {
                        addFacet(facetType, facet, null);
                    }

                    @Override
                    public <F> void addFacet(Class<F> facetType, F facet, FacetCapabilityAdapter<F> adapter) {
                        // Try to build capability based on the facet
                        if (adapter != null) {
                            Capability capability = adapter.getCapability(facetArtifact, facet);
                            if (capability != null) {
                                facetArtifact.addCapability(capability);
                            }
                        }

                        // Add the facet capability in all cases
                        facetArtifact.addCapability(new FacetCapabilityImpl(facetArtifact, facetType));

                        // Add facet
                        facetArtifact.addFacet(facetType, facet, new NamedProcessor() {
                            @Override
                            public String getName() {
                                return reference.getName();
                            }
                        });
                    }
                });
            } else {
                throw new FacetBuilderException("Missing FacetBuilder : " + reference.getName());
            }
        }
    }

    private void collect(Set<InternalArtifactModel> artifacts, Set<InternalWire> wires) {
        Collection<? extends InternalArtifactModel> registered = manager.getDeployedRootArtifacts();

        // Traverse all artifacts
        for (InternalArtifactModel model : registered) {
            traverseArtifact(artifacts, wires, model);
        }

    }

    private void traverseArtifact(Set<InternalArtifactModel> artifacts, Set<InternalWire> wires, InternalArtifactModel model) {
        if (!artifacts.contains(model)) {
            artifacts.add(model);
            // Retrieve all its connections
            for (InternalWire wire : model.getInternalWires()) {
                // Collect wires
                if (!wires.contains(wire)) {
                    wires.add(wire);
                }
                traverseArtifact(artifacts, wires, wire.getInternalTo());
            }
        }
    }

}
