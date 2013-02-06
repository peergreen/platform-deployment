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

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import com.peergreen.deployment.monitor.URITracker;
import com.peergreen.deployment.monitor.URITrackerException;

/**
 * Checks URI that are not local.
 * @author Florent Benoit
 */
@Component
@Provides(properties={@StaticServiceProperty(name="scheme", type="java.lang.String", value="*")})
@Instantiate(name="URI:// change tracker")
public class DefaultURITracker implements URITracker {


    @Override
    public long getLastModified(final URI uri) throws URITrackerException {
        return getConnection(uri).getLastModified();
    }

    @Override
    public long getLength(final URI uri) throws URITrackerException {
        return getConnection(uri).getContentLength();
    }

    @Override
    public boolean exists(final URI uri)  {
        try {
            getConnection(uri);
            return true;
        } catch (URITrackerException e) {
            return false;
        }
    }


    protected URLConnection getConnection(URI uri) throws URITrackerException {
        try {
            return uri.toURL().openConnection();
        } catch (IOException e) {
            throw new URITrackerException("Unable to open a connection on URI '" + uri + "'.", e);
        }
    }

}
