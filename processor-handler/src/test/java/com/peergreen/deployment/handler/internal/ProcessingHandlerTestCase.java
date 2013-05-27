package com.peergreen.deployment.handler.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Resource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.XMLContent;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Manifest;
import com.peergreen.deployment.processor.Attribute;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.XmlNamespace;
import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;
import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;
import com.peergreen.deployment.resource.builder.RequirementBuilder;
import com.peergreen.deployment.resource.facet.FacetRequirement;

/**
 * User: guillaume
 * Date: 14/05/13
 * Time: 12:18
 */
public class ProcessingHandlerTestCase {

    @Mock
    private ProcessingHandler.ProcessorFactory factory;

    @Mock
    private DeploymentContext deploymentContext;

    @Mock
    private Artifact artifact;

    @Mock
    private Content content;

    @Mock
    private RequirementBuilder builder;
    @Mock
    private ArchiveRequirement archiveReq;
    @Mock
    private ArtifactRequirement artifactReq;
    @Mock
    private XMLContentRequirement xmlReq;
    @Mock
    private FacetRequirement facetReq;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPhaseRequirement() throws Exception {

        when(factory.create()).thenReturn(new InstallPhaseContentProcessor());

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(builder).buildPhaseRequirement(any(Resource.class), eq("install"));
        verify(builder).buildContentRequirement(any(Resource.class));
    }

    @Test
    public void testPhaseRequirementWithDiscovery() throws Exception {

        when(factory.create()).thenReturn(new FacetScannerDiscoveryPhaseContentProcessor());

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(builder).buildPhaseRequirement(any(Resource.class), eq("FACET_SCANNER"));
        verify(builder).buildContentRequirement(any(Resource.class));
    }

    @Test
    public void testSchemeURIRequirement() throws Exception {

        when(factory.create()).thenReturn(new TotoSchemeURIProcessor());
        when(builder.buildArtifactRequirement(any(Resource.class))).thenReturn(artifactReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(artifactReq).setURIScheme("toto");
        verify(builder).buildContentRequirement(any(Resource.class));

    }

    @Test
    public void testExtensionURIRequirement() throws Exception {

        when(factory.create()).thenReturn(new JarExtensionURIProcessor());
        when(builder.buildArtifactRequirement(any(Resource.class))).thenReturn(artifactReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(artifactReq).setPathExtension(".jar");
        verify(builder).buildContentRequirement(any(Resource.class));

    }

    @Test
    public void testSchemeAndExtensionURIRequirement() throws Exception {

        when(factory.create()).thenReturn(new TotoSchemeJarExtensionURIProcessor());
        when(builder.buildArtifactRequirement(any(Resource.class))).thenReturn(artifactReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(artifactReq).setPathExtension(".jar");
        verify(artifactReq).setURIScheme("toto");
        verify(builder).buildContentRequirement(any(Resource.class));
    }

    @Test
    public void testXmlNamespaceRequirement() throws Exception {

        when(factory.create()).thenReturn(new XmlNamespaceProcessor());
        when(builder.buildXMLContentRequirement(any(Resource.class))).thenReturn(xmlReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(xmlReq).setNamespace("http://www.peergreen.com/xmlns");
        verify(builder).buildFacetRequirement(any(Resource.class), eq(XMLContent.class));
    }

    @Test
    public void testArchiveRequirement() throws Exception {

        when(factory.create()).thenReturn(new ArchiveArtifactProcessor());
        when(builder.buildArchiveRequirement(any(Resource.class))).thenReturn(archiveReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        verify(archiveReq).addRequiredAttribute("a");
        verify(archiveReq).addRequiredAttribute("c", "d");
    }

    @Test
    public void testDeploymentContextProcessorInvocation() throws Exception {

        DeploymentContextProcessor processor = new DeploymentContextProcessor();
        when(factory.create()).thenReturn(processor);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        handler.handle(deploymentContext);

        assertEquals(processor.facet, deploymentContext);
    }

    @Test
    public void testContentProcessorInvocation() throws Exception {

        InstallPhaseContentProcessor processor = new InstallPhaseContentProcessor();
        when(factory.create()).thenReturn(processor);
        when(deploymentContext.getArtifact()).thenReturn(artifact);
        when(artifact.as(Content.class)).thenReturn(content);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        handler.handle(deploymentContext);

        assertEquals(processor.facet, content);

    }

    @Test
    public void testArtifactProcessorInvocation() throws Exception {

        ArchiveArtifactProcessor processor = new ArchiveArtifactProcessor();
        when(factory.create()).thenReturn(processor);
        when(deploymentContext.getArtifact()).thenReturn(artifact);
        when(builder.buildArchiveRequirement(any(Resource.class))).thenReturn(archiveReq);

        ProcessingHandler handler = new ProcessingHandler();
        handler.setFactory(factory);
        handler.bindRequirementBuilder(builder);
        handler.configure(null, null);
        handler.start();
        handler.validate();

        handler.handle(deploymentContext);

        assertEquals(processor.facet, artifact);
    }

    @Phase("install")
    public class InstallPhaseContentProcessor implements Processor<Content> {

        private Content facet;
        private ProcessorContext processorContext;

        @Override
        public void handle(final Content facet, final ProcessorContext processorContext) throws ProcessorException {
            this.facet = facet;
            this.processorContext = processorContext;

        }
    }

    @Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
    public class FacetScannerDiscoveryPhaseContentProcessor implements Processor<Content> {

        private Content facet;
        private ProcessorContext processorContext;

        @Override
        public void handle(final Content facet, final ProcessorContext processorContext) throws ProcessorException {
            this.facet = facet;
            this.processorContext = processorContext;

        }
    }

    @Uri("toto")
    public class TotoSchemeURIProcessor implements Processor<Content> {

        @Override
        public void handle(Content instance, ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Uri(extension = ".jar")
    public class JarExtensionURIProcessor implements Processor<Content> {

        @Override
        public void handle(Content instance, ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Uri(extension = ".jar", value = "toto")
    public class TotoSchemeJarExtensionURIProcessor implements Processor<Content> {

        @Override
        public void handle(Content instance, ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @XmlNamespace("http://www.peergreen.com/xmlns")
    public class XmlNamespaceProcessor implements Processor<XMLContent> {

        @Override
        public void handle(XMLContent instance, ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Manifest({
            @Attribute(name = "a"),
            @Attribute(name = "c", value = "d")
    })
    public class ArchiveArtifactProcessor implements Processor<Artifact> {

        public Artifact facet;
        public ProcessorContext processorContext;

        @Override
        public void handle(final Artifact facet, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
            this.facet = facet;
            this.processorContext = processorContext;
        }
    }

    public class DeploymentContextProcessor implements Processor<DeploymentContext> {

        private DeploymentContext facet;
        private ProcessorContext processorContext;

        @Override
        public void handle(final DeploymentContext facet, final ProcessorContext processorContext) throws ProcessorException {
            //To change body of implemented methods use File | Settings | File Templates.
            this.facet = facet;
            this.processorContext = processorContext;
        }
    }

}
