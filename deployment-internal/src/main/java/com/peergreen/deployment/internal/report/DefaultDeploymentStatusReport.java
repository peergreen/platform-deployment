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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.peergreen.deployment.report.ArtifactStatusReport;
import com.peergreen.deployment.report.DeploymentStatusReport;

public class DefaultDeploymentStatusReport implements DeploymentStatusReport {

    // Exception ?

   // Artifacts

   // For each phase, elapsed time

    // top XX processors

    private long elapsedTime;

    private final Collection<ArtifactStatusReport> artifactsReport;

    private boolean failure = false;


    public DefaultDeploymentStatusReport() {
        this.artifactsReport = new HashSet<>();
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
    public Collection<ArtifactStatusReport> getArtifactStatusReports() {
        return Collections.unmodifiableCollection(artifactsReport);
    }


    protected String getReport(ArtifactStatusReport artifactStatusReport) {

        StringBuilder sb = new StringBuilder();

        // failure or not ?

        // failures
        List<String> exceptions = new ArrayList<>();
        if (childException(artifactStatusReport, exceptions)) {

            Stats stats = new Stats();
            updateStats(artifactStatusReport, stats);

            sb.append(exceptions.size()).append(" errors for ").append(artifactStatusReport.uri()).append(":");
            int i = 1;
            for (String exception : exceptions) {
                sb.append("\n  [").append(i++).append("] :").append(exception);
            }
            sb.append("\nTotal: ").append(stats.total).append(" artifacts with ");
            sb.append(stats.success).append(" OK");
            sb.append(", ").append(stats.fail).append(" KO");

        } else {
            // No exception gets only a resume
            Stats stats = new Stats();

            // There are children
            if (artifactStatusReport.children().size() > 0) {
                updateStats(artifactStatusReport, stats);
                sb.append(artifactStatusReport.uri());
                sb.append(" : ");
                sb.append(String.format("%d children, %d OK, %d FAIL.", stats.total, stats.success, stats.fail));
            } else {
                sb.append(artifactStatusReport.uri().toString()).append(":\t").append("OK");
            }

        }



        return sb.toString();

    }


    protected boolean childException(ArtifactStatusReport artifactStatusReport, Collection<String> exceptions) {
        boolean hasExceptions = false;
        // we've exceptions
        if (artifactStatusReport.getExceptions() != null && artifactStatusReport.getExceptions().size() > 0) {
            hasExceptions = true;
            exceptions.add(artifactStatusReport.uri().toString().concat(" : ").concat(formatException(artifactStatusReport.getExceptions())));
        }

        if (artifactStatusReport.children() != null && artifactStatusReport.children().size() > 0) {
            for (ArtifactStatusReport child : artifactStatusReport.children()) {
                hasExceptions =  childException(child, exceptions) || hasExceptions;
            }
        }

        return hasExceptions;
    }

    protected String formatException(Collection<Throwable> exceptions) {
        StringBuilder sb = new StringBuilder();
        for (Throwable exception : exceptions) {
            sb.append(exception.getMessage());
            Throwable cause = exception.getCause();
            String indent = "    ";
            while(cause != null) {
                if (cause.getMessage() != null && cause.getMessage().length() > 0) {
                    sb.append("\n").append(indent).append("cause: ").append(cause.getMessage());
                }
                cause = cause.getCause();
                indent = indent.concat("  ");
            }
        }


        return sb.toString();
    }



    @Override
    public String shortReport() {
        StringBuilder sb = new StringBuilder("Report: \n");

        // Only one request ?
        if (artifactsReport.size() == 0) {
            sb.append(" No request.");
            return sb.toString();
        }


        // Request
        for (ArtifactStatusReport artifactStatusReport : artifactsReport) {
            sb.append(getReport(artifactStatusReport));
        }

        sb.append("\nProcess done in '");
        sb.append(elapsedTime);
        sb.append("' ms.");

        return sb.toString();
    }

    protected void updateStats(ArtifactStatusReport artifactStatusReport, Stats stats) {
        Collection<ArtifactStatusReport> children = artifactStatusReport.children();

        stats.total++;
        if (artifactStatusReport.getExceptions() != null && artifactStatusReport.getExceptions().size() > 0) {
            stats.fail++;
        } else {
            stats.success++;
        }



        if (children != null) {
            for (ArtifactStatusReport child : children) {
                updateStats(child, stats);
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nDeploymentStatus[");
        sb.append("elapsedTime='");
        sb.append(elapsedTime);
        sb.append("' ms.");

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


    static class Stats {
        public int total;
        public int fail;
        public int success;
    }


}
