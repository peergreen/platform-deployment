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

package com.peergreen.deployment.internal.facet.xmlcontent.builder;

import static com.peergreen.deployment.internal.util.Closes.close;

import java.io.InputStream;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;
import com.peergreen.deployment.facet.content.XMLContent;
import com.peergreen.deployment.internal.facet.xmlcontent.XMLContentImpl;
import com.peergreen.deployment.internal.facet.xmlcontent.adapter.XMLContentFacetAdapter;
import com.peergreen.deployment.internal.facet.xmlcontent.processor.XMLContentStreamFilter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 17/01/13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class XmlContentFacetBuilder implements FacetBuilder {

    public static final String ID = "com.peergreen.deployment.internal.facet.xmlcontent.builder";
    private final XMLInputFactory factory;
    private final StreamFilter filter;

    public XmlContentFacetBuilder() {
        factory = createFactory();
        filter = new XMLContentStreamFilter();
    }

    @Override
    public void build(BuilderContext context) throws FacetBuilderException {
        // Lookup the expected Content facet (should have been build before)
        Content facet = context.getArtifact().as(Content.class);
        if (facet == null) {
            throw new FacetBuilderException(String.format(
                    "Missing Content facet in artifact %s",
                    context.getArtifact().name()
            ));
        }

        context.addFacet(XMLContent.class,
                         new XMLContentImpl(facet, findNamespace(facet)),
                         new XMLContentFacetAdapter());
    }

    private String findNamespace(Content content) throws FacetBuilderException {

        // TODO Namespace may be a serialized value ?
        InputStream is = null;
        XMLStreamReader reader = null;
        try {
            is = content.getInputStream();
            reader = factory.createXMLStreamReader(is);
            reader = factory.createFilteredReader(reader, filter);
            return reader.getNamespaceURI();
        } catch (ContentException e) {
            throw new FacetBuilderException("Unable to get Content's InputStream", e);
        } catch (XMLStreamException e) {
            throw new FacetBuilderException("Unable to parse XML", e);
        } finally {
            close(is);
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    // Ignored
                }
            }
        }
    }

    private XMLInputFactory createFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        return factory;
    }

}
