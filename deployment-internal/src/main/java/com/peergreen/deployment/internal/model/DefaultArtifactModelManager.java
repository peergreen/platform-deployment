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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import com.peergreen.deployment.model.ArtifactModelManager;
import com.peergreen.deployment.monitor.URITrackerException;
import com.peergreen.deployment.monitor.URITrackerManager;

@Component
@Provides
@Instantiate(name="Default Artifact Model Manager")
public class DefaultArtifactModelManager implements ArtifactModelManager, InternalArtifactModelManager {

    /**
     * Map between the root artifact URI and the associated artifact model.
     */
    private final Map<URI, InternalArtifactModel> artifactsByURI;

    /**
     * URI tracker.
     */
    @Requires
    private URITrackerManager uriTrackerManager;

    public DefaultArtifactModelManager() {
        this.artifactsByURI = Collections.synchronizedMap(new HashMap<URI, InternalArtifactModel>());
    }

    @Override
    public void addArtifactModel(URI uri, InternalArtifactModel artifactModel) {
        artifactsByURI.put(uri, artifactModel);
    }

    @Override
    public InternalArtifactModel getArtifactModel(URI uri) {
        return artifactsByURI.get(uri);
    }

    public InternalArtifactModel getView(URI uri) {
        return artifactsByURI.get(uri);
    }

    /**
     * @return a snapshot view of the deployed URIs
     */
    @Override
    public Collection<URI> getDeployedRootURIs() {
        List<URI> uris = new ArrayList<URI>();
        // Make a copy in a synchronized block to avoid concurrent modification exceptions
        Set<Entry<URI, InternalArtifactModel>> artifactsEntries;
        synchronized (artifactsByURI) {
            artifactsEntries = new HashSet<>(artifactsByURI.entrySet());
        }
        for (Entry<URI, InternalArtifactModel> entry : artifactsEntries) {
            // Exclude artifacts being un-deployed
            if (entry.getValue().isUndeployed()) {
                continue;
            }
            uris.add(entry.getKey());
        }

        return uris;
    }

    @Override
    public Collection<InternalArtifactModel> getDeployedRootArtifacts() {
        List<InternalArtifactModel> artifactModels = new ArrayList<InternalArtifactModel>();
        // Make a copy in a synchronized block to avoid concurrent modification exceptions
        Set<InternalArtifactModel> artifactModelsSet;
        synchronized (artifactsByURI) {
            artifactModelsSet = new HashSet<>(artifactsByURI.values());
        }
        for (InternalArtifactModel artifactModel : artifactModelsSet) {
            // Exclude artifacts being un-deployed
            if (artifactModel.isUndeployed()) {
                continue;
            }
            artifactModels.add(artifactModel);
        }

        return artifactModels;
    }

    @Override
    public void updateLengthLastModified(InternalArtifactModel artifactModel) {
        URI uri = artifactModel.getFacetArtifact().uri();
        try {
            artifactModel.setLastModified(uriTrackerManager.getLastModified(uri));
        } catch (URITrackerException e) {
            // Unable to read
            artifactModel.setLastModified(-1);
        }

        try {
            artifactModel.setArtifactLength(uriTrackerManager.getLength(uri));
        } catch (URITrackerException e) {
            // Unable to read
            artifactModel.setArtifactLength(-1);
        }

    }




}
