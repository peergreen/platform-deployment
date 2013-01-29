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
package com.peergreen.deployment.internal.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Constants;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

public class MissingCapability implements Capability {
    private static class Resource implements org.osgi.resource.Resource {
        public static final Resource INSTANCE = new Resource();

        public Resource() {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put(IdentityNamespace.IDENTITY_NAMESPACE, "missing.resource");
            attributes.put(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE, IdentityNamespace.TYPE_UNKNOWN);
        }

        @Override
        public List<Capability> getCapabilities(String namespace) {
            return Collections.emptyList();
        }

        @Override
        public List<Requirement> getRequirements(String namespace) {
            return Collections.emptyList();
        }
    }

    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private final Requirement requirement;

    public MissingCapability(Requirement requirement) {
        this.requirement = requirement;
        initializeAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Map<String, String> getDirectives() {
        return Collections.emptyMap();
    }

    @Override
    public String getNamespace() {
        return requirement.getNamespace();
    }

    @Override
    public Resource getResource() {
        return Resource.INSTANCE;
    }

    private void initializeAttributes() {
        String filter = requirement.getDirectives().get(Constants.FILTER_DIRECTIVE);
        if (filter == null)
            return;
        Pattern pattern = Pattern.compile("\\(([^(=]+)=([^)]+)\\)");
        Matcher matcher = pattern.matcher(filter);
        while (matcher.find())
            attributes.put(matcher.group(1), matcher.group(2));
    }
}