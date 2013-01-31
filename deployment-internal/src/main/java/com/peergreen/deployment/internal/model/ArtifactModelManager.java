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
package com.peergreen.deployment.internal.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ArtifactModelManager {

    /**
     * Map between the root artifact URI and the associated artifact model.
     */
    private final Map<URI, DefaultArtifactModel> artifactsByURI;

    public ArtifactModelManager() {
        this.artifactsByURI = new HashMap<URI, DefaultArtifactModel>();
    }

    public void addArtifactModel(URI uri, DefaultArtifactModel artifactModel) {
        artifactsByURI.put(uri, artifactModel);
    }


    public DefaultArtifactModel getArtifactModel(URI uri) {
        return artifactsByURI.get(uri);
    }

    public InternalArtifactModel getView(URI uri) {
        return artifactsByURI.get(uri);
    }

    public Collection<URI> getDeployedRootURIs() {
        List<URI> uris = new ArrayList<URI>();
        Set<Entry<URI, DefaultArtifactModel>> artifactsEntries = artifactsByURI.entrySet();
        Iterator<Entry<URI, DefaultArtifactModel>> iterator = artifactsEntries.iterator();
        while (iterator.hasNext()) {
            Entry<URI, DefaultArtifactModel> entry = iterator.next();
            DefaultArtifactModel artifactModel = entry.getValue();
            if (artifactModel.isUndeployed()) {
                continue;
            }
            uris.add(entry.getKey());

        }

        return uris;
    }

}
