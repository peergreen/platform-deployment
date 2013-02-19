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
package com.peergreen.deployment.internal.facet.osgibundle;

import java.net.MalformedURLException;

import com.peergreen.deployment.facet.FacetBuilderReference;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.facet.archive.OSGiBundle;
import com.peergreen.deployment.internal.facet.osgibundle.builder.OsgiBundleFacetBuilder;

@FacetBuilderReference(OsgiBundleFacetBuilder.class)
public class OSGiBundleImpl implements OSGiBundle {

    private final String symbolicName;

    private final String location;

    public OSGiBundleImpl(Archive archive) throws ArchiveException {
        this.symbolicName = archive.getManifestEntries().get("Symbolic-Name");
        try {
            this.location = archive.getURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new ArchiveException("Unable to get bundle location", e);
        }
    }

    @Override
    public String symbolicName() {
        return symbolicName;
    }


    @Override
    public String location() {
        return location;
    }

}
