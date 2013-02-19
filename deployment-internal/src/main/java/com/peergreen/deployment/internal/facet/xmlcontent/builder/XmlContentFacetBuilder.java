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

package com.peergreen.deployment.internal.facet.xmlcontent.builder;

import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.XMLContent;
import com.peergreen.deployment.internal.facet.xmlcontent.XMLContentImpl;
import com.peergreen.deployment.internal.facet.xmlcontent.adapter.XMLContentFacetAdapter;
import com.peergreen.deployment.internal.facet.xmlcontent.processor.XMLContentProcessor;


/**
 * Defines builder for XML Content
 * @author Guillaume Sauthier
 */
public class XmlContentFacetBuilder implements FacetBuilder<XMLContent> {

    private final XMLContentProcessor xmlContentProcessor;

    public XmlContentFacetBuilder() {
        xmlContentProcessor = new XMLContentProcessor();
    }

    @Override
    public void build(BuilderContext<XMLContent> context) throws FacetBuilderException {
        // Lookup the expected Content facet (should have been build before)
        Content facet = context.getArtifact().as(Content.class);
        if (facet == null) {
            throw new FacetBuilderException(String.format(
                    "Missing Content facet in artifact %s",
                    context.getArtifact().name()
            ));
        }

        context.addFacet(new XMLContentImpl(facet, findNamespace(facet)),
                         new XMLContentFacetAdapter());
    }

    private String findNamespace(Content content) throws FacetBuilderException {

        // TODO Namespace may be a serialized value ?
        try {
            return xmlContentProcessor.findNamespace(content);
        } catch (ProcessorException e) {
            throw new FacetBuilderException("Unable to find namespace", e);
        }
    }
}
