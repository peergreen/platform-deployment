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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.facet.endpoint.Endpoint;
import com.peergreen.deployment.facet.endpoint.Endpoints;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.report.ArtifactError;
import com.peergreen.deployment.report.ArtifactErrorDetail;
import com.peergreen.deployment.report.ArtifactStatusReport;

public class DefaultArtifactStatusReport implements ArtifactStatusReport {

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
    private final URI uri;
    private final List<FacetInfo> facetInfos;
    private final List<ArtifactError> exceptions;
    private final List<ProcessorInfo> processors;
    private final List<ArtifactStatusReport> artifactsReport;
    private final long totalTime;

    @Override
    public List<ArtifactError> getExceptions() {
        return exceptions;
    }

    @Override
    public List<ArtifactStatusReport> children() {
        return artifactsReport;
    }


    @Override
    public List<ProcessorInfo> getProcessors() {
        return processors;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public List<FacetInfo> getFacets() {
        return facetInfos;
    }

    private final List<Endpoint> endpoints;


    public DefaultArtifactStatusReport(IFacetArtifact facetArtifact) {
        this.name = facetArtifact.name();
        this.uri = facetArtifact.uri();
        this.facetInfos = facetArtifact.getFacetInfos();
        this.artifactsReport = new ArrayList<>();
        this.processors = facetArtifact.getProcessors();
        this.totalTime = facetArtifact.getTotalTime();

        Endpoints endpointService = facetArtifact.as(Endpoints.class);
        if (endpointService != null) {
            this.endpoints = endpointService.list();
        } else {
            this.endpoints = Collections.emptyList();
        }

        // convert exception
        this.exceptions = new ArrayList<>();
        List<Throwable> throwables = facetArtifact.getExceptions();
        for (Throwable throwable : throwables) {
            ArtifactError artifactError = new DefaultArtifactError(throwable);
            exceptions.add(artifactError);
        }
    }

    public void addChild(DefaultArtifactStatusReport artifactStatusReport) {
        artifactsReport.add(artifactStatusReport);
    }


    @Override
    public String toString() {
        return toString("");
    }

    @Override
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
        sb.append(", totalTime='");
        sb.append(totalTime);
        sb.append("' ms");
        sb.append("]");
        for (FacetInfo facetInfo : facetInfos) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Facet[name=");
            sb.append(facetInfo.getName());
            sb.append(", addedBy=");
            sb.append(facetInfo.getProcessor());
            sb.append("]");
        }
        for (Endpoint endpoint : endpoints) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Endpoint[uri=");
            sb.append(endpoint.getURI());
            sb.append(" ");
            sb.append(endpoint.getCategories());
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
            sb.append(", duration='");
            sb.append(processorInfo.getTime());
            sb.append("' ms");
            sb.append("]");
        }
        for (ArtifactError artifactError : exceptions) {
            sb.append("\n");
            sb.append(indent);
            sb.append("  |-");
            sb.append("Exception[");
            String stackIndent = indent;
            for (ArtifactErrorDetail artifactErrorDetail : artifactError.getDetails()) {
                sb.append("message=");
                sb.append(artifactErrorDetail.getMessage());
                printStackTrace(artifactErrorDetail.getStackTrace(), sb, stackIndent);
                stackIndent = stackIndent.concat("  ");
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

    protected void printStackTrace(StackTraceElement[] stackTrace, StringBuilder sb, String indent) {
        if (stackTrace.length > 0) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                sb.append(System.lineSeparator());
                sb.append(indent);
                sb.append("  ");
                sb.append("  |-");
                sb.append(stackTraceElement.getClassName());
                sb.append(".");
                sb.append(stackTraceElement.getMethodName());
                sb.append("(");
                if (!stackTraceElement.isNativeMethod()) {
                    sb.append(stackTraceElement.getFileName());
                    sb.append(":");
                    sb.append(stackTraceElement.getLineNumber());
                } else {
                    sb.append("Native Method");
                }
                sb.append(")");
            }
            sb.append(System.lineSeparator());
        }
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }



}
