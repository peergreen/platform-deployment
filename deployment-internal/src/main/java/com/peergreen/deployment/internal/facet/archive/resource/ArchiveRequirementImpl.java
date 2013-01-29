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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.resource.Resource;
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

import com.peergreen.deployment.internal.resource.AbsDeploymentRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveNamespace;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;

public class ArchiveRequirementImpl extends AbsDeploymentRequirement implements ArchiveRequirement {

    private Map<String, String> requiredAttributes = null;


    public ArchiveRequirementImpl(Resource resource) {
        super(resource, ArchiveNamespace.ARCHIVE_NAMESPACE);
        this.requiredAttributes = new HashMap<>();
    }

    @Override
    public ArchiveRequirement addRequiredAttribute(String attributeName) {
        requiredAttributes.put(attributeName, null);
        recomputeFilterValue();
        return this;
    }

    @Override
    public ArchiveRequirement addRequiredAttribute(String attributeName, String expectedValue) {
        requiredAttributes.put(attributeName, expectedValue);
        recomputeFilterValue();
        return this;
    }


    @Override
    protected IFilter computeFilter(IFilter filter) {
        if (requiredAttributes != null && requiredAttributes.isEmpty()) {
            Iterator<Entry<String, String>> it = requiredAttributes.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                // Null value, check if only present
                if (entry.getValue() == null) {
                    return Filters.and(filter, Filters.present(entry.getKey()));
                } else {
                    // check equals for attribute
                    return Filters.and(filter, Filters.equal(entry.getKey(), entry.getValue()));
                }
            }
        }
        return filter;
    }
}
