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
package com.peergreen.deployment.tracker;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.report.ArtifactStatusReport;

/**
 * Notification by the deployment service that an artifact is being handled or untracked.
 * @author Florent Benoit
 */
public interface DeploymentServiceTracker {

    /**
     * Notify the tracker that a new artifact will be processed
     * @param artifact the artifact that will be processed
     * @param deploymentMode the deployment mode
     */
    void beforeProcessing(Artifact artifact, DeploymentMode deploymentMode);

    /**
     * Notify the tracker that a new artifact has been processed
     * @param artifactModel the artifact model corresponding to the artifact that has been processed
     * @param deploymentMode the deployment mode
     * @param artifactStatusReport the report for the given artifact
     */
    void afterProcessing(ArtifactModel artifactModel, DeploymentMode deploymentMode, ArtifactStatusReport artifactStatusReport);

}
