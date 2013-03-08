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
package com.peergreen.deployment.internal.facet.archive.builder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.internal.facet.archive.JarArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;

/**
 * @author Florent Benoit
 */
public class TestJarArchiveFacetBuilder {

    @Mock
    private BuilderContext<Archive> builderContext;

    @Mock
    private Artifact artifact;


    private URI uri;
    private JarArchiveFacetBuilder jarArchiveFacetBuilder;


    @BeforeMethod
    public void init() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
        this.jarArchiveFacetBuilder = spy(new JarArchiveFacetBuilder());
        this.uri = new URI("file://test.jar");
    }


    @Test
    public void testBuilder()  {
        doReturn(artifact).when(builderContext).getArtifact();
        doReturn(uri).when(artifact).uri();
        jarArchiveFacetBuilder.build(builderContext);
        doAnswer(new Answer<Void>(){
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args= invocation.getArguments();
                 JarArchiveImpl jarArchiveImpl = (JarArchiveImpl) args[0];
                ArchiveFacetAdapter archiveFacetAdapter = (ArchiveFacetAdapter) args[1];
                assertNotNull(jarArchiveImpl);
                assertNotNull(archiveFacetAdapter);
                assertEquals(jarArchiveImpl.getURI(), getURI());

                return null;
            }
        }).when(builderContext).addFacet(any(JarArchiveImpl.class), any(ArchiveFacetAdapter.class));

        verify(builderContext).addFacet(any(JarArchiveImpl.class), any(ArchiveFacetAdapter.class));

    }

    protected URI getURI() {
        return uri;
    }

}
