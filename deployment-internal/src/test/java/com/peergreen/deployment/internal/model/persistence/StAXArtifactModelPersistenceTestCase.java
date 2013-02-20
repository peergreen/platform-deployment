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

package com.peergreen.deployment.internal.model.persistence;

import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.ATTRIBUTE_KEY;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.BOOLEAN_VALUETYPE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.LONG_VALUETYPE;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.NULL_VALUETYPE;
import static com.peergreen.deployment.model.ArtifactModelConstants.DEPLOYMENT_ROOT;
import static com.peergreen.deployment.model.ArtifactModelConstants.PERSISTENT;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.InternalFacetBuilderInfo;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultArtifactModelManager;
import com.peergreen.deployment.internal.model.DefaultFacetBuilderInfo;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelChangesView;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.Wire;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.flag.Use;
import com.peergreen.deployment.model.view.ArtifactModelChangesView;
import com.peergreen.deployment.model.view.ArtifactModelDeploymentView;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;

/**
 * Tests for persistence of the model
 * @author guillaume
 */
public class StAXArtifactModelPersistenceTestCase {

    @Mock
    private IFacetArtifact artifact;
    @Mock
    private IFacetArtifact artifact2;

    @Mock
    private BundleContext bundleContext;

    private DefaultArtifactModelManager manager;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manager = new DefaultArtifactModelManager(bundleContext);
    }

    @Test
    public void testOneArtifactPersistence() throws Exception {
        URI uri = new URI("test:mock");
        when(artifact.uri()).thenReturn(uri);
        when(artifact.name()).thenReturn("mock");
        List<InternalFacetBuilderInfo> facetBuilderInfos = new ArrayList<>();
        DefaultFacetBuilderInfo facetBuilderInfo = new DefaultFacetBuilderInfo();
        facetBuilderInfo.setName("hello.builder");
        facetBuilderInfo.setProvides("hello.Facet");

        facetBuilderInfos.add(facetBuilderInfo);
        when(artifact.getFacetBuilders()).thenReturn(facetBuilderInfos);
        DefaultArtifactModel model = new DefaultArtifactModel(artifact);

        long lastModified = System.currentTimeMillis();
        long length = 2503L;
        model.as(InternalArtifactModelChangesView.class).setArtifactLength(length);
        model.as(InternalArtifactModelChangesView.class).setLastModified(lastModified);


        manager.addArtifactModel(uri, model);
        StringWriter writer = new StringWriter();
        // -----------------------
        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence();
        persistence.store(manager, writer);

        assertEquals(writer.toString(), "<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\" " + ATTRIBUTE_KEY + "lastModified=\"" + LONG_VALUETYPE + lastModified + "\" " + ATTRIBUTE_KEY + "artifactLength=\"" + LONG_VALUETYPE + length + "\">" +
                "<facet-builder name=\"hello.builder\" provides=\"hello.Facet\"/>" +
                "</artifact>" +
                "</deployed-artifacts>");
    }

    @Test
    public void testTwoArtifactPersistence() throws Exception {
        URI uri = new URI("test:mock");
        URI uri2 = new URI("test:mock2");
        when(artifact.uri()).thenReturn(uri);
        when(artifact.name()).thenReturn("mock");
        when(artifact2.uri()).thenReturn(uri2);
        when(artifact2.name()).thenReturn("mock2");
        DefaultArtifactModel model = new DefaultArtifactModel(artifact);
        long lastModified = System.currentTimeMillis();
        long length = 2503L;
        model.as(InternalArtifactModelChangesView.class).setArtifactLength(length);
        model.as(InternalArtifactModelChangesView.class).setLastModified(lastModified);

        DefaultArtifactModel model2 = new DefaultArtifactModel(artifact2);
        long lastModified2 = System.currentTimeMillis() + 25;
        long length2 = 9791L;
        model2.as(InternalArtifactModelChangesView.class).setArtifactLength(length2);
        model2.as(InternalArtifactModelChangesView.class).setLastModified(lastModified2);

        DefaultWire wire = new DefaultWire(model, model2);
        wire.setAttribute(Use.class.getName());
        model.addWire(wire);
        model2.addWire(wire);

        manager.addArtifactModel(uri, model);
        StringWriter writer = new StringWriter();
        // -----------------------
        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence();
        persistence.store(manager, writer);

        // As artifacts are not sorted, I have to test elements individually
        String actual = writer.toString();
        assertTrue(actual.contains("<?xml version=\"1.0\" ?>"));
        assertTrue(actual.contains("<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">"));
        assertTrue(actual.contains("<artifact uri=\"test:mock\" name=\"mock\" " + ATTRIBUTE_KEY + "lastModified=\"" + LONG_VALUETYPE + lastModified + "\" " + ATTRIBUTE_KEY + "artifactLength=\"" + LONG_VALUETYPE + length + "\"/>"));
        assertTrue(actual.contains("<artifact uri=\"test:mock2\" name=\"mock2\" " + ATTRIBUTE_KEY + "lastModified=\"" + LONG_VALUETYPE + lastModified2 + "\" " + ATTRIBUTE_KEY + "artifactLength=\"" + LONG_VALUETYPE + length2 + "\"/>"));
        assertTrue(actual.contains("<wire from=\"test:mock\" to=\"test:mock2\" attribute-com.peergreen.deployment.model.flag.Use=\""+ NULL_VALUETYPE +  "\"/>"));
        assertTrue(actual.contains("</deployed-artifacts>"));

    }

    @Test
    public void testOneArtifactReload() throws Exception {
        Long lastModified = 9791L;
        Long artifactLength = 25L;
        String xml = "<?xml version=\"1.0\" ?>" +
        "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
        "<artifact uri=\"test:mock\" name=\"mock\" " + ATTRIBUTE_KEY + DEPLOYMENT_ROOT + "=\"" + BOOLEAN_VALUETYPE + "true\" " + ATTRIBUTE_KEY + "lastModified=\"" + LONG_VALUETYPE + lastModified + "\" " + ATTRIBUTE_KEY + "artifactLength=\"" + LONG_VALUETYPE + artifactLength + "\">" +
        "<facet-builder name=\"" + HelloFacetBuilder.class.getName() + "\" provides=\"" + Hello.class.getName() + "\"/>" +
        "</artifact>" +
        "</deployed-artifacts>";
        StringReader reader = new StringReader(xml);
        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence();
        persistence.load(manager, reader);

        URI uri = new URI("test:mock");
        InternalArtifactModel model = manager.getArtifactModel(uri);
        assertNotNull(model);
        assertEquals(model.getFacetArtifact().uri(), uri);
        assertEquals(model.getFacetArtifact().name(), "mock");
        assertFalse(model.as(ArtifactModelPersistenceView.class).isPersistent());
        assertTrue(model.as(ArtifactModelDeploymentView.class).isDeploymentRoot());
        assertEquals(model.as(ArtifactModelChangesView.class).getLastModified(), lastModified.longValue());
        assertEquals(model.as(ArtifactModelChangesView.class).getArtifactLength(), artifactLength.longValue());
        assertEquals(model.getFacetArtifact().getFacetBuilders().size(), 1);

        DefaultFacetBuilderInfo facetBuilderInfo = new DefaultFacetBuilderInfo();
        facetBuilderInfo.setName(HelloFacetBuilder.class.getName());
        facetBuilderInfo.setProvides(Hello.class.getName());
        assertTrue(model.getFacetArtifact().getFacetBuilders().contains(facetBuilderInfo));
    }

    @Test
    public void testOneArtifactReloadWithDependentFacetBuilders() throws Exception {
        StringReader reader = new StringReader("<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\" " + ATTRIBUTE_KEY + DEPLOYMENT_ROOT + "=\"" + BOOLEAN_VALUETYPE + "true\">" +
                "<facet-builder name=\"" + HelloFacetBuilder.class.getName() + "\" provides=\"" + Hello.class.getName() + "\"/>" +
                "<facet-builder name=\"" + WorldFacetBuilder.class.getName() + "\" provides=\"" + World.class.getName() + "\"/>" +
                "</artifact>" +
                "</deployed-artifacts>");

        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence();
        persistence.load(manager, reader);

        URI uri = new URI("test:mock");
        InternalArtifactModel model = manager.getArtifactModel(uri);
        assertNotNull(model);
        assertEquals(model.getFacetArtifact().uri(), uri);
        assertEquals(model.getFacetArtifact().name(), "mock");
        assertFalse(model.as(ArtifactModelPersistenceView.class).isPersistent());
        assertTrue(model.as(ArtifactModelDeploymentView.class).isDeploymentRoot());

        DefaultFacetBuilderInfo facetBuilderInfo = new DefaultFacetBuilderInfo();
        facetBuilderInfo.setName(HelloFacetBuilder.class.getName());
        facetBuilderInfo.setProvides(Hello.class.getName());
        assertTrue(model.getFacetArtifact().getFacetBuilders().contains(facetBuilderInfo));

        DefaultFacetBuilderInfo worldBuilderInfo = new DefaultFacetBuilderInfo();
        worldBuilderInfo.setName(WorldFacetBuilder.class.getName());
        worldBuilderInfo.setProvides(World.class.getName());
        assertTrue(model.getFacetArtifact().getFacetBuilders().contains(worldBuilderInfo));


    }


    @Test
    public void testWiredArtifactsReload() throws Exception {
        String xml = "<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\" " + ATTRIBUTE_KEY + DEPLOYMENT_ROOT + "=\"" + BOOLEAN_VALUETYPE + "true\" " + ATTRIBUTE_KEY + PERSISTENT + "=\"" + BOOLEAN_VALUETYPE + "true\"/>" +
                "<artifact uri=\"test:mock2\" name=\"mock2\" " + ATTRIBUTE_KEY + PERSISTENT + "=\"" + BOOLEAN_VALUETYPE + "true\"/>" +
                "<wire from=\"test:mock\" to=\"test:mock2\" attribute-firstFlag=\"" + NULL_VALUETYPE + "\" attribute-otherFlag=\"B\" attribute-anotherFlag=\"{Long}123\" />" +
                "</deployed-artifacts>";
        StringReader reader = new StringReader(xml);

        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence();
        persistence.load(manager, reader);

        URI uri = new URI("test:mock");
        InternalArtifactModel model = manager.getArtifactModel(uri);
        assertNotNull(model);
        assertEquals(model.getFacetArtifact().uri(), uri);
        assertEquals(model.getFacetArtifact().name(), "mock");
        assertTrue(model.as(ArtifactModelPersistenceView.class).isPersistent());
        assertTrue(model.as(ArtifactModelDeploymentView.class).isDeploymentRoot());

        assertNull(manager.getArtifactModel(new URI("test:mock2")));
        assertEquals(manager.getDeployedRootURIs().size(), 1);

        Wire wire = model.getWires(WireScope.FROM).iterator().next();
        assertNotNull(wire);
        ArtifactModel linked = wire.getTo();
        assertNotNull(linked);
        assertTrue(linked.as(ArtifactModelPersistenceView.class).isPersistent());
        assertFalse(linked.as(ArtifactModelDeploymentView.class).isDeploymentRoot());

        // Check attributes of the wire
        assertTrue(wire.hasAttributes("firstFlag"));
        assertTrue(wire.hasAttributes("otherFlag"));
        assertTrue(wire.hasAttributes("anotherFlag"));

        assertEquals(wire.getAttribute("firstFlag"), null);
        assertEquals(wire.getAttribute("otherFlag"), "B");
        assertEquals(wire.getAttribute("anotherFlag"), 123L);


        assertFalse(wire.hasAttributes("unknownFlag"));



    }

    private interface Hello {}

    private class HelloFacetBuilder implements FacetBuilder<Hello> {

        @Override
        public void build(BuilderContext<Hello> context) throws FacetBuilderException {
        }
    }

    private interface World {}

    private class WorldFacetBuilder implements FacetBuilder<World> {

        @Override
        public void build(BuilderContext<World> context) throws FacetBuilderException {
        }
    }
}

