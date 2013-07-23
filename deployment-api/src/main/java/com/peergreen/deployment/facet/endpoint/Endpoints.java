/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.facet.endpoint;

import java.net.URI;
import java.util.List;

/**
 * Allows to declare and list endpoint that can be associated to an artifact
 * @author Florent Benoit
 */
public interface Endpoints {

    /**
     * Register an URI Endpoint with the optional categories
     * @param uri the given URI
     * @param categories the optional categories for the given Endpoint
     */
    void register(URI uri, String... categories);

    /**
     * Gets all endpoints that are registered and matching the given categories
     * @param categories the matching categories
     * @return list of URI matching the given categories
     */
    List<Endpoint> list(String... categories);


    /**
     * Remove the given endpoint URI
     * @param uri the URI to remove
     */
    void remove(URI uri);

}
