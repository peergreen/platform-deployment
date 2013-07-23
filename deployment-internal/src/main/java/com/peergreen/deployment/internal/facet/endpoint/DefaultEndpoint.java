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
import java.util.Collections;
import java.util.List;

import com.peergreen.deployment.facet.endpoint.Endpoint;


/**
 * Defines an endpoint
 * @author Florent Benoit
 */
public class DefaultEndpoint implements Endpoint {

    private final URI uri;

    private final List<String> categories;

    public DefaultEndpoint(URI uri) {
        this(uri, Collections.<String> emptyList());
    }

    public DefaultEndpoint(URI uri, List<String> categories) {
        this.uri = uri;
        this.categories = new ArrayList<>(categories);
    }


    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public List<String> getCategories() {
        return categories;
    }


}