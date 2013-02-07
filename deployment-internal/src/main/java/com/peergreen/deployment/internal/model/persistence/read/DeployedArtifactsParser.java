package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.model.persistence.IncompleteArtifactModel;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class DeployedArtifactsParser implements Parser {

    private Map<String,IncompleteArtifactModel> m_models = new HashMap<>();

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("artifact".equals(reader.getLocalName())) {
                            ArtifactParser parser = new ArtifactParser();
                            parser.build(reader);
                            IncompleteArtifactModel model = parser.getIncompleteArtifact();
                            String uri = model.getModel().getFacetArtifact().uri().toString();
                            m_models.put(uri, model);
                        }
                        if ("wire".equals(reader.getLocalName())) {
                            WireParser parser = new WireParser(m_models);
                            parser.build(reader);
                        }
                    }
                    break;
            }
            if (reader.hasNext()) {
                reader.nextTag();
            }
        } while (!endingElement(reader));
    }

    private boolean endingElement(XMLStreamReader reader) {
        return (reader.isEndElement()) &&
                PG_NAMESPACE_URI.equals(reader.getNamespaceURI()) &&
                "deployed-artifacts".equals(reader.getLocalName());
    }

    public Map<String, IncompleteArtifactModel> getModels() {
        return m_models;
    }
}
