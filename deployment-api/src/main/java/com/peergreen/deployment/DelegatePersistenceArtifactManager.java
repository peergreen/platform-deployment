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
package com.peergreen.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

import com.peergreen.deployment.resource.builder.RequirementBuilder;

public class DelegatePersistenceArtifactManager implements InternalPersistenceArtifactManager {

    private RequirementBuilder requirementBuilder;

    private final List<Requirement> requirements;

    private final PersistenceArtifactManager wrappedPersistenceArtifactManager;

    public DelegatePersistenceArtifactManager(PersistenceArtifactManager persistenceArtifactManager) {
        this(persistenceArtifactManager, new ArrayList<Requirement>());
    }

    public DelegatePersistenceArtifactManager(PersistenceArtifactManager persistenceArtifactManager, List<Requirement> requirements) {
        this.wrappedPersistenceArtifactManager = persistenceArtifactManager;
        this.requirements = requirements;
    }

    protected void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        this.requirementBuilder = requirementBuilder;
    }

    protected RequirementBuilder getRequirementBuilder() {
        return requirementBuilder;
    }


    /**
     * No capabilities for the persistence artifact manager (only requirements)
     */
    @Override
    public List<Capability> getCapabilities(String namespace) {
        return Collections.emptyList();
    }


    @Override
    public List<Requirement> getRequirements(String namespace) {

        // No namespace, no filter
        if (namespace == null) {
            return Collections.unmodifiableList(requirements);
        }

        // Select only matching namespace
        List<Requirement> matchingRequirements = new ArrayList<Requirement>();
        List<Requirement> currentRequirements = requirements;

        // for each internal requirement, check if given namespace is
        // matching and add it
        for (Requirement requirement : currentRequirements) {
            if (namespace.equals(requirement.getNamespace())) {
                matchingRequirements.add(requirement);
            }
        }

        return Collections.unmodifiableList(matchingRequirements);

    }

    public void addRequirement(Requirement requirement) {
        requirements.add(requirement);
    }

    public void addRequirements(List<Requirement> requirements) {
        this.requirements.addAll(requirements);
    }

    @Override
    public void forget(Collection<Artifact> artifacts) {
       wrappedPersistenceArtifactManager.forget(artifacts);
    }


}
