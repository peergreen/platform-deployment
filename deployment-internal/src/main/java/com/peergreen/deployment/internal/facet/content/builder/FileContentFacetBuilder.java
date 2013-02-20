/*
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

package com.peergreen.deployment.internal.facet.content.builder;

import java.io.File;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.facet.content.FileContentImpl;
import com.peergreen.deployment.internal.facet.content.adapter.ContentFacetAdapter;


/**
 * Defines builder for File content
 * @author Guillaume Sauthier
 */
public class FileContentFacetBuilder implements FacetBuilder<Content> {

    @Override
    public void build(BuilderContext<Content> context) {
        context.addFacet(new FileContentImpl(getFile(context.getArtifact())),
                         new ContentFacetAdapter());
    }

    protected File getFile(Artifact artifact) {
        return new File(artifact.uri().getPath());
    }


}
