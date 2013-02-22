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
import java.util.Collections;
import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.resource.builder.CapabilityBuilder;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

public class DelegateFacetBuilder<Facet> implements InternalFacetBuilder<Facet> {

    private RequirementBuilder requirementBuilder;

    private CapabilityBuilder capabilityBuilder;

    private final List<Requirement> requirements;

    private final List<Capability> capabilities;

    private final FacetBuilder<Facet> wrappedFacetBuilder;

    private Class<Facet> facetType;

    public DelegateFacetBuilder(FacetBuilder<Facet> wrappedFacetBuilder, Class<Facet> facetType) {
        this(wrappedFacetBuilder, facetType, new ArrayList<Requirement>(), new ArrayList<Capability>());
    }

    public DelegateFacetBuilder(FacetBuilder<Facet> wrappedFacetBuilder, Class<Facet> facetType, List<Requirement> requirements, List<Capability> capabilities) {
        this.wrappedFacetBuilder = wrappedFacetBuilder;
        this.facetType = facetType;
        this.requirements = requirements;
        this.capabilities = capabilities;
    }

    protected void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        this.requirementBuilder = requirementBuilder;
    }

    protected void bindCapabilityBuilder(CapabilityBuilder capabilityBuilder) {
        this.capabilityBuilder = capabilityBuilder;
    }


    protected RequirementBuilder getRequirementBuilder() {
        return requirementBuilder;
    }


    protected CapabilityBuilder getCapabilityBuilder() {
        return capabilityBuilder;
    }


    /**
     * Capabilities for the facet builder (Facet produced)
     */
    @Override
    public List<Capability> getCapabilities(String namespace) {

        // No namespace, no filter
        if (namespace == null) {
            return Collections.unmodifiableList(capabilities);
        }

        // Select only matching namespace
        List<Capability> matchingCapabilities = new ArrayList<>();
        List<Capability> currentCapabilities = capabilities;

        // for each internal capability, check if given namespace is
        // matching and add it
        for (Capability capability : currentCapabilities) {
            if (namespace.equals(capability.getNamespace())) {
                matchingCapabilities.add(capability);
            }
        }

        return Collections.unmodifiableList(matchingCapabilities);
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


    public void addCapability(Capability capability) {
        capabilities.add(capability);
    }

    public void addCapabilities(List<Capability> capabilities) {
        this.capabilities.addAll(capabilities);
    }



    @Override
    public void build(BuilderContext<Facet> context) throws FacetBuilderException {
        wrappedFacetBuilder.build(context);

    }

    @Override
    public String getName() {
        return wrappedFacetBuilder.getClass().getName();
    }

    @Override
    public Class<Facet> getFacetClass() {
        return facetType;
    }

    public void postConstruct() {
        this.addCapability(capabilityBuilder.buildFacetCapability(this, facetType));
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FacetBuilder[");
        sb.append(wrappedFacetBuilder.getClass().getName());
        sb.append("(");
        sb.append(facetType);
        sb.append(")]");
        return sb.toString();
    }

}
