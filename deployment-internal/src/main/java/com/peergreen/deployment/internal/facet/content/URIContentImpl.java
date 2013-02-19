/**
 * Copyright 2012 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.facet.content;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import com.peergreen.deployment.facet.FacetBuilderReference;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;
import com.peergreen.deployment.internal.facet.content.builder.UriContentFacetBuilder;

@FacetBuilderReference(UriContentFacetBuilder.class)
public class URIContentImpl implements Content {

    private final URI uri;

    public URIContentImpl(URI uri) {
        this.uri = uri;
    }

    @Override
    public InputStream getInputStream() throws ContentException {

        URLConnection urlConnection;
        try {
            urlConnection = uri.toURL().openConnection();
            urlConnection.setDefaultUseCaches(false);
            return urlConnection.getInputStream();
        } catch (IOException e) {
            throw new ContentException("Unable to get inputstream from uri '" + uri + "'.", e);
        }
    }

    @Override
    public String getName() {
        return uri.getPath();
    }

}
