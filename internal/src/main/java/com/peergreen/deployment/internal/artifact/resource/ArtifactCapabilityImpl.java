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
package com.peergreen.deployment.internal.artifact.resource;

import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.resource.AbsDeploymentCapability;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.artifact.ArtifactNamespace;

public class ArtifactCapabilityImpl extends AbsDeploymentCapability implements ArtifactCapability {

    public ArtifactCapabilityImpl(Resource resource) {
        super(resource, ArtifactNamespace.ARTIFACT_NAMESPACE, "");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ArtifactCapability> T setPathExtension(String fileExtension) {

        String existingPathExtension = (String) getAttributes().get(ArtifactNamespace.CAPABILITY_PATHEXTENSION_ATTRIBUTE);
        if (existingPathExtension != null) {
            throw new IllegalArgumentException("File extension already set");
        }
        addAttribute(ArtifactNamespace.CAPABILITY_PATHEXTENSION_ATTRIBUTE, fileExtension);
        return (T) this;
    }

    @Override
    public <T extends ArtifactCapability> T setURIScheme(String scheme) {
        String existingScheme = (String) getAttributes().get(ArtifactNamespace.CAPABILITY_SCHEME_ATTRIBUTE);
        if (existingScheme != null) {
            throw new IllegalArgumentException("URI scheme already set");
        }
        addAttribute(ArtifactNamespace.CAPABILITY_SCHEME_ATTRIBUTE, scheme);
        return (T) this;
    }

}
