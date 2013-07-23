/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.facet.endpoint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.peergreen.deployment.facet.endpoint.Endpoint;
import com.peergreen.deployment.facet.endpoint.Endpoints;


/**
 * Allows to declare and list endpoint that can be associated to an artifact
 * @author Florent Benoit
 */
public class DefaultEndpoints implements Endpoints {

    private final List<Endpoint> endpoints;

    public DefaultEndpoints() {
        this.endpoints = new ArrayList<>();
    }


    /**
     * Register an URI Endpoint with the optional categories
     * @param uri the given URI
     * @param categories the optional categories for the given Endpoint
     */
    @Override
    public void register(URI uri, String... categories) {
        this.endpoints.add(new DefaultEndpoint(uri, Arrays.asList(categories)));
    }

    /**
     * Gets all endpoints that are registered and matching the given categories
     * @param categories the matching categories
     * @return list of URI matching the given categories
     */
    @Override
    public List<Endpoint> list(String... categories) {
        List<Endpoint> matchingEnndpoints = new ArrayList<>();
        List<String> wantedCategories = Arrays.asList(categories);
        for (Endpoint endpoint : endpoints) {
            List<String> foundCategories = endpoint.getCategories();
            if (foundCategories.containsAll(wantedCategories)) {
                matchingEnndpoints.add(endpoint);
            }
        }
        return matchingEnndpoints;
    }

    /**
     * Remove the given endpoint URI
     * @param uri the URI to remove
     */
    @Override
    public void remove(URI uri) {
        Iterator<Endpoint> iterator = this.endpoints.iterator();
        while (iterator.hasNext()) {
            Endpoint endpoint = iterator.next();
            if (endpoint.getURI().equals(uri)) {
                iterator.remove();
            }
        }
    }

}