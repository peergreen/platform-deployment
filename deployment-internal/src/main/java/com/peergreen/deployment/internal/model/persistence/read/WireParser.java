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

import static com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence.PG_NAMESPACE_URI;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.model.WireType;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class WireParser implements Parser {

    private Map<String, DefaultArtifactModel> models;

    public WireParser(Map<String, DefaultArtifactModel> models) {
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
        DefaultArtifactModel model = models.get(uri);
        if (model == null) {
            throw new XMLStreamException("Cannot find model for URI " + uri);
        }
        return model;
    }

    private boolean endingElement(XMLStreamReader reader) {
        return (reader.isEndElement()) &&
                PG_NAMESPACE_URI.equals(reader.getNamespaceURI()) &&
                "wire".equals(reader.getLocalName());
    }

}
