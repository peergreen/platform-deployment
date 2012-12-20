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
package com.peergreen.deployment.internal.service;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.service.resolver.Resolver;

import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.internal.phase.current.CurrentPhase;
import com.peergreen.deployment.internal.phase.lifecycle.FacetLifeCycleManager;
import com.peergreen.deployment.internal.processor.ProcessorManager;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.tasks.context.ExecutionContext;

@Component
@Provides
@Instantiate(name="Injection Context")
public class BasicInjectionContext implements InjectionContext {

    private ArtifactBuilder artifactBuilder;

    private Resolver resolver;

    private ProcessorManager processorManager;

    private FacetLifeCycleManager facetLifeCycleManager;

    private CurrentProcessor currentProcessor;

    private CurrentPhase currentPhase;

    // Adds injection into deployment context
    @Override
    public void addInjection(ExecutionContext executionContext) {
        executionContext.add(artifactBuilder);
        executionContext.add(resolver);
        executionContext.add(processorManager);
        executionContext.add(facetLifeCycleManager);
        executionContext.add(currentProcessor);
        executionContext.add(currentPhase);
    }


    //FIXME : Allows to plug other services to inject something in the deployment context


    @Bind(optional=false)
        public void bindArtifactBuilder(ArtifactBuilder artifactBuilder) {
        this.artifactBuilder = artifactBuilder;
    }

    @Bind(optional=false)
        public void bindResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    @Bind(optional=false)
        public void bindProcessorManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }

    @Bind(optional=false)
    public void bindFacetLifeCycleManager(FacetLifeCycleManager facetLifeCycleManager) {
        this.facetLifeCycleManager = facetLifeCycleManager;
    }


    @Bind(optional=false)
    public void bindCurrentProcessor(CurrentProcessor currentProcessor) {
        this.currentProcessor = currentProcessor;
    }

    @Bind(optional=false)
    public void bindCurrentPhase(CurrentPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

}
