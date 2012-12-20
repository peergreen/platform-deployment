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
package com.peergreen.deployment.internal.resource;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.artifact.resource.ArtifactRequirementImpl;
import com.peergreen.deployment.internal.facet.FacetRequirementImpl;
import com.peergreen.deployment.internal.facet.archive.resource.ArchiveRequirementImpl;
import com.peergreen.deployment.internal.facet.content.resource.ContentRequirementImpl;
import com.peergreen.deployment.internal.facet.xmlcontent.resource.XMLContentRequirementImpl;
import com.peergreen.deployment.internal.phase.resource.PhaseRequirementImpl;
import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;
import com.peergreen.deployment.resource.artifact.content.ContentRequirement;
import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;
import com.peergreen.deployment.resource.builder.RequirementBuilder;
import com.peergreen.deployment.resource.facet.FacetRequirement;
import com.peergreen.deployment.resource.phase.PhaseRequirement;

@Component
@Provides
@Instantiate(name="Requirement Builder")
public class DefaultRequirementBuilder implements RequirementBuilder {

    @Override
    public ContentRequirement buildContentRequirement(Resource resource) {
       return new ContentRequirementImpl(resource);
    }

    @Override
    public PhaseRequirement buildPhaseRequirement(Resource resource, String phaseName) {
        return new PhaseRequirementImpl(resource, phaseName);
    }

    @Override
    public XMLContentRequirement buildXMLContentRequirement(Resource resource) {
        return new XMLContentRequirementImpl(resource);
    }

    @Override
    public ArtifactRequirement buildArtifactRequirement(Resource resource) {
        return new ArtifactRequirementImpl(resource);
    }

    @Override
    public ArchiveRequirement buildArchiveRequirement(Resource resource) {
        return new ArchiveRequirementImpl(resource);
    }

    @Override
    public FacetRequirement buildFacetRequirement(Resource resource, Class<?> facetClass) {
        return new FacetRequirementImpl(resource, facetClass);
    }

}
