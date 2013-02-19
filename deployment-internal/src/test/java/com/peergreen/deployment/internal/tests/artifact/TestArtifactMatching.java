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
package com.peergreen.deployment.internal.tests.artifact;

import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Capability;
import org.osgi.service.resolver.ResolutionException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.internal.artifact.adapter.ArtifactFacetAdapter;
import com.peergreen.deployment.internal.resource.ConsumerResource;
import com.peergreen.deployment.internal.resource.DefaultRequirementBuilder;
import com.peergreen.deployment.internal.resource.ProviderResource;
import com.peergreen.deployment.internal.solver.MissingCapability;
import com.peergreen.deployment.internal.tests.resource.solver.TestMatching;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

public class TestArtifactMatching extends TestMatching {

    @Mock
    private Artifact artifact;

    private ProviderResource provider;

    private ConsumerResource consumer;

    private final FacetCapabilityAdapter<Artifact> facetAdapter;

    private RequirementBuilder requirementBuilder = null;

    public TestArtifactMatching() {
        super();
        this.facetAdapter = new ArtifactFacetAdapter();
        this.requirementBuilder = new DefaultRequirementBuilder();
    }

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.provider = new ProviderResource();
        this.consumer = new ConsumerResource();
    }

    @Test
    public void testMatchNoAttributes() throws ResolutionException, URISyntaxException {

        URI uri = new URI("file:///testMatchNoAttributes");
        when(artifact.name()).thenReturn("testMatchNoAttributes");
        when(artifact.uri()).thenReturn(uri);

        ArtifactCapability capability = facetAdapter.getCapability(provider, artifact);
        provider.addCapability(capability);

        ArtifactRequirement requirement = requirementBuilder.buildArtifactRequirement(consumer);
        consumer.addRequirement(requirement);

        Capability foundCapability =  match(provider, consumer);

        // wire should be our capability
        Assert.assertEquals(foundCapability, capability);
    }


    @Test
    public void testDontMatchExtension() throws ResolutionException, URISyntaxException {

        URI uri = new URI("file:///testNoMatchAttributes.xml");
        when(artifact.name()).thenReturn("testNoMatchAttributes.xml");
        when(artifact.uri()).thenReturn(uri);

        ArtifactCapability capability = facetAdapter.getCapability(provider, artifact);
        provider.addCapability(capability);

        ArtifactRequirement requirement = requirementBuilder.buildArtifactRequirement(consumer).setPathExtension("txt");
        consumer.addRequirement(requirement);

        Capability foundCapability =  match(provider, consumer);
        Assert.assertTrue(foundCapability instanceof MissingCapability);
    }


    @Test
    public void testDontMatchScheme() throws ResolutionException, URISyntaxException {

        URI uri = new URI("file:///testDontMatchScheme.xml");
        when(artifact.name()).thenReturn("testDontMatchScheme.xml");
        when(artifact.uri()).thenReturn(uri);

        ArtifactCapability capability = facetAdapter.getCapability(provider, artifact);
        provider.addCapability(capability);

        ArtifactRequirement requirement = requirementBuilder.buildArtifactRequirement(consumer).setURIScheme("ftp");
        consumer.addRequirement(requirement);

        Capability foundCapability =  match(provider, consumer);
        //shouldn't work
        Assert.assertTrue(foundCapability instanceof MissingCapability);
    }


    @Test
    public void testSchemeAndExtension() throws ResolutionException, URISyntaxException {

        URI uri = new URI("florent:///testSchemeAndExtension.myExtension");
        when(artifact.name()).thenReturn("testSchemeAndExtension.myExtension");
        when(artifact.uri()).thenReturn(uri);

        ArtifactCapability capability = facetAdapter.getCapability(provider, artifact);
        provider.addCapability(capability);

        ArtifactRequirement requirement = requirementBuilder.buildArtifactRequirement(consumer).setURIScheme("florent").setPathExtension("myExtension");
        consumer.addRequirement(requirement);

        Capability foundCapability =  match(provider, consumer);

        // wire capability should be our capability
        Assert.assertEquals(foundCapability, capability);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testTwiceScheme()  {
        requirementBuilder.buildArtifactRequirement(null).setURIScheme("florent").setURIScheme("benoit");
    }


    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testTwicePthExtensions()  {
        requirementBuilder.buildArtifactRequirement(null).setPathExtension("florent").setPathExtension("benoit");
    }

}
