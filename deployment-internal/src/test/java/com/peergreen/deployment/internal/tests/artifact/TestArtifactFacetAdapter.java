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
package com.peergreen.deployment.internal.tests.artifact;

import static org.mockito.Mockito.doReturn;

import java.net.URI;
import java.net.URISyntaxException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.internal.artifact.adapter.ArtifactFacetAdapter;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.artifact.ArtifactNamespace;

public class TestArtifactFacetAdapter {

        @Mock
        private Resource resource;

        @Mock
        private Artifact artifact;

        private final ArtifactFacetAdapter facetAdapter;

        public TestArtifactFacetAdapter() {
            super();
            this.facetAdapter = new ArtifactFacetAdapter();
        }

        @BeforeMethod
        public void init() {
            MockitoAnnotations.initMocks(this);
        }

        @Test
        public void testPathExtension() throws URISyntaxException  {

            URI uri = new URI("file:///tmp/valid.jar");
            doReturn(uri).when(artifact).uri();

            ArtifactCapability capability = facetAdapter.getCapability(resource, artifact);
            Assert.assertNotNull(capability);
            Assert.assertEquals(capability.getAttributes().get(ArtifactNamespace.CAPABILITY_PATHEXTENSION_ATTRIBUTE), "jar");
        }

        @Test(expectedExceptions=IllegalArgumentException.class)
        public void testAlreadyExistPathExtension() throws URISyntaxException  {
            URI uri = new URI("file:///tmp/valid.jar");
            doReturn(uri).when(artifact).uri();
            ArtifactCapability capability = facetAdapter.getCapability(resource, artifact);
            capability.setPathExtension(".com");
        }

        @Test(expectedExceptions=IllegalArgumentException.class)
        public void testSchemeAlreadyExist() throws URISyntaxException  {
            URI uri = new URI("file:///tmp/valid.jar");
            doReturn(uri).when(artifact).uri();
            ArtifactCapability capability = facetAdapter.getCapability(resource, artifact);
            capability.setURIScheme("toto");
        }


        @Test
        public void testNoPathExtension() throws URISyntaxException  {

            URI uri = new URI("mvn:groupId/artifactId");
            doReturn(uri).when(artifact).uri();

            ArtifactCapability capability = facetAdapter.getCapability(resource, artifact);
            Assert.assertNotNull(capability);
            Assert.assertNull(capability.getAttributes().get(ArtifactNamespace.CAPABILITY_PATHEXTENSION_ATTRIBUTE));
        }

}
