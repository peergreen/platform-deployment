package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.internal.model.persistence.IncompleteArtifactModel;
import com.peergreen.deployment.model.WireType;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class WireParser implements Parser {

    private Map<String, IncompleteArtifactModel> models;

    public WireParser(Map<String, IncompleteArtifactModel> models) {
        this.models = models;
    }

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("wire".equals(reader.getLocalName())) {
                            String from = reader.getAttributeValue(null, "from");
                            String to = reader.getAttributeValue(null, "to");
                            DefaultArtifactModel fromModel = find(from);
                            DefaultArtifactModel toModel = find(to);
                            DefaultWire wire = new DefaultWire(fromModel, toModel, WireType.USE);
                            fromModel.addWire(wire);
                            toModel.addWire(wire);
                        }
                    }
                    break;
            }
            reader.nextTag();
        } while (!endingElement(reader));
    }

    private DefaultArtifactModel find(String uri) throws XMLStreamException {
        IncompleteArtifactModel model = models.get(uri);
        if (model == null) {
            throw new XMLStreamException("Cannot find model for URI " + uri);
        }
        return model.getModel();
    }

    private boolean endingElement(XMLStreamReader reader) {
        return (reader.isEndElement()) &&
                PG_NAMESPACE_URI.equals(reader.getNamespaceURI()) &&
                "wire".equals(reader.getLocalName());
    }

}
