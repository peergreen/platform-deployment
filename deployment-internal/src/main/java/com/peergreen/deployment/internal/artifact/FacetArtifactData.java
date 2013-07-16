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
import java.util.Iterator;
import java.util.List;

import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;

/**
 * Data associated to a deployment (DEPLOY, UPDATE, UNDEPLOY, etc)
 * @author Florent Benoit
 */
public class FacetArtifactData {

    private final List<Throwable> exceptions;
    private final List<FacetInfo> facetInfos;
    private final List<ProcessorInfo> processorInfos;
    private final List<InternalFacetBuilderInfo> facetBuildersInfo;
    private long totalTime = 0;


    public FacetArtifactData() {
        this.facetInfos = new ArrayList<>();
        this.processorInfos = new ArrayList<>();
        this.facetBuildersInfo = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }

    protected List<FacetInfo> getFacetInfos() {
        return facetInfos;
    }

    protected void removeFacetInfo(Class<?> facetClass) {
        Iterator<FacetInfo> iterator = facetInfos.iterator();
        while (iterator.hasNext()) {
            FacetInfo facetInfo = iterator.next();
            if (facetInfo.getName().equals(facetClass.getName())) {
                iterator.remove();
            }
        }
    }

    protected List<ProcessorInfo> getProcessors() {
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
