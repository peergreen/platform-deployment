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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

public abstract class AbsDeploymentCapability implements Capability {

    private final Resource resource;

    private final String value;

    private final String namespace;


    private final Map<String, Object> attributes;


    public AbsDeploymentCapability(Resource resource, String namespace) {
        this(resource, namespace, null);
    }

    public AbsDeploymentCapability(Resource resource, String namespace, String value) {
        this.resource = resource;
        this.namespace = namespace;
        this.attributes = new HashMap<String, Object>();
        this.value = value;
        if (value == null) {
            this.attributes.put(namespace, "");
        } else {
            this.attributes.put(namespace, value);
        }
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public Map<String, String> getDirectives() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }


    protected void addAttribute(String name, Object attribute) {
        this.attributes.put(name,  attribute);
    }

    @Override
    public Resource getResource() {
        return resource;
    }


    @Override
    public String toString() {
        return  this.getClass().getName().concat("[").concat(value).concat("]");
    }


}
