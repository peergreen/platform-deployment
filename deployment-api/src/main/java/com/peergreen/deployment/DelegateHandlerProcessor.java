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
import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.builder.RequirementBuilder;

public class DelegateHandlerProcessor<T> implements HandlerProcessor, Resource {

    private RequirementBuilder requirementBuilder;

    private List<Requirement> requirements;

    private final Class<T> expectedHandleType;

    private final Processor<T> wrappedProcessor;

    public DelegateHandlerProcessor(Processor<T> processor, Class<T> expectedHandleType) {
        this(processor, expectedHandleType, new ArrayList<Requirement>());
    }

    public DelegateHandlerProcessor(Processor<T> processor, Class<T> expectedHandleType, List<Requirement> requirements) {
        this.wrappedProcessor = processor;
        this.expectedHandleType = expectedHandleType;
        this.requirements = requirements;
    }

    protected void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        this.requirementBuilder = requirementBuilder;
    }

    protected RequirementBuilder getRequirementBuilder() {
        return requirementBuilder;
    }


    /**
     * No capabilities for the processor (only requirements)
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
    public void handle(DeploymentContext deploymentContext) throws ProcessorException {
        Class<T> toCast =  expectedHandleType;
        ProcessorContext processorContext = deploymentContext.get(ProcessorContext.class);
        if (DeploymentContext.class.equals(toCast) || toCast.isInstance(deploymentContext)) {
            wrappedProcessor.handle(toCast.cast(deploymentContext), processorContext);
        } else if (Artifact.class.equals(toCast)) {
            wrappedProcessor.handle(toCast.cast(deploymentContext.getArtifact()), processorContext);
        } else {
            // It's contained in the artifact
            T object = deploymentContext.getArtifact().as(toCast);
            wrappedProcessor.handle(toCast.cast(object), processorContext);
        }
    }

    @Override
    public Class<T> getExpectedHandleType() {
        return expectedHandleType;
    }

    public Processor<T> getWrappedProcessor() {
        return wrappedProcessor;
    }


}
