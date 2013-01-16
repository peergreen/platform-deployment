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

import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;

public class ArtifactStatusReport {

    /**
    ArtifactReport
    - name
    - uri
    - facets (time)
    - elapsed time
    - error
    - children
    - processors (time)
    */

    private final String name;
    private final String uri;
    private final Collection<FacetInfo> facetInfos;
    private final List<Exception> exceptions;
    private final Collection<ProcessorInfo> processors;
    private final Collection<ArtifactStatusReport> artifactsReport;
    private final long totalTime;

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public Collection<ProcessorInfo> getProcessors() {
        return processors;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public Collection<FacetInfo> getFacets() {
        return facetInfos;
    }

    public ArtifactStatusReport(IFacetArtifact facetArtifact) {
        this.name = facetArtifact.name();
        this.uri = facetArtifact.uri().toString();
        this.facetInfos = facetArtifact.getFacets();
        this.artifactsReport = new HashSet<ArtifactStatusReport>();
        this.processors = facetArtifact.getProcessors();
        this.totalTime = facetArtifact.getTotalTime();
        this.exceptions = facetArtifact.getExceptions();
    }

    public void addChild(ArtifactStatusReport artifactStatusReport) {
        artifactsReport.add(artifactStatusReport);
    }


    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder sb;
        if (indent != null && indent.length() > 0) {
            sb = new StringBuilder("\n");
            sb.append(indent);
            sb.append("");
        } else {
            sb = new StringBuilder();
        }

        sb.append("Artifact[name=");
        sb.append(name);
        sb.append(", uri=");
        sb.append(uri);
        sb.append(", totalTime=");
        sb.append(totalTime);
        sb.append("]");
        for (FacetInfo facetInfo : facetInfos) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Facet[name=");
            sb.append(facetInfo.getName());
            sb.append(", addedBy=");
            sb.append(facetInfo.getProcessor());
            sb.append(", duration=");
            sb.append(facetInfo.getTime());
            sb.append(" ms");
            sb.append("]");
        }
        for (ProcessorInfo processorInfo : processors) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Processor[name=");
            sb.append(processorInfo.getName());
            sb.append(", phase=");
            sb.append(processorInfo.getPhase());
            sb.append(", duration=");
            sb.append(processorInfo.getTime());
            sb.append(" ms");
            sb.append("]");
        }
        for (Exception exception : exceptions) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Exception[message=");
            sb.append(exception.getMessage());
            if (exception.getCause() != null) {
                sb.append(", cause=");
                sb.append(exception.getCause().getMessage());
            }
            sb.append("]");
        }
        if (artifactsReport.size() > 0) {
            for (ArtifactStatusReport artifactStatusReport : artifactsReport) {
                sb.append(artifactStatusReport.toString(indent + "  "));
            }
        }


        return sb.toString();

    }



}
