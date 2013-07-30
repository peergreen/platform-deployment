/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.processor.uriresolver;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.deployment.processor.Uri;

/**
 * bundleresource:// URI processor
 * @author Florent Benoit
 */
@Processor
@Uri("bundleresource")
@Discovery(DiscoveryPhasesLifecycle.URI_RESOLVER)
public class BundleResourceURIProcessor extends AbstractURIProcessor {

}
