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

import com.peergreen.deployment.internal.model.DefaultArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.persistence.read.DeployedArtifactsParser;
import com.peergreen.deployment.internal.model.persistence.write.ModelWriter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 14/01/13
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class StAXArtifactModelPersistence {
    public static final String PG_NAMESPACE_URI = "http://www.peergreen.com/xmlns/deployment/1.0";
    private DefaultArtifactModelManager manager;

    public StAXArtifactModelPersistence(DefaultArtifactModelManager manager) {
        this.manager = manager;
    }

    public void persist(Writer writer) throws PersistenceException {
        Set<InternalArtifactModel> artifacts = new HashSet<>();
        Set<InternalWire> wires = new HashSet<>();

        collect(artifacts, wires);

        new ModelWriter().writeDocument(writer, artifacts, wires);
    }

    public void load(Reader in) throws PersistenceException {
        try {
            Map<String, IncompleteArtifactModel> models = null;
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

            // TODO Artifact's facet should be created at this point

            for (IncompleteArtifactModel model : models.values()) {
                if (model.getModel().isDeploymentRoot()) {
                    URI uri = model.getModel().getFacetArtifact().uri();
                    manager.addArtifactModel(uri, model.getModel());
                }
            }

        } catch (XMLStreamException e) {
            throw new PersistenceException("Cannot un-marshall", e);
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
