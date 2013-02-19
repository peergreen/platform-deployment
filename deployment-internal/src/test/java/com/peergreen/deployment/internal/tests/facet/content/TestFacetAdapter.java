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
package com.peergreen.deployment.internal.tests.facet.content;


import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.facet.content.adapter.ContentFacetAdapter;
import com.peergreen.deployment.resource.artifact.content.ContentCapability;
import com.peergreen.deployment.resource.artifact.content.ContentNamespace;

public class TestFacetAdapter {

    @Mock
    private Resource resource;

    @Mock
    private Content content;

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testContentFacetAdapter() {

        FacetCapabilityAdapter<Content> facetAdapter = new ContentFacetAdapter();
        ContentCapability contentCapability = facetAdapter.getCapability(resource, content);

        // Check capability
        Assert.assertEquals( contentCapability.getNamespace(), ContentNamespace.CONTENT_NAMESPACE);

        Assert.assertEquals( contentCapability.getResource(), resource);
        Assert.assertNotNull(contentCapability.getAttributes());
        Assert.assertEquals(contentCapability.getAttributes().get(ContentNamespace.CONTENT_NAMESPACE), "");
    }


}
