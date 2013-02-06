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
package com.peergreen.deployment.internal.monitor;

import java.net.URI;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import com.peergreen.deployment.monitor.URITracker;
import com.peergreen.deployment.monitor.URITrackerException;
import com.peergreen.deployment.monitor.URITrackerManager;

/**
 * Checks the given URI and try to call the expected scheme URI tracker.
 * @author Florent Benoit
 */
@Component
@Provides
@Instantiate(name="URI:// tracker manager")
public class DefaultURITrackerManager implements URITrackerManager {

    private static final String DEFAULT_SCHEME = "*";

    private final Map<String, URITracker> uriTrackers;

    public DefaultURITrackerManager() {
        this.uriTrackers = new HashMap<String, URITracker>();

    }

    @Override
    public long getLastModified(URI uri) throws URITrackerException {
        return getTracker(uri).getLastModified(uri);
    }

    @Override
    public long getLength(URI uri) throws URITrackerException {
        return getTracker(uri).getLength(uri);
    }

    @Override
    public boolean exists(URI uri) throws URITrackerException {
        return getTracker(uri).exists(uri);
    }


    protected URITracker getTracker(URI uri) throws URITrackerException {
        String scheme = uri.getScheme();
        URITracker uriTracker = this.uriTrackers.get(scheme);
        // try with default
        if (uriTracker == null) {
            uriTracker = this.uriTrackers.get(DEFAULT_SCHEME);
            if (uriTracker == null) {
                throw new URITrackerException("Unable to get default scheme for URI '" + uri + "'.");
            }
        }
        return uriTracker;
    }


    @Bind(aggregate=true)
    public void bindURITracker(URITracker uriTracker, Dictionary<String, String> dictionary) {
        String scheme = dictionary.get("scheme");
        if (scheme != null) {
            this.uriTrackers.put(scheme, uriTracker);
        }
    }

    @Unbind(aggregate=true)
    public void unbindURITracker(URITracker uriTracker, Dictionary<String, String> dictionary) {
        String scheme = dictionary.get("scheme");
        if (scheme != null) {
            this.uriTrackers.remove(scheme);
        }
    }


}
