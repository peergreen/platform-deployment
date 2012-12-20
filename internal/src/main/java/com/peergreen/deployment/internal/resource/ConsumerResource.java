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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

/**
 * A consumer is not providing any capabilities, only requirements.
 * @author Florent Benoit
 */
public class ConsumerResource implements Resource {

    private final Collection<Requirement> requirements;

    public ConsumerResource() {
        this.requirements = new ArrayList<Requirement>();
    }

    /**
     * No capabilities.
     */
    @Override
    public List<Capability> getCapabilities(String namespace) {
        return Collections.emptyList();
            }


    @Override
    public List<Requirement> getRequirements(String namespace) {

        // New list
        List<Requirement> matchingRequirements = new ArrayList<Requirement>();

        // Add all matching requirements
        if (namespace != null) {
            for (Requirement requirement : requirements) {
                if (namespace.equals(requirement.getNamespace())) {
                    matchingRequirements.add(requirement);
                }
            }
        } else {
            // add all requirements for null namespace
            matchingRequirements.addAll(requirements);
        }

        return Collections.unmodifiableList(matchingRequirements);

    }


    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }


}
