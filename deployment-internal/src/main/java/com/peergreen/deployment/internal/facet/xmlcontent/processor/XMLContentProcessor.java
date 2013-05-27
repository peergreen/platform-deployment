/**
 * Copyright 2012 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.facet.xmlcontent.processor;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;
import com.peergreen.deployment.facet.content.XMLContent;
import com.peergreen.deployment.internal.facet.xmlcontent.XMLContentImpl;
import com.peergreen.deployment.internal.facet.xmlcontent.adapter.XMLContentFacetAdapter;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.handler.Processor;

/**
 * XML Content processor : Parse XML files to dectect the namespace and provides
 * a facet
 * @author Florent Benoit
 */
@Component
@Instantiate
@Processor
@Uri(extension = "xml")
@Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
public class XMLContentProcessor {

    private final XMLInputFactory xmlInputFactory;
    private final StreamFilter streamFilter;
    private final FacetCapabilityAdapter<XMLContent> xmlContentFacetAdapter;

    public XMLContentProcessor()  {
        this.xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        this.streamFilter = new XMLContentStreamFilter();
        this.xmlContentFacetAdapter = new XMLContentFacetAdapter();
    }

    public void handle(Content content, ProcessorContext processorContext) throws ProcessorException {
        String namespace = findNamespace(content);
        // add the facet
        processorContext.addFacet(XMLContent.class, new XMLContentImpl(content, namespace), xmlContentFacetAdapter);
    }

    public String findNamespace(Content content) throws ProcessorException {

        XMLStreamReader reader = null;
        try (InputStream is = content.getInputStream()) {
            reader = xmlInputFactory.createXMLStreamReader(is);
            reader = xmlInputFactory.createFilteredReader(reader, streamFilter);
            return reader.getNamespaceURI();
        } catch (ContentException e) {
            throw new ProcessorException("Unable to get Content's InputStream", e);
        } catch (XMLStreamException e) {
            throw new ProcessorException("Unable to parse XML", e);
        } catch (IOException e) {
            throw new ProcessorException("Unable to get Content's InputStream", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    // Ignored
                }
            }
        }
    }

}
