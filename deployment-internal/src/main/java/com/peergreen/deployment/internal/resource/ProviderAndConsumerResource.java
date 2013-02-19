/**
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
package com.peergreen.deployment.internal.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ProviderAndConsumerResource implements Resource {


    private final List<Requirement> requirements;
    private final List<Capability> capabilities;

    public ProviderAndConsumerResource() {
        this.capabilities = new ArrayList<>();
        this.requirements = new ArrayList<>();

    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        // handle null namespace
        if (namespace == null) {
            return Collections.unmodifiableList(requirements);
        }

        // New list
        List<Requirement> matchingRequirements = new ArrayList<Requirement>();

        // Add all matching requirements
        for (Requirement requirement : requirements) {
            if (namespace.equals(requirement.getNamespace())) {
                matchingRequirements.add(requirement);
            }
        }

        return Collections.unmodifiableList(matchingRequirements);
    }


    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }


    protected Collection<Capability> getInnerCapabilities() {
        return capabilities;
    }


    protected Collection<Requirement> getInnerRequirements() {
        return requirements;
    }



    @Override
    public List<Capability> getCapabilities(String namespace) {
        // handle null namespace
        if (namespace == null) {
            return Collections.unmodifiableList(capabilities);
        }

        // New list
        List<Capability> matchingCapabilities = new ArrayList<Capability>();

        // Add all matching capabilities
        for (Capability capability : capabilities) {
            if (namespace.equals(capability.getNamespace())) {
                matchingCapabilities.add(capability);
            }
        }

        return Collections.unmodifiableList(matchingCapabilities);
    }


    public void addCapability(Capability capability) {
        this.capabilities.add(capability);
    }
}
