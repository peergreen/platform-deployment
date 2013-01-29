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
package com.peergreen.deployment.internal.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.resource.Requirement;

import com.peergreen.deployment.HandlerProcessor;
import com.peergreen.deployment.internal.phase.current.CurrentPhase;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.deployment.resource.phase.PhaseNamespace;
import com.peergreen.deployment.resource.phase.PhaseRequirement;


@Component
@Provides
@Instantiate(name="Processor Manager")
public class DefaultProcessorManager implements ProcessorManager {

    private final Set<HandlerProcessor> handlerProcessors;

    private final Set<InternalProcessor> allProcessors;
    private final Map<String, Set<InternalProcessor>> processorsByPhase;

    private CurrentProcessor currentProcessor;
    private CurrentPhase currentPhase;

    private boolean validated;


    public DefaultProcessorManager() {
        this.handlerProcessors = new HashSet<HandlerProcessor>();
        this.allProcessors = new HashSet<InternalProcessor>();
        this.processorsByPhase = new HashMap<String, Set<InternalProcessor>>();
    }

    /**
     * All required binding have been done.
     */
    @Validate
    public void validate() {
        validated = true;
        for (HandlerProcessor handlerProcessor : handlerProcessors) {

            InternalProcessor internalProcessor = new TaskInternalProcessor(handlerProcessor, currentProcessor, currentPhase);
            add(internalProcessor);
        }
        // clear
        handlerProcessors.clear();
    }


    protected void add(InternalProcessor internalProcessor) {
        List<Requirement> requirements = internalProcessor.getRequirements(null);
        for (Requirement requirement : requirements) {
            if (requirement instanceof PhaseRequirement) {
                String phase = (String) ((PhaseRequirement) requirement).getAttributes().get(PhaseNamespace.PHASE_NAMESPACE);

                Set<InternalProcessor> set = processorsByPhase.get(phase);
                if (set == null) {
                    set = new HashSet<InternalProcessor>();
                    processorsByPhase.put(phase, set);
                }
                set.add(internalProcessor);

            }
        }

        this.allProcessors.add(internalProcessor);

    }


    @Override
    public Iterable<InternalProcessor> getProcessors() {
        return Collections.unmodifiableSet(allProcessors);
    }

    @Override
    public Iterable<InternalProcessor> getProcessors(String phase) {
        Set<InternalProcessor> processors = processorsByPhase.get(phase);
        if (processors != null) {
            return Collections.unmodifiableSet(processors);
        }
        return null;
    }


    @Bind(aggregate=true,optional=true)
    public void bindProcessor(HandlerProcessor processor) {
        if (validated) {
            add(new TaskInternalProcessor(processor, currentProcessor, currentPhase));
        } else {
            this.handlerProcessors.add(processor);
        }

    }

    @Unbind(aggregate=true,optional=true)
    public void unbindProcessor(HandlerProcessor processor) {
        //FIXME : not implemented
    }


    @Bind(optional=false)
    public void bindCurrentProcessor(CurrentProcessor currentProcessor) {
        this.currentProcessor = currentProcessor;
    }

    @Unbind(optional=false)
    public void unbindCurrentProcessor(CurrentProcessor currentProcessor) {
        this.currentProcessor = null;
    }


    @Bind(optional=false)
    public void bindCurrentPhase(CurrentPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    @Unbind(optional=false)
    public void unbindCurrentPhase(CurrentPhase currentPhase) {
        this.currentPhase = null;
    }




}
