/**
 * Copyright 2012-2013 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;

import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.internal.context.Job;
import com.peergreen.deployment.internal.phase.ContainerTask;
import com.peergreen.deployment.internal.phase.ParallelContainerTask;
import com.peergreen.deployment.internal.phase.current.CurrentPhase;
import com.peergreen.deployment.internal.processor.resource.ProcessorJobResource;
import com.peergreen.deployment.internal.solver.MissingCapability;
import com.peergreen.deployment.internal.solver.ResolveContextImpl;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.group.Group;

public class ProcessorJob implements Job {

    /**
     * Name of the phase on which we've to bind the jobs
     */
    private final String bindingPhase;

    private final ContainerTask containerTask;

    private final Group group;

    public ProcessorJob(String bindingPhase, Parallel parallel) {
        this(bindingPhase, new ParallelContainerTask(parallel));
    }

    public ProcessorJob(String bindingPhase, ContainerTask containerTask) {
        this(bindingPhase, containerTask, null);
    }


    public ProcessorJob(String bindingPhase, ContainerTask containerTask, Group group) {
        this.bindingPhase = bindingPhase;
        this.containerTask = containerTask;
        this.group = group;
    }

    @Override
    public void execute(DeploymentContext deploymentContext) {
        // Set the current task object
        deploymentContext.setProperty(bindingPhase, containerTask);

        // First, get the processor manager
        ProcessorManager processorManager = deploymentContext.get(ProcessorManager.class);

        // now we get current deployment context capabilities
        Collection<Capability> capabilities = deploymentContext.getCapabilities(null);

        // list of resources for the solver
        Set<Resource> resources = new HashSet<Resource>();

        // create the provider with the capabilities
        Resource provider = new ProcessorJobResource(capabilities, bindingPhase);
        resources.add(provider);

        // Prepare resources
        Iterable<InternalProcessor> processors = processorManager.getProcessors(bindingPhase);
        List<Resource> mandatoryResources = new ArrayList<Resource>();

        // no processors, don't try to do anything
        if (processors == null) {
            return;
        }

        for (InternalProcessor processor : processors) {
            mandatoryResources.add(processor);
            resources.add(processor);
        }

        // No optional resources (as we want to execute only matching processors)
        List<Resource> optionalResources = Collections.emptyList();

        // Create wirings
        Map<Resource, Wiring> wirings = new HashMap<Resource, Wiring>();

        // Gets the solver
        Resolver resolver = deploymentContext.get(Resolver.class);

        // Current Phase
        CurrentPhase currentPhase = deploymentContext.get(CurrentPhase.class);

        // Create the context
        ResolveContext resolveContext = new ResolveContextImpl(resources, wirings, mandatoryResources, optionalResources);

        Map<Resource, List<Wire>> wireMap = null;
        try {
            wireMap = resolver.resolve(resolveContext);
        } catch (ResolutionException e) {
            // FIXME Auto-generated catch block
            e.printStackTrace();
        }

        // Gets processors that are matching the current capabilities
        Set<InternalProcessor> toRunProcessors = new HashSet<InternalProcessor>();
        for (InternalProcessor processor : processors) {
            List<Wire> wires = wireMap.get(processor);
            if (wires != null) {
                boolean foundMissing = false;
                for (Wire wire : wires) {
                    if (wire.getCapability() instanceof MissingCapability) {
                        foundMissing = true;
                        break;
                    }
                }
                if (!foundMissing) {
                    toRunProcessors.add(processor);
                }
            }
        }

        // Now, for each processor that is matching, add it to the expected phase
        for (InternalProcessor processor : toRunProcessors) {
            UnitOfWork unitOfWork = new UnitOfWork(new JobPhase(processor, bindingPhase, currentPhase), processor.getName());
            if (group != null) {
                group.addTask(unitOfWork);
            }
            containerTask.addTask(unitOfWork);
        }

    }


}
