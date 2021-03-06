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
package com.peergreen.deployment.internal.phase.builder;

import java.util.Collection;

import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.internal.model.InternalArtifactModel;

public class TaskModelParameters {

    private Collection<ArtifactProcessRequest> artifactProcessRequests;

    public Collection<ArtifactProcessRequest> getArtifactProcessRequests() {
        return artifactProcessRequests;
    }
    public void setArtifactProcessRequests(Collection<ArtifactProcessRequest> artifactProcessRequests) {
        this.artifactProcessRequests = artifactProcessRequests;
    }
    public DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }
    public void setDeploymentMode(DeploymentMode deploymentMode) {
        this.deploymentMode = deploymentMode;
    }
    public TaskExecutionHolder getTaskExecutionHolder() {
        return taskExecutionHolder;
    }
    public void setTaskExecutionHolder(TaskExecutionHolder taskExecutionHolder) {
        this.taskExecutionHolder = taskExecutionHolder;
    }
    public InternalArtifactModel getRootArtifactModel() {
        return rootArtifactModel;
    }
    public void setRootArtifactModel(InternalArtifactModel rootArtifactModel) {
        this.rootArtifactModel = rootArtifactModel;
    }
        private DeploymentMode deploymentMode;
        private TaskExecutionHolder taskExecutionHolder;
        private InternalArtifactModel rootArtifactModel;

}
