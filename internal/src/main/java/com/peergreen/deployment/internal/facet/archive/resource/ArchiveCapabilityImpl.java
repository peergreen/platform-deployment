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
package com.peergreen.deployment.internal.facet.archive.resource;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.resource.Resource;

import com.peergreen.deployment.internal.resource.AbsDeploymentCapability;
import com.peergreen.deployment.resource.artifact.archive.ArchiveCapability;
import com.peergreen.deployment.resource.artifact.archive.ArchiveNamespace;

public class ArchiveCapabilityImpl extends AbsDeploymentCapability implements ArchiveCapability {

    public ArchiveCapabilityImpl(Resource resource) {
        super(resource, ArchiveNamespace.ARCHIVE_NAMESPACE);
    }

    @Override
    public ArchiveCapability addManifestEntries(Map<String, String> manifestEntries) {
        Iterator<Entry<String, String>> it = manifestEntries.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            addAttribute(entry.getKey(), entry.getValue());
        }

        return this;
    }


}
