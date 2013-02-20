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
package com.peergreen.deployment.internal.tests.processors.customprocessor;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.resolver.Logger;
import org.apache.felix.resolver.ResolverImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.resolver.Resolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DelegateHandlerProcessor;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.adapter.ArtifactFacetAdapter;
import com.peergreen.deployment.internal.facet.content.resource.ContentCapabilityImpl;
import com.peergreen.deployment.internal.processor.DefaultProcessorManager;
import com.peergreen.deployment.internal.processor.ProcessorJob;
import com.peergreen.deployment.internal.processor.ProcessorManager;
import com.peergreen.deployment.internal.resource.DefaultRequirementBuilder;
import com.peergreen.deployment.internal.resource.ProviderResource;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.artifact.content.ContentCapability;
import com.peergreen.deployment.resource.builder.RequirementBuilder;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Task;

public class TestCallProcessorResolver {

    private DefaultProcessorManager processorManager = null;


    @Mock
    private DeploymentContext deploymentContext;

    @Mock
    private Content content;

    private Parallel parallel;


    private final Resolver resolver;

    private RequirementBuilder requirementBuilder = null;

    private final  FacetCapabilityAdapter<Artifact> artifactFacetAdapter;


    @Mock
    private Artifact artifact;

    @Mock
    private IFacetArtifact facetArtifact;


    public TestCallProcessorResolver() {
        this.resolver = new ResolverImpl(new Logger(Logger.LOG_DEBUG));
        this.processorManager = new DefaultProcessorManager();
        this.requirementBuilder = new DefaultRequirementBuilder();
        this.artifactFacetAdapter = new ArtifactFacetAdapter();
    }

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.parallel = spy(new Parallel());
    }

    @Test
    public void testRequirementMatching() throws Exception {

        // Build processor
        Processor<Content> dummyProcessor = spy(new DummyTxtFileProcessor());
        List<Requirement> requirements = new ArrayList<Requirement>();

        // Wrap
        DelegateHandlerProcessor<Content> delegate = spy(new DelegateHandlerProcessor<>(dummyProcessor, Content.class));

        // Execute only on a content with txt file extension
        requirements.add(requirementBuilder.buildContentRequirement(delegate));
        requirements.add(requirementBuilder.buildArtifactRequirement(delegate).setPathExtension("txt"));

        // artifact URI
        when(artifact.uri()).thenReturn(new URI("file:///local.txt"));

        // Execute at the correct lifecycle
        requirements.add(requirementBuilder.buildPhaseRequirement(delegate, DiscoveryPhasesLifecycle.FACET_SCANNER.toString()));
        delegate.addRequirements(requirements);

        delegate.bindRequirementBuilder(requirementBuilder);
        processorManager.bindProcessor(delegate);
        processorManager.validate();

        // Processors for process phase
        ProcessorJob processorJob = new ProcessorJob(DiscoveryPhasesLifecycle.FACET_SCANNER.toString(), parallel);
        ProviderResource providerResource =  new ProviderResource();

        // Add artifact capability
        ArtifactCapability artifactCapability = artifactFacetAdapter.getCapability(providerResource, artifact);
        providerResource.addCapability(artifactCapability);

        // add a content capability
        ContentCapability contentCapability = new ContentCapabilityImpl(providerResource);
        providerResource.addCapability(contentCapability);

        // Add capabilities of the deployment context
        List<Capability> capabilities = new ArrayList<Capability>();
        capabilities.addAll(providerResource.getCapabilities(null));

        // Add deployment context data
        when(deploymentContext.get(ProcessorManager.class)).thenReturn(processorManager);
        when(deploymentContext.get(Resolver.class)).thenReturn(resolver);
        when(deploymentContext.getCapabilities(null)).thenReturn(capabilities);

        // Facet artifact
        when(facetArtifact.getCapabilities(null)).thenReturn(capabilities);
        when(deploymentContext.get(IFacetArtifact.class)).thenReturn(facetArtifact);

        // content facet
        when(deploymentContext.getArtifact()).thenReturn(artifact);
        when(artifact.as(Content.class)).thenReturn(content);

        // Artifact Content
        when(content.toString()).thenReturn("expected content");

        // Call the Job
        processorJob.execute(deploymentContext);

        // Check that the delegate processor is called once
        verify(parallel).add((Task) anyObject());

        // Now that the add method has been called on the parallel container, execute the job in the parallel
        delegate.handle(deploymentContext);

        // Check that the delegate processor is called with the deployment context
        verify(delegate).handle(deploymentContext);

        // Check that the internal processor is being executed with the expected object
        verify(dummyProcessor).handle(content, null);

    }
}
