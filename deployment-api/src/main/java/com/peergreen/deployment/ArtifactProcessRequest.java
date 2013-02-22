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
package com.peergreen.deployment;

/**
 * Request for the deployment service.
 * @author Florent Benoit
 */
public class ArtifactProcessRequest {

    private final Artifact artifact;

    private DeploymentMode deploymentMode;

    private boolean persistent = false;

    public ArtifactProcessRequest(Artifact artifact) {
        this.artifact = artifact;
        // default is deploy
        this.deploymentMode = DeploymentMode.DEPLOY;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(DeploymentMode deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Artifact[name=");
        sb.append(artifact.name());
        sb.append(", uri=");
        sb.append(artifact.uri());
        if (deploymentMode != null) {
            sb.append(", mode=");
            sb.append(deploymentMode);
        }
        if (persistent) {
            sb.append(", persistent");
        }
        sb.append("]").toString();
        return sb.toString();
    }

}
