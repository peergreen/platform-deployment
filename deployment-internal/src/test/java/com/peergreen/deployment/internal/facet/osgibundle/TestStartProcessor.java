/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.facet.osgibundle;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.osgi.framework.Bundle.START_TRANSIENT;
import static org.osgi.framework.Constants.FRAGMENT_HOST;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.internal.facet.osgibundle.processor.OSGiBundleStartProcessor;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;

/**
 * Test of the Start processor
 * @author Florent Benoit
 */
public class TestStartProcessor {

    @Mock
    private Bundle bundle;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle systemBundle;


    @Mock
    private ProcessorContext processorContext;

    @Mock
    private ArtifactModel artifactModel;

    @Mock
    private ArtifactModelPersistenceView artifactModelPersistenceView;

    @Mock
    private FrameworkWiring frameworkWiring;

    private Dictionary<String, String> headers;


    private OSGiBundleStartProcessor bundleStartProcessor;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);

        this.bundleStartProcessor = new OSGiBundleStartProcessor();
        headers = new Hashtable<>();

        doReturn(artifactModel).when(processorContext).getArtifactModel();
        doReturn(artifactModelPersistenceView).when(artifactModel).as(ArtifactModelPersistenceView.class);
        doReturn(headers).when(bundle).getHeaders();

        doReturn(bundleContext).when(bundle).getBundleContext();
        doReturn(systemBundle).when(bundleContext).getBundle(0);
        doReturn(frameworkWiring).when(systemBundle).adapt(FrameworkWiring.class);

    }




    @Test
    public void testNotPersistentBundle() throws ProcessorException, BundleException {
        doReturn(false).when(artifactModelPersistenceView).isPersistent();

        bundleStartProcessor.handle(bundle, processorContext);

        verify(bundle).start(START_TRANSIENT);
    }

    @Test
    public void testPersistentBundle() throws ProcessorException, BundleException {
        doReturn(true).when(artifactModelPersistenceView).isPersistent();

        bundleStartProcessor.handle(bundle, processorContext);

        verify(bundle).start();

    }


    @Test
    public void testFragmentBundle() throws ProcessorException {
        headers.put(FRAGMENT_HOST, "test-host");

        bundleStartProcessor.handle(bundle, processorContext);

        // check bundle has been resolved
        verify(frameworkWiring).resolveBundles(Collections.singleton(bundle));
    }


}
