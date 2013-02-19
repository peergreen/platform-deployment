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

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class DeployedArtifactsParser implements Parser {

    private final Map<String, DefaultArtifactModel> models = new HashMap<String, DefaultArtifactModel>();

    @Override
    public void build(XMLStreamReader reader) throws XMLStreamException {
        do {
            switch (reader.getEventType()) {
                case START_ELEMENT:
                    if (PG_NAMESPACE_URI.equals(reader.getNamespaceURI())) {
                        if ("artifact".equals(reader.getLocalName())) {
                            ArtifactParser parser = new ArtifactParser();
                            parser.build(reader);
                            DefaultArtifactModel model = parser.getIncompleteArtifact();
                            String uri = model.getFacetArtifact().uri().toString();
                            models.put(uri, model);
                        }
                        if ("wire".equals(reader.getLocalName())) {
                            WireParser parser = new WireParser(models);
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

    public Map<String, DefaultArtifactModel> getModels() {
        return models;
    }
}
