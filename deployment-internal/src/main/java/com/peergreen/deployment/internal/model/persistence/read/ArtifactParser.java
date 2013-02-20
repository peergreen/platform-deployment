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

package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.ATTRIBUTE_KEY;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.NAME_ATTRIBUTE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.URI_ATTRIBUTE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.decodeValue;
import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * @author guillaume
 */
public class ArtifactParser implements Parser {

    private DefaultArtifactModel model;

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("artifact".equals(reader.getLocalName())) {

                            // Read mandatory fields for building the model
                            String uri = reader.getAttributeValue(null, URI_ATTRIBUTE);
                            String name = reader.getAttributeValue(null, NAME_ATTRIBUTE);

                            try {
                                model = new DefaultArtifactModel(new FacetArtifact(new ImmutableArtifact(name, new URI(uri))));

                                // Now read the attributes
                                int count = reader.getAttributeCount();
                                for (int i = 0; i < count; i++) {
                                    QName qName = reader.getAttributeName(i);
                                    String localPart = qName.getLocalPart();
                                    if (localPart.startsWith(ATTRIBUTE_KEY)) {
                                        String attributeValue = reader.getAttributeValue(i);
                                        String attributeName = qName.getLocalPart().substring(ATTRIBUTE_KEY.length());
                                        model.getAttributes().put(attributeName, decodeValue(attributeValue));
                                    }
                                }

                            } catch (URISyntaxException e) {
                                throw new XMLStreamException(e);
                            }

                        }
                        if ("facet-builder".equals(reader.getLocalName())) {
                            FacetBuilderParser parser = new FacetBuilderParser();
                            parser.build(reader);
                            model.getFacetArtifact().getFacetBuilders().add(parser.getBuilder());
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

    public DefaultArtifactModel getIncompleteArtifact() {
        return model;
    }
}
