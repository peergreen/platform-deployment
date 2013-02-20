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

package com.peergreen.deployment.internal.model.persistence.read;

import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.NAME_ATTRIBUTE;
import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.artifact.InternalFacetBuilderInfo;
import com.peergreen.deployment.internal.model.DefaultFacetBuilderInfo;

/**
 * @author Guillaume
 */
public class FacetBuilderParser implements Parser {

    private InternalFacetBuilderInfo facetBuilderInfo;

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    facetBuilderInfo = new DefaultFacetBuilderInfo();
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("facet-builder".equals(reader.getLocalName())) {
                            String name = reader.getAttributeValue(null, NAME_ATTRIBUTE);
                            String provides = reader.getAttributeValue(null, "provides");
                            facetBuilderInfo.setName(name);
                            facetBuilderInfo.setProvides(provides);
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

    public InternalFacetBuilderInfo getBuilder() {
        return facetBuilderInfo;
    }
}
