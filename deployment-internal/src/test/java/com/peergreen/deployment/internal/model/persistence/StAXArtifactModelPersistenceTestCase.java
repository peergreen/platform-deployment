package com.peergreen.deployment.internal.model.persistence;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultArtifactModelManager;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.Wire;
import com.peergreen.deployment.model.WireType;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 14/01/13
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class StAXArtifactModelPersistenceTestCase {

    @Mock
    private IFacetArtifact artifact;
    @Mock
    private IFacetArtifact artifact2;

    private DefaultArtifactModelManager manager;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manager = new DefaultArtifactModelManager();
    }

    @Test
    public void testOneArtifactPersistence() throws Exception {
        URI uri = new URI("test:mock");
        when(artifact.uri()).thenReturn(uri);
        when(artifact.name()).thenReturn("mock");
        DefaultArtifactModel model = new DefaultArtifactModel(artifact);

        manager.addArtifactModel(uri, model);
        StringWriter writer = new StringWriter();
        // -----------------------
        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence(manager);
        persistence.persist(writer);

        assertEquals(writer.toString(), "<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\"/>" +
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
        DefaultArtifactModel model2 = new DefaultArtifactModel(artifact2);

        DefaultWire wire = new DefaultWire(model, model2, WireType.USE);
        model.addWire(wire);
        model2.addWire(wire);

        manager.addArtifactModel(uri, model);
        StringWriter writer = new StringWriter();
        // -----------------------
        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence(manager);
        persistence.persist(writer);

        // As artifacts are not sorted, I have to test elements individually
        String actual = writer.toString();
        assertTrue(actual.contains("<?xml version=\"1.0\" ?>"));
        assertTrue(actual.contains("<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">"));
        assertTrue(actual.contains("<artifact uri=\"test:mock\" name=\"mock\"/>"));
        assertTrue(actual.contains("<artifact uri=\"test:mock2\" name=\"mock2\"/>"));
        assertTrue(actual.contains("<wire from=\"test:mock\" to=\"test:mock2\"/>"));
        assertTrue(actual.contains("</deployed-artifacts>"));

    }

    @Test
    public void testOneArtifactReload() throws Exception {
        StringReader reader = new StringReader("<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\" root=\"true\"/>" +
                "</deployed-artifacts>");

        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence(manager);
        persistence.load(reader);

        URI uri = new URI("test:mock");
        InternalArtifactModel model = manager.getArtifactModel(uri);
        assertNotNull(model);
        assertEquals(model.getFacetArtifact().uri(), uri);
        assertEquals(model.getFacetArtifact().name(), "mock");
        assertFalse(model.isPersistent());
        assertTrue(model.isDeploymentRoot());
    }


    @Test
    public void testWiredArtifactsReload() throws Exception {
        StringReader reader = new StringReader("<?xml version=\"1.0\" ?>" +
                "<deployed-artifacts xmlns=\"http://www.peergreen.com/xmlns/deployment/1.0\">" +
                "<artifact uri=\"test:mock\" name=\"mock\" root=\"true\" persistent=\"true\"/>" +
                "<artifact uri=\"test:mock2\" name=\"mock2\" persistent=\"true\"/>" +
                "<wire from=\"test:mock\" to=\"test:mock2\"/>" +
                "</deployed-artifacts>");

        StAXArtifactModelPersistence persistence = new StAXArtifactModelPersistence(manager);
        persistence.load(reader);

        URI uri = new URI("test:mock");
        InternalArtifactModel model = manager.getArtifactModel(uri);
        assertNotNull(model);
        assertEquals(model.getFacetArtifact().uri(), uri);
        assertEquals(model.getFacetArtifact().name(), "mock");
        assertTrue(model.isPersistent());
        assertTrue(model.isDeploymentRoot());

        assertNull(manager.getArtifactModel(new URI("test:mock2")));
        assertEquals(manager.getDeployedRootURIs().size(), 1);

        Wire wire = model.getFromWires().iterator().next();
        assertNotNull(wire);
        ArtifactModel linked = wire.getTo();
        assertNotNull(linked);
        assertTrue(linked.isPersistent());
        assertFalse(linked.isDeploymentRoot());

    }

}

