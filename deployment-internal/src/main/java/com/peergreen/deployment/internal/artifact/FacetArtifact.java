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

package com.peergreen.deployment.internal.artifact;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.processor.DefaultProcessorInfo;
import com.peergreen.deployment.internal.processor.InternalProcessor;
import com.peergreen.deployment.internal.resource.ProviderResource;

/**
 * Artifact on which we can add facets
 * This artifact is used internally by the deployment.
 * @author Florent Benoit
 */
public class FacetArtifact extends ProviderResource implements IFacetArtifact {

    private final List<Exception> exceptions;

    private static final Log LOGGER = LogFactory.getLog(FacetArtifact.class);

    private final Artifact wrappedArtifact;

    private final Map<Class<?>, Object> facets;

    private final String name;
    private final URI uri;

    private final Map<Class<?>, FacetInfo> facetInfos;
    private final Set<ProcessorInfo> processorInfos;

    private long totalTime = 0;

    public FacetArtifact(Artifact wrappedArtifact) {
        this.wrappedArtifact = wrappedArtifact;
        this.name = wrappedArtifact.name();
        this.uri = wrappedArtifact.uri();
        this.facets = new HashMap<Class<?>, Object>();
        this.facetInfos = new HashMap<Class<?>, FacetInfo>();
        this.processorInfos = new HashSet<ProcessorInfo>();
        this.exceptions = new ArrayList<Exception>();
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public <Facet> Facet as(Class<Facet> facetClass) {
        Facet found = facetClass.cast(facets.get(facetClass));

        // ask the wrapped artifact (if any) if not found
        if (found == null && wrappedArtifact != null) {
            return wrappedArtifact.as(facetClass);
        }
        return found;
    }

    @Override
    public <Facet> Facet removeFacet(Class<Facet> facetClass) {
        facetInfos.remove(facetClass);
        return facetClass.cast(facets.remove(facetClass));
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet, InternalProcessor processor) {


        DefaultFacetInfo facetInfo = new DefaultFacetInfo();
        facetInfo.setName(facetClass.getName());
        facetInfo.setProcessor(processor.getName());
        facetInfos.put(facetClass, facetInfo);

        //LOGGER.info("Facet ''{0}'' added by processor ''{1}''", facet, processor.getName());
        facets.put(facetClass, facet);
    }

    // Register the given facet object with all the interfaces and super interfaces of this object
    protected <Facet> void addInnerFacet(Class<?> facetClass, Facet facet, InternalProcessor processor) {
        // for all interfaces
        Class<?>[] interfaces = facetClass.getInterfaces();
        if (interfaces != null) {
            for (Class<?> itf : interfaces) {
                addInnerFacet(itf, itf.cast(facet), processor);
            }
        }

        DefaultFacetInfo facetInfo = new DefaultFacetInfo();
        facetInfo.setName(facetClass.getName());
        if (processor != null) {
        facetInfo.setProcessor(processor.getName());
        facetInfos.put(facetClass, facetInfo);
        }

        // add current interface
        facets.put(facetClass, facet);
    }


    @Override
    public <Facet> void addFacet(Facet facet, InternalProcessor processor) {
        if (facet == null) {
            throw new IllegalArgumentException("Invalid null facet");
        }
        Class<?> facetClass = facet.getClass();
        addInnerFacet(facetClass, facet, processor);
    }

    @Override
    public void addProcessorTime(String phase, long totalTime, InternalProcessor processor) {
        processorInfos.add(new DefaultProcessorInfo(phase, processor.getName(), totalTime));
    }

    @Override
    public Collection<FacetInfo> getFacets() {
        return facetInfos.values();
    }

    @Override
    public Collection<ProcessorInfo> getProcessors() {
        return processorInfos;
    }

    @Override
    public void addTime(long time) {
        totalTime += time;
    }

    @Override
    public long getTotalTime() {
        return totalTime;
    }

    @Override
    public void addException(Exception e) {
        exceptions.add(e);
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }
}
