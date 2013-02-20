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

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.persistence.read.DeployedArtifactsParser;
import com.peergreen.deployment.internal.model.persistence.write.ModelWriter;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.view.ArtifactModelDeploymentView;

/**
 * Allows to load or store the persistence model.
 */
@Component
@Provides
@Instantiate
public class StAXArtifactModelPersistence implements ArtifactModelPersistence {
    public static final String PG_NAMESPACE_URI = "http://www.peergreen.com/xmlns/deployment/1.0";


    @Override
    public void store(InternalArtifactModelManager artifactModelManager, Writer writer) throws PersistenceException {
        Set<InternalArtifactModel> artifacts = new HashSet<>();
        Set<InternalWire> wires = new HashSet<>();

        Collection<? extends InternalArtifactModel> registered = artifactModelManager.getDeployedRootArtifacts();

        // Traverse all artifacts
        for (InternalArtifactModel model : registered) {
            traverseArtifact(artifacts, wires, model);
        }

        new ModelWriter().writeDocument(writer, artifacts, wires);
    }

    @Override
    public void load(InternalArtifactModelManager artifactModelManager, Reader in) throws PersistenceException {
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
            if (models != null) {
                for (DefaultArtifactModel model : models.values()) {
                    if (model.as(ArtifactModelDeploymentView.class).isDeploymentRoot()) {
                        URI uri = model.getFacetArtifact().uri();
                        artifactModelManager.addArtifactModel(uri, model);
                    }
                }
            }

        } catch (XMLStreamException e) {
            throw new PersistenceException("Cannot un-marshall", e);
        }
    }


    protected void traverseArtifact(Set<InternalArtifactModel> artifacts, Set<InternalWire> wires, InternalArtifactModel model) {
        if (!artifacts.contains(model)) {
            artifacts.add(model);
            // Retrieve all its connections
            for (InternalWire wire : model.getInternalWires(WireScope.ALL)) {
                // apply wires
                if (!wires.contains(wire)) {
                    wires.add(wire);
                }
                traverseArtifact(artifacts, wires, wire.getInternalTo());
            }
        }
    }




}
