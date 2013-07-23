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

import java.util.Collection;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.framework.Bundle;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.model.BundleArtifactManager;

/**
 * Adds operations that are specific to the bundles
 * @author Florent Benoit
 */
@Component
@Provides
@Instantiate
public class DefaultBundleArtifactManager implements BundleArtifactManager {

    private final InternalArtifactModelManager internalArtifactModelManager;

    public DefaultBundleArtifactManager(@Requires InternalArtifactModelManager internalArtifactModelManager) {
        this.internalArtifactModelManager = internalArtifactModelManager;
    }


    /**
     * Gets artifact for a given bundle.
     * @param bundle the given bundle
     * @return the artifact associated to the given bundle
     */
    @Override
    public IFacetArtifact getArtifact(Bundle bundle) {
        InternalArtifactModel artifactModel = getArtifactModel(bundle);
        if (artifactModel != null) {
            return artifactModel.getFacetArtifact();
        }
        return null;
    }

    /**
     * Gets artifact for a given bundle.
     * @param bundle the given bundle
     * @return the artifact associated to the given bundle
     */
    @Override
    public InternalArtifactModel getArtifactModel(Bundle bundle) {
        // Search all artifacts
        Collection<InternalArtifactModel> collection = internalArtifactModelManager.getArtifacts(new AssociatedBundleFilter(bundle));

        // only one match
        if (collection.size() == 1) {
            return collection.iterator().next();
        }
        return null;
    }
}

/**
 * Match if the bundle in the artifact is the same than the wanted bundle
 * @author Florent Benoit
 */
class AssociatedBundleFilter implements ArtifactModelFilter {

    private final Bundle bundle;

    public AssociatedBundleFilter(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public boolean accept(InternalArtifactModel model) {
        return bundle.equals(model.getFacetArtifact().as(Bundle.class));
    }

}
