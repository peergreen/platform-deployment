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
package com.peergreen.deployment.internal.model;

import static org.mockito.Mockito.when;

import java.net.URI;

import org.apache.felix.resolver.ResolverImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DelegateFacetBuilder;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.facet.FacetCapabilityImpl;
import com.peergreen.deployment.internal.facet.FacetRequirementImpl;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelPersistenceView;

/**
 * Test if facet builder can restore facets.
 * @author Florent Benoit
 */
public class FacetBuilderTestCase {


    @Mock
    private Artifact artifact;

    private FacetArtifact facetArtifact;

    @Mock
    private BundleContext bundleContext;

    private DefaultArtifactModelManager manager;


    private DefaultArtifactModel artifactModel;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.manager = new DefaultArtifactModelManager(bundleContext);
        this.manager.bindResolver(new ResolverImpl(null));
        URI uri = new URI("test:mock");
        when(artifact.uri()).thenReturn(uri);
        when(artifact.name()).thenReturn("mock");
        facetArtifact = new FacetArtifact(artifact);
        artifactModel = new DefaultArtifactModel(facetArtifact);
        manager.addArtifactModel(uri, artifactModel);

    }


    @Test
    public void testFacetBuilderNotPersistent() throws Exception {
        DefaultFacetBuilderInfo dummyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        dummyFacetBuilderInfo.setName(DummyFacetBuilder.class.getName());
        dummyFacetBuilderInfo.setProvides(DummyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(dummyFacetBuilderInfo);

        // Check artifact doesn't contains the facet
        Assert.assertNull(artifact.as(DummyFacet.class));

        // Init manager
        manager.init();

        // Now, bind the builder
        manager.bindInternalFacetBuilder(new DummyDelegateFacetBuilder());

        // Check artifact does not contain the facet (as it's not persistent)
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
    }


    @Test
    public void testFacetBuilderNoDepedency() throws Exception {
        DefaultFacetBuilderInfo dummyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        dummyFacetBuilderInfo.setName(DummyFacetBuilder.class.getName());
        dummyFacetBuilderInfo.setProvides(DummyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(dummyFacetBuilderInfo);

        // persistent
        artifactModel.as(InternalArtifactModelPersistenceView.class).setPersistent(true);

        // Check artifact doesn't contains the facet
        Assert.assertNull(artifact.as(DummyFacet.class));

        // Init manager
        manager.init();

        // Now, bind the builder
        manager.bindInternalFacetBuilder(new DummyDelegateFacetBuilder());

        // Check artifact does contains the facet
        Assert.assertNotNull(facetArtifact.as(DummyFacet.class));
    }


    @Test
    public void testFacetBuilderWithDepedency() throws Exception {
        DefaultFacetBuilderInfo dummyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        dummyFacetBuilderInfo.setName(DummyFacetBuilder.class.getName());
        dummyFacetBuilderInfo.setProvides(DummyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(dummyFacetBuilderInfo);

        DefaultFacetBuilderInfo dummyDependencyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        dummyDependencyFacetBuilderInfo.setName(DummyDependencyFacetBuilder.class.getName());
        dummyDependencyFacetBuilderInfo.setProvides(DummyDependencyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(dummyDependencyFacetBuilderInfo);

        // persistent
        artifactModel.as(InternalArtifactModelPersistenceView.class).setPersistent(true);

        // Check artifact doesn't contains the facet
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNull(facetArtifact.as(DummyDependencyFacet.class));

        // Now, bind the builder
        manager.bindInternalFacetBuilder(new DummyDependencyDelegateFacetBuilder());

        // This time, init the manager after
        manager.init();

        // Check artifact still doesn't contains the facet (as dependency builder is not here)
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNull(facetArtifact.as(DummyDependencyFacet.class));

        // Now, bind the new builder
        manager.bindInternalFacetBuilder(new DummyDelegateFacetBuilder());

        // Check artifact does contains the two facets
        Assert.assertNotNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNotNull(facetArtifact.as(DummyDependencyFacet.class));
    }


    @Test
    public void testFacetBuilderWithCycleDepedency() throws Exception {
        // persistent
        artifactModel.as(InternalArtifactModelPersistenceView.class).setPersistent(true);

        DefaultFacetBuilderInfo cycle1DependencyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        cycle1DependencyFacetBuilderInfo.setName(Cycle1DependencyFacetBuilder.class.getName());
        cycle1DependencyFacetBuilderInfo.setProvides(DummyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(cycle1DependencyFacetBuilderInfo);

        DefaultFacetBuilderInfo cycle2DependencyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        cycle2DependencyFacetBuilderInfo.setName(Cycle2DependencyFacetBuilder.class.getName());
        cycle2DependencyFacetBuilderInfo.setProvides(DummyDependencyFacet.class.getName());
        facetArtifact.getFacetBuilders().add(cycle2DependencyFacetBuilderInfo);


        // Check artifact doesn't contains the facet
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNull(facetArtifact.as(DummyDependencyFacet.class));


        // Now, bind one of the builders
        manager.bindInternalFacetBuilder(new Cycle1DependencyDelegateFacetBuilder());

        // still null
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNull(facetArtifact.as(DummyDependencyFacet.class));

        // This time, init the manager after
        manager.init();

        // still null
        Assert.assertNull(facetArtifact.as(DummyFacet.class));
        Assert.assertNull(facetArtifact.as(DummyDependencyFacet.class));

        // no exception
        Assert.assertNull(cycle1DependencyFacetBuilderInfo.getThrowable());
        Assert.assertNull(cycle1DependencyFacetBuilderInfo.getThrowable());

        // Now, bind the new builder (with a cycle)
        manager.bindInternalFacetBuilder(new Cycle2DependencyDelegateFacetBuilder());

        // Check exception
        Assert.assertNotNull(cycle1DependencyFacetBuilderInfo.getThrowable());
        Assert.assertTrue(cycle1DependencyFacetBuilderInfo.getThrowable().getMessage().contains("Cycle"));
    }


    static class DummyFacet {
    }

    static class DummyDependencyFacet {
    }


    static class DummyDelegateFacetBuilder extends DelegateFacetBuilder<DummyFacet> {

        public DummyDelegateFacetBuilder() {
            super(new DummyFacetBuilder(), DummyFacet.class);
            addCapability(new FacetCapabilityImpl(this, DummyFacet.class));
        }

    }

    static class DummyFacetBuilder implements FacetBuilder<DummyFacet> {

        @Override
        public void build(BuilderContext<DummyFacet> context) throws FacetBuilderException {
            context.addFacet(new DummyFacet());
        }
    }


    static class DummyDependencyDelegateFacetBuilder extends DelegateFacetBuilder<DummyDependencyFacet> {

        public DummyDependencyDelegateFacetBuilder() {
            super(new DummyDependencyFacetBuilder(), DummyDependencyFacet.class);
            addCapability(new FacetCapabilityImpl(this,DummyDependencyFacet.class));
            addRequirement(new FacetRequirementImpl(this, DummyFacet.class));
        }

    }

    static class DummyDependencyFacetBuilder implements FacetBuilder<DummyDependencyFacet> {

        @Override
        public void build(BuilderContext<DummyDependencyFacet> context) throws FacetBuilderException {

            // get Dummy Facet
            DummyFacet dummyFacet = context.getArtifact().as(DummyFacet.class);
            if (dummyFacet != null) {
                context.addFacet(new DummyDependencyFacet());
                return;
            }
            throw new FacetBuilderException("Cannot build the facet as dependency is not found");
        }
    }



    static class Cycle1DependencyDelegateFacetBuilder extends DelegateFacetBuilder<DummyFacet> {

        public Cycle1DependencyDelegateFacetBuilder() {
            super(new Cycle1DependencyFacetBuilder(), DummyFacet.class);
            addCapability(new FacetCapabilityImpl(this,DummyFacet.class));
            addRequirement(new FacetRequirementImpl(this, DummyDependencyFacet.class));
        }

    }

    static class Cycle1DependencyFacetBuilder implements FacetBuilder<DummyFacet> {

        @Override
        public void build(BuilderContext<DummyFacet> context) throws FacetBuilderException {

            // get Dummy Facet
            DummyDependencyFacet dummyDependencyFacet = context.getArtifact().as(DummyDependencyFacet.class);
            if (dummyDependencyFacet != null) {
                context.addFacet(new DummyFacet());
                return;
            }
            throw new FacetBuilderException("Cannot build the facet as dependency is not found");
        }
    }

    static class Cycle2DependencyDelegateFacetBuilder extends DelegateFacetBuilder<DummyDependencyFacet> {

        public Cycle2DependencyDelegateFacetBuilder() {
            super(new Cycle2DependencyFacetBuilder(), DummyDependencyFacet.class);
            addCapability(new FacetCapabilityImpl(this, DummyDependencyFacet.class));
            addRequirement(new FacetRequirementImpl(this, DummyFacet.class));
        }

    }

    static class Cycle2DependencyFacetBuilder implements FacetBuilder<DummyDependencyFacet> {

        @Override
        public void build(BuilderContext<DummyDependencyFacet> context) throws FacetBuilderException {

            // get Dummy Facet
            DummyDependencyFacet dummyDependencyFacet = context.getArtifact().as(DummyDependencyFacet.class);
            if (dummyDependencyFacet != null) {
                context.addFacet(new DummyDependencyFacet());
                return;
            }
            throw new FacetBuilderException("Cannot build the facet as dependency is not found");
        }
    }

}
