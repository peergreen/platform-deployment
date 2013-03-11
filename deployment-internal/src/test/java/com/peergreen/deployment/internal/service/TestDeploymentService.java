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

package com.peergreen.deployment.internal.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.resolver.ResolverImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.service.resolver.Resolver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.HandlerProcessor;
import com.peergreen.deployment.InternalFacetLifeCyclePhaseProvider;
import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.InternalPhases;
import com.peergreen.deployment.internal.phase.current.CurrentPhase;
import com.peergreen.deployment.internal.phase.current.DefaultCurrentPhase;
import com.peergreen.deployment.internal.phase.lifecycle.FacetLifeCycleManager;
import com.peergreen.deployment.internal.processor.InternalProcessor;
import com.peergreen.deployment.internal.processor.NamedProcessor;
import com.peergreen.deployment.internal.processor.ProcessorManager;
import com.peergreen.deployment.internal.processor.TaskInternalProcessor;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.deployment.internal.processor.current.DefaultCurrentProcessor;
import com.peergreen.deployment.internal.processor.undeploy.UndeployPostConfigProcessor;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.flag.Created;
import com.peergreen.deployment.report.ArtifactStatusReport;
import com.peergreen.deployment.report.ArtifactStatusReportException;
import com.peergreen.deployment.report.DeploymentStatusReport;

/**
 * Tests the deployment service
 *
 * @author Florent Benoit
 */
public class TestDeploymentService {

    private BasicDeploymentService deploymentService;


    private BasicInjectionContext injectionContext;
    @Mock
    private ArtifactBuilder artifactBuilder;

    private CurrentPhase currentPhase;
    private CurrentProcessor currentProcessor;

    @Mock
    private FacetLifeCycleManager facetLifeCycleManager;
    @Mock
    private InternalFacetLifeCyclePhaseProvider facetLifeCyclePhaseProvider;


    @Mock
    private ProcessorManager processorManager;

    private Resolver resolver;

    @Mock
    private InternalArtifactModelManager artifactModelManager;
    private Map<URI, InternalArtifactModel> artifactModelManagerMap;


    /**
     * Deployment service is the same for all tests
     */
    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);

        // real implementation
        resolver = new ResolverImpl(null);
        this.currentPhase = new DefaultCurrentPhase();
        this.currentProcessor = new DefaultCurrentProcessor();

        // injection context
        injectionContext = new BasicInjectionContext();
        injectionContext.bindArtifactBuilder(artifactBuilder);
        injectionContext.bindCurrentPhase(currentPhase);
        injectionContext.bindCurrentProcessor(currentProcessor);
        injectionContext.bindFacetLifeCycleManager(facetLifeCycleManager);
        injectionContext.bindProcessorManager(processorManager);
        injectionContext.bindResolver(resolver);

        deploymentService = new BasicDeploymentService();
        deploymentService.bindInjectionContext(injectionContext);
        deploymentService.bindInternalArtifactModelManager(artifactModelManager);
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new UndeployPostConfigProcessor(), DeploymentContext.class), InternalPhases.UNDEPLOY_POSTCONFIG.toString());
        deploymentService.bindThreadGroup(new ThreadGroup("Testing"));
        deploymentService.start();

        this.artifactModelManagerMap = new HashMap<URI, InternalArtifactModel>();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                URI uri = (URI) args[0];
                InternalArtifactModel artifactModel = (InternalArtifactModel) args[1];
                artifactModelManagerMap.put(uri, artifactModel);
                return null;
            }
        }).when(artifactModelManager).addArtifactModel(any(URI.class), any(InternalArtifactModel.class));
        doAnswer(new Answer<InternalArtifactModel>() {
            @Override
            public InternalArtifactModel answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                URI uri = (URI) args[0];
                return artifactModelManagerMap.get(uri);
            }
        }).when(artifactModelManager).getArtifactModel(any(URI.class));

        // Providers
        Set<InternalFacetLifeCyclePhaseProvider> providers = new HashSet<>();
        providers.add(facetLifeCyclePhaseProvider);
        doReturn(providers).when(facetLifeCycleManager).getProviders();
        doReturn(XmlPlanFacet.class).when(facetLifeCyclePhaseProvider).getFacetType();

        List<String> lstDeploy = new ArrayList<>();
        lstDeploy.add("DEPLOY_XML_PLAN");

        List<String> lstUndeploy = new ArrayList<>();
        lstUndeploy.add("UNDEPLOY_XML_PLAN");

        List<String> lstUpdate = new ArrayList<>();
        lstUpdate.add("UPDATE_XML_PLAN");


        doReturn(lstDeploy).when(facetLifeCyclePhaseProvider).getLifeCyclePhases(DeploymentMode.DEPLOY);
        doReturn(lstUndeploy).when(facetLifeCyclePhaseProvider).getLifeCyclePhases(DeploymentMode.UNDEPLOY);
        doReturn(lstUpdate).when(facetLifeCyclePhaseProvider).getLifeCyclePhases(DeploymentMode.UPDATE);


    }

    @AfterClass
    public void stop() {
        deploymentService.stop();
    }


    /**
     * Test
     *
     * @throws URISyntaxException
     */
    @Test
    public void testProcessNullRequests() throws URISyntaxException {
        DeploymentStatusReport deploymentStatusReport = deploymentService.process(null);
        Assert.assertNotNull(deploymentStatusReport);

    }


    /**
     * Build new Artifact Model
     */
    protected InternalArtifactModel createArtifactModel(String name, String uriPath) throws URISyntaxException {
        URI uri = new URI(uriPath);
        Artifact artifact = mock(Artifact.class);
        doReturn(uri).when(artifact).uri();
        doReturn(name).when(artifact).name();

        IFacetArtifact facetArtifact = spy(new FacetArtifact(artifact));
        InternalArtifactModel artifactModel = spy(new DefaultArtifactModel(facetArtifact));

        artifactModelManagerMap.put(uri, artifactModel);
        return artifactModel;
    }


    protected void addProcessor(HandlerProcessor processor, String phase) {
        List<InternalProcessor> processors = new ArrayList<>();
        // Add our processor
        InternalProcessor internalProcessor = new TaskInternalProcessor(processor, currentProcessor, currentPhase);
        processors.add(internalProcessor);

        doReturn(processors).when(processorManager).getProcessors(phase);

    }


    @Test
    public void testSingleDeploy() throws URISyntaxException {
        InternalArtifactModel artifactModel = createArtifactModel("testSingleDeploy.jar", "test://testSingleDeploy.jar");
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(facetArtifact);

        // Add processor on the processor manager
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestProcessor(), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_SCANNER.toString());

        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        Collection<ArtifactStatusReport> artifactStatusReports = deploymentStatusReport.getArtifactStatusReports();
        Assert.assertNotNull(artifactStatusReports);

        // only one node
        Assert.assertEquals(artifactStatusReports.size(), 1);
        ArtifactStatusReport artifactStatusReport = artifactStatusReports.iterator().next();

        // Report value should the same than the request
        Assert.assertEquals(artifactStatusReport.name(), facetArtifact.name());
        Assert.assertEquals(artifactStatusReport.uri(), facetArtifact.uri());

        // Check processor has been called at FACET Scanner phase
        verify(facetArtifact, times(1)).addProcessorTime(eq(DiscoveryPhasesLifecycle.FACET_SCANNER.toString()), anyLong(), any(NamedProcessor.class));

        // Check facet has been added
        DummyFacet dummyFacet = facetArtifact.as(DummyFacet.class);
        Assert.assertNotNull(dummyFacet);
        Collection<FacetInfo> facetInfos = artifactStatusReport.getFacets();
        Assert.assertEquals(facetInfos.size(), 1);
        FacetInfo facetInfo = facetInfos.iterator().next();
        Assert.assertEquals(facetInfo.getName(), DummyFacet.class.getName());
        Assert.assertEquals(facetInfo.getProcessor(), TestProcessor.class.getName());

        Collection<ProcessorInfo> processorInfos = artifactStatusReport.getProcessors();
        Iterator<ProcessorInfo> itProcessorInfo = processorInfos.iterator();
        ProcessorInfo processorInfo = null;
        while (itProcessorInfo.hasNext()) {
            processorInfo = itProcessorInfo.next();
            if (processorInfo.getPhase().equals(DiscoveryPhasesLifecycle.FACET_SCANNER.toString())) {
                break;
            }
        }
        Assert.assertNotNull(processorInfo);
        Assert.assertEquals(processorInfo.getName(), TestProcessor.class.getName());
        Assert.assertEquals(processorInfo.getPhase(), DiscoveryPhasesLifecycle.FACET_SCANNER.toString());

    }

    @Test(dependsOnMethods = "testSingleDeploy")
    public void testSingleUnDeploy() throws URISyntaxException {
        URI uri = new URI("test://testSingleDeploy.jar");
        Artifact artifact = new ImmutableArtifact("testSingleDeploy.jar", uri);
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
        artifactProcessRequest.setDeploymentMode(DeploymentMode.UNDEPLOY);

        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        Collection<ArtifactStatusReport> artifactStatusReports = deploymentStatusReport.getArtifactStatusReports();
        Assert.assertNotNull(artifactStatusReports);

        // only one node
        Assert.assertEquals(artifactStatusReports.size(), 1);
        ArtifactStatusReport artifactStatusReport = artifactStatusReports.iterator().next();

        // No more facets in the model
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(uri);
        Assert.assertNotNull(artifactModel);
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();

        // Facets should have been removed
        Assert.assertEquals(facetArtifact.getFacets().size(), 0);

        // Processors info and facet info
        Collection<ProcessorInfo> processorInfos = artifactStatusReport.getProcessors();
        ProcessorInfo processorInfo = processorInfos.iterator().next();
        Assert.assertEquals(processorInfo.getName(), UndeployPostConfigProcessor.class.getName());
        Assert.assertEquals(processorInfo.getPhase(), InternalPhases.UNDEPLOY_POSTCONFIG.toString());

        // No more facet infos
        Collection<FacetInfo> facetInfos = artifactStatusReport.getFacets();
        Assert.assertEquals(facetInfos.size(), 0);


    }


    @Test
    public void testMultiDeploy() throws URISyntaxException {
        // emulate a deployment plan
        InternalArtifactModel artifactModelXml = createArtifactModel("testMultiDeploy.xml", "test://testMultiDeploy.xml");
        IFacetArtifact facetArtifactXml = artifactModelXml.getFacetArtifact();
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(facetArtifactXml);

        // Add processor on the processor manager
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestAddingArtifactProcessor("dependency"), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_CONFLICTS.toString());
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestProcessor(), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_SCANNER.toString());

        // Deploy the "XML emulating a deployment plan"
        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        Collection<ArtifactStatusReport> artifactStatusReports = deploymentStatusReport.getArtifactStatusReports();
        Assert.assertNotNull(artifactStatusReports);

        // only one node
        Assert.assertEquals(artifactStatusReports.size(), 1);
        ArtifactStatusReport artifactStatusReport = artifactStatusReports.iterator().next();

        // Report value should the same than the request
        Assert.assertEquals(artifactStatusReport.name(), facetArtifactXml.name());
        Assert.assertEquals(artifactStatusReport.uri(), facetArtifactXml.uri());

        // Check processor has been called at FACET Scanner phase
        verify(facetArtifactXml, times(1)).addProcessorTime(eq(DiscoveryPhasesLifecycle.FACET_CONFLICTS.toString()), anyLong(), any(NamedProcessor.class));

        // Check facet has been added
        XmlPlanFacet xmlPlanFacet = facetArtifactXml.as(XmlPlanFacet.class);
        Assert.assertNotNull(xmlPlanFacet);
        Collection<FacetInfo> facetInfos = artifactStatusReport.getFacets();
        Assert.assertEquals(facetInfos.size(), 1);
        FacetInfo facetInfo = facetInfos.iterator().next();
        Assert.assertEquals(facetInfo.getName(), XmlPlanFacet.class.getName());
        Assert.assertEquals(facetInfo.getProcessor(), TestAddingArtifactProcessor.class.getName());

        // Two processors infos
        Collection<ProcessorInfo> processorInfos = artifactStatusReport.getProcessors();
        Assert.assertEquals(processorInfos.size(), 2);
        System.out.println(deploymentStatusReport);

        // Created By
        Collection<? extends InternalWire> wires = artifactModelXml.getInternalWires(WireScope.ALL, Created.class.getName());
        // 15 nodes created
        Assert.assertEquals(wires.size(), 15);

    }


    @Test(dependsOnMethods = "testMultiUnDeployGetReport")
    public void testMultiUnDeploy() throws URISyntaxException {
        URI uri = new URI("test://testMultiDeploy.xml");
        Artifact artifact = new ImmutableArtifact("testMultiDeploy.xml", uri);
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
        artifactProcessRequest.setDeploymentMode(DeploymentMode.UNDEPLOY);

        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        Collection<ArtifactStatusReport> artifactStatusReports = deploymentStatusReport.getArtifactStatusReports();
        Assert.assertNotNull(artifactStatusReports);

        // only one node
        Assert.assertEquals(artifactStatusReports.size(), 1);
        ArtifactStatusReport artifactStatusReport = artifactStatusReports.iterator().next();

        // No more facets in the model
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(uri);
        Assert.assertNotNull(artifactModel);
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();

        // Facets should have been removed
        Assert.assertEquals(facetArtifact.getFacets().size(), 0);

        // Processors info and facet info
        Collection<ProcessorInfo> processorInfos = artifactStatusReport.getProcessors();
        ProcessorInfo processorInfo = processorInfos.iterator().next();
        Assert.assertEquals(processorInfo.getName(), UndeployPostConfigProcessor.class.getName());
        Assert.assertEquals(processorInfo.getPhase(), InternalPhases.UNDEPLOY_POSTCONFIG.toString());


        // Wires
        Iterable<? extends InternalWire> wires = artifactModel.getInternalWires(WireScope.FROM, Created.class.getName());
        for (InternalWire wire : wires) {
            InternalArtifactModel toArtifactModel = wire.getInternalTo();
            Assert.assertNotNull(toArtifactModel);
            IFacetArtifact addedFacetArtifact = toArtifactModel.getFacetArtifact();

            // Facets should have been removed
            Assert.assertEquals(addedFacetArtifact.getFacets().size(), 0);
        }
    }


    @Test(expectedExceptions = ArtifactStatusReportException.class)
    public void testInvalidURIGetReport() throws ArtifactStatusReportException {
        deploymentService.getReport("::::@@@::@@@://abc");
    }

    @Test(expectedExceptions = ArtifactStatusReportException.class)
    public void testMissingURIGetReport() throws ArtifactStatusReportException, URISyntaxException {
        URI uri = new URI("test://unknown.xml");
        deploymentService.getReport(uri.toString());
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUndeployNotRootElement() throws URISyntaxException {
        InternalArtifactModel artifactModel = createArtifactModel("testUndeployNotRootElement.xml", "test://testUndeployNotRootElement.xml");
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(facetArtifact);

        // Add processor on the processor manager
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestAddingArtifactProcessor("testUndeployNotRootElement"), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_CONFLICTS.toString());
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestProcessor(), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_SCANNER.toString());

        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        // now try to undeploy a subelement
        URI undeployURI = new URI("test:testUndeployNotRootElement0.jar");
        InternalArtifactModel artifactModelSubElement = artifactModelManager.getArtifactModel(undeployURI);
        Assert.assertNotNull(artifactModelSubElement);

        ArtifactProcessRequest artifactSubElementProcessRequest = new ArtifactProcessRequest(artifactModelSubElement.getFacetArtifact());
        artifactSubElementProcessRequest.setDeploymentMode(DeploymentMode.UNDEPLOY);
        DeploymentStatusReport deploymentSubElementStatusReport = deploymentService.process(Collections.singleton(artifactSubElementProcessRequest));
        Assert.assertNotNull(deploymentSubElementStatusReport);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUndeployNotExistingElement() throws URISyntaxException {
        Artifact artifact = new ImmutableArtifact("testUndeployNotExistingElement", new URI("test://testUndeployNotExistingElement.xml"));
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
        artifactProcessRequest.setDeploymentMode(DeploymentMode.UNDEPLOY);
        DeploymentStatusReport deploymentSubElementStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentSubElementStatusReport);


    }


    @Test(dependsOnMethods = "testMultiDeploy")
    public void testMultiUnDeployGetReport() throws URISyntaxException, ArtifactStatusReportException {
        URI uri = new URI("test://testMultiDeploy.xml");
        ArtifactStatusReport artifactStatusReport = deploymentService.getReport(uri.toString());
        assertNotNull(artifactStatusReport);
        assertEquals(artifactStatusReport.getFacets().size(), 1);
        FacetInfo facetInfo = artifactStatusReport.getFacets().iterator().next();
        assertEquals(facetInfo.getName(), XmlPlanFacet.class.getName());
        assertEquals(facetInfo.getProcessor(), TestAddingArtifactProcessor.class.getName());
    }


    // Test many deploy / undeploy
    @Test
    public void testMultiDeployUndeploy() throws URISyntaxException {
        // emulate a deployment plan
        InternalArtifactModel artifactModelXml = createArtifactModel("testMultiDeployUndeploy.xml", "test://testMultiDeployUndeploy.xml");
        IFacetArtifact facetArtifactXml = artifactModelXml.getFacetArtifact();
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(facetArtifactXml);

        // Add processor on the processor manager
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestAddingArtifactProcessor("multidepundep"), DeploymentContext.class), DiscoveryPhasesLifecycle.URI_FETCHER.toString());
        addProcessor(new TestHandlerProcessor<DeploymentContext>(new TestProcessor(), DeploymentContext.class), DiscoveryPhasesLifecycle.FACET_SCANNER.toString());

        // Deploy the "XML emulating a deployment plan"
        DeploymentStatusReport deploymentStatusReport = deploymentService.process(Collections.singleton(artifactProcessRequest));
        Assert.assertNotNull(deploymentStatusReport);

        Collection<ArtifactStatusReport> artifactStatusReports = deploymentStatusReport.getArtifactStatusReports();
        Assert.assertNotNull(artifactStatusReports);
    }


}
