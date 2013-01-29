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
package com.peergreen.deployment.resource.builder;

import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;
import com.peergreen.deployment.resource.artifact.content.ContentRequirement;
import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;
import com.peergreen.deployment.resource.facet.FacetRequirement;
import com.peergreen.deployment.resource.phase.PhaseRequirement;

public interface RequirementBuilder {

    ContentRequirement buildContentRequirement(Resource resource);

    ArchiveRequirement buildArchiveRequirement(Resource resource);

    PhaseRequirement buildPhaseRequirement(Resource resource, String phaseName);

    XMLContentRequirement buildXMLContentRequirement(Resource resource);

    ArtifactRequirement buildArtifactRequirement(Resource resource);

    FacetRequirement buildFacetRequirement(Resource resource, Class<?> facetClass);

}
