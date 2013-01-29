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
package com.peergreen.deployment.internal.artifact.adapter;

import org.osgi.resource.Resource;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.internal.artifact.resource.ArtifactCapabilityImpl;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;

public class ArtifactFacetAdapter implements FacetCapabilityAdapter<Artifact> {

    @Override
    public ArtifactCapability getCapability(Resource resource, Artifact artifact) {

        // Defines artifact with the URI scheme
        ArtifactCapability artifactCapability = new ArtifactCapabilityImpl(resource).setURIScheme(artifact.uri().getScheme());

        String name = artifact.uri().getPath();
        if (name != null) {
            String pathExtension = name;
            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                pathExtension = name.substring(dot + 1);
            }

            // extension
            artifactCapability.setPathExtension(pathExtension);
        }

        return artifactCapability;
    }

}
