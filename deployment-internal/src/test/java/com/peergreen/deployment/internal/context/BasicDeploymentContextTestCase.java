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

package com.peergreen.deployment.internal.context;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.facet.Facet;
import com.peergreen.deployment.internal.artifact.FacetBuilderReference;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.tasks.context.ExecutionContext;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 18/01/13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class BasicDeploymentContextTestCase {

    @Mock
    private ExecutionContext executionContext;
    @Mock
    private IFacetArtifact facetArtifact;
    @Mock
    private CurrentProcessor currentProcessor;
    private List<FacetBuilderReference> references;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        references = new ArrayList<>();
        when(executionContext.get(CurrentProcessor.class)).thenReturn(currentProcessor);
        when(facetArtifact.getFacetBuilders()).thenReturn(references);
    }

    @Test
    public void testBuilderIdFoundFromAnnotation() throws Exception {
        BasicDeploymentContext context = new BasicDeploymentContext(facetArtifact, executionContext);
        context.addFacet(Hello.class, new HelloImpl());
        assertTrue(references.contains(new FacetBuilderReference("hello.builder")));
        assertEquals(references.size(), 1);
    }

    @Test
    public void testBuilderIdFoundFromParameters() throws Exception {
        BasicDeploymentContext context = new BasicDeploymentContext(facetArtifact, executionContext);
        context.addFacet(Hello.class, new Hello2Impl(), null, "hello.builder");
        assertTrue(references.contains(new FacetBuilderReference("hello.builder")));
        assertEquals(references.size(), 1);
    }

    @Test
    public void testBuilderIdOverriddenByParameters() throws Exception {
        BasicDeploymentContext context = new BasicDeploymentContext(facetArtifact, executionContext);
        context.addFacet(Hello.class, new HelloImpl(), null, "hello.builder.2");
        assertTrue(references.contains(new FacetBuilderReference("hello.builder.2")));
        assertEquals(references.size(), 1);
    }

    // Represents the facet
    private interface Hello {}

    @Facet("hello.builder")
    private class HelloImpl implements Hello {

    }

    // No @Facet annotation, that's normal
    private class Hello2Impl implements Hello {}
}
