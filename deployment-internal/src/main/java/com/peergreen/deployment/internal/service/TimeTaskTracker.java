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
package com.peergreen.deployment.internal.service;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.tasks.execution.LiveTask;
import com.peergreen.tasks.execution.tracker.TaskTracker;
import com.peergreen.tasks.model.State;
import com.peergreen.tasks.model.UnitOfWork;

/**
 * Tracker for tracking the time of a unit of work that belongs to a specific artifact.
 * Each time consumed by the unit of work is added to the artifact.
 * @author Florent Benoit
 */
public class TimeTaskTracker extends TaskTracker<TimeKeeper> {

    /**
     * @return a new object only if the task is a unit of work and has a deployment context.
     */
    @Override
    public TimeKeeper newSource(LiveTask source) {
        // Select only unit of work
        if (!(source.getModel() instanceof UnitOfWork)) {
            return null;
        }

        BasicDeploymentContext deploymentContext = source.getContext().get(BasicDeploymentContext.class);

        // No deployment context, nothing to track
        if (deploymentContext == null) {
            return null;
        }
        // There is a deployment context, needs to track it
        return new TimeKeeper();
    }


    /**
     * Track the time if the current state is running/completed/failed.
     */
    @Override
    public void sourceChanged(LiveTask source, State previous, TimeKeeper timeKeeper) {
        // Get current artifacrt
        BasicDeploymentContext deploymentContext = source.getContext().get(BasicDeploymentContext.class);
        IFacetArtifact artifact = deploymentContext.getFacetArtifact();


        switch (source.getState()) {
        case RUNNING:
            timeKeeper.setStartTime(System.currentTimeMillis());
            break;
        case COMPLETED:
        case FAILED:
            long endTime = System.currentTimeMillis();

            artifact.addTime((endTime - timeKeeper.getStartTime()));
            break;
        default:
            // Nothing for this default case
            break;
        }

    }
}
