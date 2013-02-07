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

package com.peergreen.deployment.internal.facet.archive.builder;

import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.internal.facet.archive.URIArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 17/01/13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class UriArchiveFacetBuilder implements FacetBuilder {

    public static final String ID = "com.peergreen.deployment.internal.facet.archive.builder.uri";

    @Override
    public void build(BuilderContext context) throws FacetBuilderException {
        try {
            context.addFacet(Archive.class,
                             new URIArchiveImpl(context.getArtifact().uri()),
                             new ArchiveFacetAdapter());
        } catch (ArchiveException e) {
            throw new FacetBuilderException("Cannot rebuild UriArchive facet", e);
        }
    }

}
