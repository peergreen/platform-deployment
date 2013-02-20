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

package com.peergreen.deployment.internal.model.persistence.write;

import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.ATTRIBUTE_KEY;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.NAME_ATTRIBUTE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.URI_ATTRIBUTE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.encodeValue;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.peergreen.deployment.FacetBuilderInfo;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.persistence.PersistenceException;
import com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence;

/**
 * @author Guillaume Sauthier
 */
public class ModelWriter {
    public void writeDocument(Writer out, Set<InternalArtifactModel> artifacts, Set<InternalWire> wires) throws PersistenceException {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);

            writer.writeStartDocument();
            writer.writeStartElement("deployed-artifacts");
            writer.writeDefaultNamespace(StAXArtifactModelPersistence.PG_NAMESPACE_URI);

            for (InternalArtifactModel artifact : artifacts) {
                writeArtifact(writer, artifact);
            }
            for (InternalWire wire : wires) {
                writeWire(writer, wire);
            }
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new PersistenceException("Cannot persist model", e);
        }

    }

    private void writeWire(XMLStreamWriter writer, InternalWire wire) throws XMLStreamException {
        writer.writeEmptyElement("wire");
        writeAttribute(writer, "from", wire.getInternalFrom().getFacetArtifact().uri().toString());
        writeAttribute(writer, "to", wire.getInternalTo().getFacetArtifact().uri().toString());

        Map<String, Object> attributes = wire.getAttributes();
        Set<Map.Entry<String, Object>> entries = attributes.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            writeAttribute(writer, ATTRIBUTE_KEY.concat(entry.getKey()) , encodeValue(entry.getValue()));
        }
    }

    private void writeArtifact(XMLStreamWriter writer, InternalArtifactModel artifact) throws XMLStreamException {

        IFacetArtifact facetArtifact = artifact.getFacetArtifact();
        if (facetArtifact.getFacetBuilders().isEmpty()) {
            writer.writeEmptyElement("artifact");
            writeArtifactAttributes(writer, artifact);
        } else {
            writer.writeStartElement("artifact");
            writeArtifactAttributes(writer, artifact);
            writeFacets(writer, facetArtifact);
            writer.writeEndElement();
        }

    }

    private void writeArtifactAttributes(XMLStreamWriter writer, InternalArtifactModel artifact) throws XMLStreamException {
        IFacetArtifact facetArtifact = artifact.getFacetArtifact();
        writeAttribute(writer, URI_ATTRIBUTE, facetArtifact.uri().toString());
        writeAttribute(writer, NAME_ATTRIBUTE, facetArtifact.name());

       Map<String, Object> attributes = artifact.getAttributes();
       Set<Map.Entry<String, Object>> entries = attributes.entrySet();
       for (Map.Entry<String, Object> entry : entries) {
           writeAttribute(writer, ATTRIBUTE_KEY.concat(entry.getKey()) , encodeValue(entry.getValue()));
       }
    }

    private void writeFacets(XMLStreamWriter writer, IFacetArtifact artifact) throws XMLStreamException {
        for (FacetBuilderInfo facetBuilderInfo : artifact.getFacetBuilders()) {
            writer.writeEmptyElement("facet-builder");
            writeAttribute(writer, NAME_ATTRIBUTE, facetBuilderInfo.getName());
            writeAttribute(writer, "provides", facetBuilderInfo.getProvides());
        }
    }

    private void writeAttribute(XMLStreamWriter writer, String attributeName, String value) throws XMLStreamException {
        String val = value;
        if (val == null) {
            val = "";
        }
        writer.writeAttribute(attributeName, val);
    }



}
