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
        artifactModel.setPersistent(true);

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
        artifactModel.setPersistent(true);

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


    //@Test
    public void testFacetBuilderWithCycleDepedency() throws Exception {
        DefaultFacetBuilderInfo dummyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        dummyFacetBuilderInfo.setName(DummyFacetBuilder.class.getName());
        facetArtifact.getFacetBuilders().add(dummyFacetBuilderInfo);

        DefaultFacetBuilderInfo cycleDependencyFacetBuilderInfo = new DefaultFacetBuilderInfo();
        cycleDependencyFacetBuilderInfo.setName(CycleDependencyFacetBuilder.class.getName());
        facetArtifact.getFacetBuilders().add(cycleDependencyFacetBuilderInfo);

        // Check artifact doesn't contains the facet
        Assert.assertNull(facetArtifact.as(DummyFacet.class));

        // Now, bind the builder
        manager.bindInternalFacetBuilder(new DummyDependencyDelegateFacetBuilder());

        // This time, init the manager after
        manager.init();

        // Check artifact still doesn't contains the facet (as dependency builder is not here)
        Assert.assertNull(facetArtifact.as(DummyFacet.class));

        // Now, bind the new builder (with a cycle)
        manager.bindInternalFacetBuilder(new CycleDependencyDelegateFacetBuilder());

        //FIXME : should check the exception in the facet artifact
    }


    class DummyFacet {
    }

    class DummyDependencyFacet {
    }


    class DummyDelegateFacetBuilder extends DelegateFacetBuilder<DummyFacet> {

        public DummyDelegateFacetBuilder() {
            super(new DummyFacetBuilder(), DummyFacet.class);
            addCapability(new FacetCapabilityImpl(this, DummyFacet.class));
        }

    }

    class DummyFacetBuilder implements FacetBuilder<DummyFacet> {

        @Override
        public void build(BuilderContext<DummyFacet> context) throws FacetBuilderException {
            context.addFacet(new DummyFacet());
        }
    }


    class DummyDependencyDelegateFacetBuilder extends DelegateFacetBuilder<DummyDependencyFacet> {

        public DummyDependencyDelegateFacetBuilder() {
            super(new DummyDependencyFacetBuilder(), DummyDependencyFacet.class);
            addCapability(new FacetCapabilityImpl(this,DummyDependencyFacet.class));
            addRequirement(new FacetRequirementImpl(this, DummyFacet.class));
        }

    }

    class DummyDependencyFacetBuilder implements FacetBuilder<DummyDependencyFacet> {

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




    class CycleDependencyDelegateFacetBuilder extends DelegateFacetBuilder<DummyFacet> {

        public CycleDependencyDelegateFacetBuilder() {
            super(new CycleDependencyFacetBuilder(), DummyFacet.class);
            addCapability(new FacetCapabilityImpl(this,DummyFacet.class));
            addRequirement(new FacetRequirementImpl(this, DummyDependencyFacet.class));
        }

    }

    class CycleDependencyFacetBuilder implements FacetBuilder<DummyFacet> {

        @Override
        public void build(BuilderContext<DummyFacet> context) throws FacetBuilderException {

            // get Dummy Facet
            DummyFacet dummyFacet = context.getArtifact().as(DummyFacet.class);
            if (dummyFacet != null) {
                context.addFacet(new DummyFacet());
                return;
            }
            throw new FacetBuilderException("Cannot build the facet as dependency is not found");
        }
    }

}
