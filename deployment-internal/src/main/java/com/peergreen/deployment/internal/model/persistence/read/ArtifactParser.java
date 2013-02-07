package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.persistence.IncompleteArtifactModel;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public class ArtifactParser implements Parser {

    private IncompleteArtifactModel incomplete = new IncompleteArtifactModel();

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("artifact".equals(reader.getLocalName())) {
                            String uri = reader.getAttributeValue(null, "uri");
                            String name = reader.getAttributeValue(null, "name");
                            boolean root = reader.getAttributeValue(null, "root") != null;
                            boolean persistent = reader.getAttributeValue(null, "persistent") != null;
                            // TODO Complement with type and persistent attributes
                            try {
                                incomplete.setModel(new DefaultArtifactModel(new FacetArtifact(new ImmutableArtifact(name, new URI(uri)))));
                                incomplete.getModel().setDeploymentRoot(root);
                                incomplete.getModel().setPersistent(persistent);
                            } catch (URISyntaxException e) {
                                throw new XMLStreamException(e);
                            }
                        }
                        if ("facet-builder".equals(reader.getLocalName())) {
                            FacetBuilderParser parser = new FacetBuilderParser();
                            parser.build(reader);
                            incomplete.getReferences().add(parser.getReference());
                        }
                    }
                    break;
            }
            reader.nextTag();
        } while (!endingElement(reader));
    }
    private boolean endingElement(XMLStreamReader reader) {
        return (reader.isEndElement()) &&
                PG_NAMESPACE_URI.equals(reader.getNamespaceURI()) &&
                "artifact".equals(reader.getLocalName());
    }

    public IncompleteArtifactModel getIncompleteArtifact() {
        return incomplete;
    }
}
