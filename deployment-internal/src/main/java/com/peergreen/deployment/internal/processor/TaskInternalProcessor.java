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

import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.HandlerProcessor;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.deployment.internal.phase.current.CurrentPhase;
import com.peergreen.deployment.internal.processor.current.CurrentProcessor;
import com.peergreen.tasks.context.TaskContext;

public class TaskInternalProcessor implements InternalProcessor {

    private long totalCount = 0;
    private long totalTime = 0;

    private final HandlerProcessor handlerProcessor;

    private final CurrentProcessor currentProcessor;

    private final CurrentPhase currentPhase;


    private final String name;


    public TaskInternalProcessor(HandlerProcessor handlerProcessor, CurrentProcessor currentProcessor, CurrentPhase currentPhase) {
        this.handlerProcessor = handlerProcessor;
        this.currentProcessor = currentProcessor;
        this.currentPhase = currentPhase;
        this.name = handlerProcessor.getClass().getName();
    }

    @Override
    public void execute(TaskContext context) {

        // Get internal deployment context
        BasicDeploymentContext deploymentContext = context.get(BasicDeploymentContext.class);
        // bypass execution if there are errors.
        if (deploymentContext.hasFailed()) {
            return;
        }


        InternalProcessor old = currentProcessor.getCurrent();
        currentProcessor.setCurrent(this);
        long tStart = System.currentTimeMillis();
        try {
            handlerProcessor.handle(deploymentContext);
        } catch (ProcessorException | RuntimeException e) {
            // Add the error on the artifact

            deploymentContext.getFacetArtifact().addException(e);

            // Flag deployment context as being in error
            deploymentContext.setFailed();
        } finally {
            // increment statistics
            long delta = (System.currentTimeMillis() - tStart);
            totalTime += delta;
            totalCount++;

            // notify the Artifact the time used by the processor
            deploymentContext.getFacetArtifact().addProcessorTime(currentPhase.getCurrent(), delta, this);

            // reset
            currentProcessor.setCurrent(old);
        }
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        return handlerProcessor.getCapabilities(namespace);
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        return handlerProcessor.getRequirements(namespace);
    }

    @Override
    public void handle(DeploymentContext deploymentContext) {
        throw new IllegalStateException("Never called");
    }

    @Override
    public Class<?> getExpectedHandleType() {
        return handlerProcessor.getExpectedHandleType();
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public long getTotaltime() {
        return totalTime;
    }

    @Override
    public void resetStats() {
        totalCount = 0;
        totalTime = 0;
    }

    @Override
    public String getName() {
        return name;
    }


}
