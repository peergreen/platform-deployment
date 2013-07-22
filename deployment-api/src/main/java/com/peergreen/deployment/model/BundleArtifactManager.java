/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.model;

import org.osgi.framework.Bundle;

import com.peergreen.deployment.Artifact;

/**
 * This manager allows to get artifact from a bundle.
 * @author Florent Benoit
 */
public interface BundleArtifactManager {

    /**
     * Gets artifact for a given bundle.
     * @param bundle the given bundle
     * @return the artifact associated to the given bundle
     */
    Artifact getArtifact(Bundle bundle);

    /**
     * Gets artifact for a given bundle.
     * @param bundle the given bundle
     * @return the artifact associated to the given bundle
     */
    ArtifactModel getArtifactModel(Bundle bundle);
}
