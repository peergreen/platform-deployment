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

import java.util.HashMap;
import java.util.Map;

import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

public abstract class AbsDeploymentRequirement implements Requirement {

    private final Resource resource;

    private String value;

    private final String namespace;

    private final Map<String, String> directives;

    private final Map<String, Object> attributes;

    private IFilter filter;



    public AbsDeploymentRequirement(Resource resource, String namespace) {
        this(resource, namespace, null);
    }

    public AbsDeploymentRequirement(Resource resource, String namespace, String value) {
        this.resource = resource;
        this.namespace = namespace;
        this.directives = new HashMap<String, String>();
        this.attributes = new HashMap<String, Object>();
        if (value == null) {
            this.attributes.put(namespace, "");
            this.filter = Filters.present(namespace);
            this.value = "";
        } else {
            this.attributes.put(namespace, value);
            this.filter = Filters.equal(namespace, value);
            this.value = value;
        }
        recomputeFilterValue();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public Map<String, String> getDirectives() {
        if (!directives.containsKey(Namespace.REQUIREMENT_FILTER_DIRECTIVE)) {
            throw new IllegalStateException("Illegal");
        }
        return directives;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Resource getResource() {
        return resource;
    }


    @Override
    public String toString() {
        return  this.getClass().getName().concat("[").concat(value).concat("]");
    }


    protected IFilter computeFilter(IFilter filter) {
        return filter;
    }

    protected void recomputeFilterValue() {
        IFilter compute = computeFilter(filter);
        if (compute == null) {
            throw new IllegalStateException("Cannot remove default filter. Origin ='" + filter + "', computed is null.");
        }
        String filterValue = compute.asText();

        directives.put(Namespace.REQUIREMENT_FILTER_DIRECTIVE, filterValue);

    }


}
