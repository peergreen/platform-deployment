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

import com.peergreen.deployment.report.DeploymentStatusReport;

public class DefaultDeploymentStatusReport implements DeploymentStatusReport {

    // Exception ?

   // Artifacts

   // For each phase, elapsed time

    // top XX processors

    private long elapsedTime;

    private final Collection<DefaultArtifactStatusReport> artifactsReport;

    private boolean failure = false;


    public DefaultDeploymentStatusReport() {
        this.artifactsReport = new HashSet<DefaultArtifactStatusReport>();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }


    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setFailure() {
        this.failure = true;
    }

    @Override
    public boolean hasFailed() {
        return failure;
    }


    public void addChild(DefaultArtifactStatusReport artifactStatusReport) {
        artifactsReport.add(artifactStatusReport);
    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nDeploymentStatus[");
        sb.append("elapsedTime='");
        sb.append(elapsedTime);
        sb.append("' ms.");

        sb.append("\nArtifacts");
        if (artifactsReport != null) {
            for (DefaultArtifactStatusReport artifactStatusReport : artifactsReport) {
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
