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

package com.peergreen.deployment.internal.model.persistence.write;

import java.io.Writer;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.peergreen.deployment.internal.artifact.FacetBuilderReference;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.persistence.PersistenceException;
import com.peergreen.deployment.internal.model.persistence.StAXArtifactModelPersistence;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 17/01/13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
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
        writer.writeAttribute("from", wire.getInternalFrom().getFacetArtifact().uri().toString());
        writer.writeAttribute("to", wire.getInternalTo().getFacetArtifact().uri().toString());
        // TODO Complete with wire attributes (when ready)
        //writer.writeEndElement();
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
        writer.writeAttribute("uri", facetArtifact.uri().toString());
        writer.writeAttribute("name", facetArtifact.name());
        if (artifact.isDeploymentRoot()) {
            writer.writeAttribute("root", "true");
        }
        if (artifact.isPersistent()) {
            writer.writeAttribute("persistent", "true");
        }
    }

    private void writeFacets(XMLStreamWriter writer, IFacetArtifact artifact) throws XMLStreamException {
        for (FacetBuilderReference reference : artifact.getFacetBuilders()) {
            writer.writeEmptyElement("facet-builder");
            writer.writeAttribute("name", reference.getName());
        }
    }


}
