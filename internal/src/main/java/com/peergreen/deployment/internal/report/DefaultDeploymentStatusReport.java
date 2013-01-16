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
package com.peergreen.deployment.internal.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentStatusReport;
import com.peergreen.tasks.model.State;

public class DefaultDeploymentStatusReport implements DeploymentStatusReport {

    // Exception ?

   // Artifacts

   // For each phase, elapsed time

    // top XX processors

    private String state;

    private long elapsedTime;

    private final Collection<ArtifactStatusReport> artifactsReport;

    private final String deploymentMode;

    private boolean failure = false;


    public DefaultDeploymentStatusReport(DeploymentMode deploymentMode, List<Artifact> artifacts) {
        this.deploymentMode = deploymentMode.name();
        this.artifactsReport = new HashSet<ArtifactStatusReport>();


    }

    public long getElapsedTime() {
        return elapsedTime;
    }


    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setState(State state) {
        this.state = state.name();
    }

    public void setFailure() {
        this.failure = true;
    }

    public boolean hasFailed() {
        return failure;
    }

    public String getState() {
        return state;
    }


    public void addChild(ArtifactStatusReport artifactStatusReport) {
        artifactsReport.add(artifactStatusReport);
    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nDeploymentStatus[mode=");
        sb.append(deploymentMode);
        sb.append(", elapsedTime=");
        sb.append(elapsedTime);
        sb.append("\nArtifacts");
        if (artifactsReport != null) {
            for (ArtifactStatusReport artifactStatusReport : artifactsReport) {
                sb.append(artifactStatusReport.toString("  "));
            }
        }
        sb.append("\n");
        if (failure) {
            sb.append("Failure in the deployment");
        }
        sb.append("\n");
        return sb.toString();
    }
}
