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
package com.peergreen.deployment.internal.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;

/**
 * Data associated to a deployment (DEPLOY, UPDATE, UNDEPLOY, etc)
 * @author Florent Benoit
 */
public class FacetArtifactData {

    private final List<Throwable> exceptions;
    private final Map<Class<?>, FacetInfo> facetInfos;
    private final Set<ProcessorInfo> processorInfos;
    private final List<InternalFacetBuilderInfo> facetBuildersInfo;
    private long totalTime = 0;


    public FacetArtifactData() {
        this.facetInfos = new HashMap<>();
        this.processorInfos = new HashSet<>();
        this.facetBuildersInfo = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }

    protected Map<Class<?>, FacetInfo> getFacetInfosMap() {
        return facetInfos;
    }

    protected Collection<FacetInfo> getFacetInfos() {
        return facetInfos.values();
    }

    protected Collection<ProcessorInfo> getProcessors() {
        return processorInfos;
    }

    protected void addTime(long time) {
        totalTime += time;
    }

    protected long getTotalTime() {
        return totalTime;
    }

    protected void addException(Throwable e) {
        exceptions.add(e);
    }

    protected List<Throwable> getExceptions() {
        return exceptions;
    }

    protected List<InternalFacetBuilderInfo> getFacetBuilders() {
        return facetBuildersInfo;
    }
}
