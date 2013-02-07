package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.model.persistence.FacetBuilderReference;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class FacetBuilderParser implements Parser {

    private FacetBuilderReference reference;

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("facet-builder".equals(reader.getLocalName())) {
                            String name = reader.getAttributeValue(null, "name");
                            reference = new FacetBuilderReference(name);
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
                "facet-builder".equals(reader.getLocalName());
    }

    public FacetBuilderReference getReference() {
        return reference;
    }
}
