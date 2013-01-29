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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolveContext;

public class ResolveContextImpl extends ResolveContext {
    private final Collection<Resource> resources;
    private final Map<Resource, Wiring> wirings;
    private final Collection<Resource> mandatoryResources;
    private final Collection<Resource> optionalResources;

    public ResolveContextImpl(Collection<Resource> resources, Map<Resource, Wiring> wirings,
            Collection<Resource> mandatoryResources, Collection<Resource> optionalResources) {
        this.resources = resources;
        this.wirings = wirings;
        this.mandatoryResources = mandatoryResources;
        this.optionalResources = optionalResources;
    }

    @Override
    public Collection<Resource> getMandatoryResources() {
        return Collections.unmodifiableCollection(mandatoryResources);
    }

    @Override
    public Collection<Resource> getOptionalResources() {
        return Collections.unmodifiableCollection(optionalResources);
    }

    @Override
    public List<Capability> findProviders(Requirement requirement) {
        ArrayList<Capability> capabilities = new ArrayList<Capability>();
        for (Resource resource : resources) {
            for (Capability capability : resource.getCapabilities(requirement.getNamespace())) {
                if (matches(requirement, capability)) {
                    capabilities.add(capability);
                }
            }
        }

        // No matching capabilities, add a missing capability
        if (capabilities.isEmpty()) {
            capabilities.add(new MissingCapability(requirement));
        }

        return capabilities;
    }

    @Override
    public int insertHostedCapability(List<Capability> capabilities, HostedCapability hostedCapability) {
        int idx = 0;
        capabilities.add(idx, hostedCapability);
        return idx;
    }

    @Override
    public boolean isEffective(Requirement requirement) {
        return true;
    }

    @Override
    public Map<Resource, Wiring> getWirings() {
        return wirings;
    }

    public static boolean matches(Requirement requirement, Capability capability) {
        boolean result = false;
        if (requirement == null && capability == null) {
            result = true;

        } else if (requirement == null || capability == null) {
            result = false;
        } else if (!capability.getNamespace().equals(requirement.getNamespace())) {
            result = false;
        } else {
            String filterStr = requirement.getDirectives().get(Constants.FILTER_DIRECTIVE);
            if (filterStr == null) {
                result = true;
            } else {
                try {
                    if (FrameworkUtil.createFilter(filterStr).matches(capability.getAttributes())) {
                        result = true;
                    }
                } catch (InvalidSyntaxException e) {
                    result = false;
                }
            }
        }
        return result;
    }

}