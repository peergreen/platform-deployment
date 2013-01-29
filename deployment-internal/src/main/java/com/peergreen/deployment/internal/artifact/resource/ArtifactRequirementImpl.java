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
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

import com.peergreen.deployment.internal.resource.AbsDeploymentRequirement;
import com.peergreen.deployment.resource.artifact.ArtifactNamespace;
import com.peergreen.deployment.resource.artifact.ArtifactRequirement;

public class ArtifactRequirementImpl extends AbsDeploymentRequirement implements ArtifactRequirement {

    private String pathExtension;
    private String scheme;

    public ArtifactRequirementImpl(Resource resource) {
        super(resource, ArtifactNamespace.ARTIFACT_NAMESPACE);
    }

    @Override
    protected IFilter computeFilter(IFilter filter) {
        if (pathExtension != null) {
            return Filters.and(filter, Filters.equal(ArtifactNamespace.CAPABILITY_PATHEXTENSION_ATTRIBUTE, pathExtension));
        }
        if (scheme != null) {
            return Filters.and(filter, Filters.equal(ArtifactNamespace.CAPABILITY_SCHEME_ATTRIBUTE, scheme));
        }
        return filter;
    }

    @Override
    public <T extends ArtifactRequirement> T setPathExtension(String pathExtension) {
        if (this.pathExtension != null) {
            throw new IllegalArgumentException("Path extension already set");
        }
        this.pathExtension = pathExtension;
        recomputeFilterValue();
        return (T) this;
    }

    @Override
    public <T extends ArtifactRequirement> T setURIScheme(String scheme) {
        if (this.scheme != null) {
            throw new IllegalArgumentException("scheme already set");
        }
        this.scheme = scheme;
        recomputeFilterValue();
        return (T) this;
    }
}
