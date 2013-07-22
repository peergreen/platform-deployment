/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.model;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import java.net.URI;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.BundleArtifactManager;

/**
 * Tests the bundle artifact manager.
 * @author Florent Benoit
 */
public class TestBundleArtifactManager {


   private BundleArtifactManager bundleArtifactManager;

   @Mock
   private Bundle bundle;

   @Mock
   private InternalArtifactModel artifactModel;

   @Mock
   private IFacetArtifact artifact;

   @Mock
   private BundleContext bundleContext;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DefaultArtifactModelManager artifactModelManager = new DefaultArtifactModelManager(bundleContext);
        this.bundleArtifactManager = new DefaultBundleArtifactManager(artifactModelManager);

        // Adds an artifact with a bundle
        artifactModelManager.addArtifactModel(new URI("file:///dummy"), artifactModel);
        doReturn(artifact).when(artifactModel).getFacetArtifact();
        doReturn(bundle).when(artifact).as(Bundle.class);
    }

    @Test
    public void testFindArtifactModel() {
        ArtifactModel foundArtifactModel = this.bundleArtifactManager.getArtifactModel(bundle);
        assertNotNull(foundArtifactModel);
        assertSame(foundArtifactModel, artifactModel);
    }

    @Test
    public void testFindArtifact() {
        Artifact foundArtifact = this.bundleArtifactManager.getArtifact(bundle);
        assertNotNull(foundArtifact);
        assertSame(foundArtifact, artifact);
    }


}
