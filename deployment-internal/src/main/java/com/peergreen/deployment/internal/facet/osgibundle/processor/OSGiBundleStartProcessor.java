/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.facet.osgibundle.processor;

import static org.osgi.framework.Bundle.START_TRANSIENT;
import static org.osgi.framework.Constants.FRAGMENT_HOST;

import java.util.Collections;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;

/**
 * Start the OSGi bundles on the gateway.
 * @author Florent Benoit
 */
@Processor
@Phase("START")
public class OSGiBundleStartProcessor {

    public void handle(Bundle bundle, ProcessorContext processorContext) throws ProcessorException {

        // Persistent mode ?
        ArtifactModel artifactModel = processorContext.getArtifactModel();
        ArtifactModelPersistenceView artifactModelPersistenceView = artifactModel.as(ArtifactModelPersistenceView.class);

        // For bundles that are not persistent, set it as transient
        boolean isTransient = true;
        if (artifactModelPersistenceView.isPersistent()) {
            isTransient = false;
        }

        // Start the bundle if it is not a fragment
        if (!isFragment(bundle)) {
            try {
                if (isTransient) {
                    bundle.start(START_TRANSIENT);
                } else {
                    bundle.start();
                }
            } catch (BundleException e) {
                throw new ProcessorException("Unable to start the bundle", e);
            }
        } else {
            // Resolve the bundle
            FrameworkWiring frameworkWiring = bundle.getBundleContext().getBundle(0).adapt(FrameworkWiring.class);
            frameworkWiring.resolveBundles(Collections.singleton(bundle));
        }
    }

    /**
     * Checks if the given bundle is a fragment
     * @param bundle the bundle to check
     * @return true if the bundle is a fragment.
     */
    protected boolean isFragment(final Bundle bundle) {
        return bundle.getHeaders().get(FRAGMENT_HOST) != null;
    }

}
