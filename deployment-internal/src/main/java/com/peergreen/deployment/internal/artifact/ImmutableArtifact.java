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
package com.peergreen.deployment.internal.artifact;

import java.net.URI;

import com.peergreen.deployment.Artifact;

/**
 * Read-only implementation of an artifact.
 * This is returned for example to the clients wanting to send artifacts to the deployment service.
 * @author Florent Benoit
 */
public class ImmutableArtifact implements Artifact {

    private final String name;
    private final URI uri;
    private final String toString;

    public ImmutableArtifact(String name, URI uri) {
        this.name = name;
        this.uri = uri;
        this.toString = new StringBuilder("Artifact[name=").append(name).append(", uri=").append(uri.toString()).append("]").toString();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        // Not implemented as it's a read only implementation
        return null;
    }


    @Override
    public String toString() {
     return toString;
    }

}
