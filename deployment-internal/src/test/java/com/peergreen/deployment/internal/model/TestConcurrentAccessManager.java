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

import java.net.URI;
import java.util.Collection;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;

/**
 * Try to concurrently access to the manager
 * @author Florent Benoit
 */
public class TestConcurrentAccessManager {

    @Mock
    private BundleContext bundleContext;

    private DefaultArtifactModelManager manager;

    @BeforeClass
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.manager = new DefaultArtifactModelManager(bundleContext);
    }


    @Test(threadPoolSize = 30, invocationCount = 300)
    public void testAccess() throws Exception {
        Collection<URI> uris = manager.getDeployedRootURIs();
        for (URI uri : uris) {
            uri.toString();
        }
        String id = UUID.randomUUID().toString();
        URI uri = new URI("test:" + id);
        Artifact artifact = new ImmutableArtifact(id, uri);
        IFacetArtifact facetArtifact = new FacetArtifact(artifact);
        InternalArtifactModel buildModel = new DefaultArtifactModel(facetArtifact);
        manager.addArtifactModel(uri, buildModel);

        Collection<InternalArtifactModel> models = manager.getDeployedRootArtifacts();
        for (InternalArtifactModel model : models) {
            model.toString();
        }

        InternalArtifactModel foundModel = manager.getArtifactModel(uri);
        Assert.assertEquals(foundModel, buildModel);

    }
}
