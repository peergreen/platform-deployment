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
package com.peergreendeployment.internal.tests.facet.xmlcontent;

import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.content.XMLContent;
import com.peergreen.deployment.internal.facet.xmlcontent.adapter.XMLContentFacetAdapter;
import com.peergreen.deployment.resource.artifact.content.XMLContentCapability;
import com.peergreen.deployment.resource.artifact.content.XMLContentNamespace;

public class TestFacetAdapter {

    @Mock
    private Resource resource;

    @Mock
    private XMLContent xmlContent;

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testXMLContentFacetAdapter() {
        String ns = "test-NS";

        // populate XML content
        when(xmlContent.namespace()).thenReturn(ns);

        FacetCapabilityAdapter<XMLContent> facetAdapter = new XMLContentFacetAdapter();
        XMLContentCapability xmlContentCapability = facetAdapter.getCapability(resource, xmlContent);

        // Check capability
        Assert.assertEquals(xmlContentCapability.getNamespace(), XMLContentNamespace.XML_CONTENT_NAMESPACE);

        Assert.assertEquals(xmlContentCapability.getResource(), resource);
        Assert.assertNotNull(xmlContentCapability.getAttributes());
        Assert.assertEquals(xmlContentCapability.getAttributes().get(XMLContentNamespace.XML_CONTENT_NAMESPACE), "");

        // expect the correct namespace from the given attribute
        Assert.assertEquals(
                xmlContentCapability.getAttributes().get(XMLContentNamespace.CAPABILITY_XMLNAMESPACE_ATTRIBUTE), ns);

    }

}
