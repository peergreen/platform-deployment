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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.processor.DefaultProcessorInfo;
import com.peergreen.deployment.internal.processor.NamedProcessor;
import com.peergreen.deployment.internal.resource.ProviderResource;

/**
 * Artifact on which we can add facets
 * This artifact is used internally by the deployment.
 * @author Florent Benoit
 */
public class FacetArtifact extends ProviderResource implements IFacetArtifact {

    private static final Log LOGGER = LogFactory.getLog(FacetArtifact.class);

    /**
     * Name of this artifact.
     */
    private final String name;

    /**
     * URI of this artifact.
     */
    private final URI uri;

    /**
     * Wrapped artifact.
     */
    private final Artifact wrappedArtifact;

    private final Map<Class<?>, Object> facets;

    private final Stack<FacetArtifactData> dataPerDeployment;

    public FacetArtifact(Artifact wrappedArtifact) {
        this.wrappedArtifact = wrappedArtifact;
        this.name = wrappedArtifact.name();
        this.uri = wrappedArtifact.uri();
        this.dataPerDeployment = new Stack<>();
        this.facets = new HashMap<>();
        // be ready
        newDeploymentMode();
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

        // ask the wrapped artifactif not found
        if (found == null) {
            return wrappedArtifact.as(facetClass);
        }
        return found;
    }

    @Override
    public <Facet> Facet removeFacet(Class<Facet> facetClass) {
        getCurrentData().removeFacetInfo(facetClass);
        return facetClass.cast(facets.remove(facetClass));
    }

    @Override
    public <Facet> void addFacet(Class<Facet> facetClass, Facet facet, NamedProcessor processor) {


        DefaultFacetInfo facetInfo = new DefaultFacetInfo();
        facetInfo.setFacetClass(facetClass);
        facetInfo.setName(facetClass.getName());
        if (processor != null) {
            facetInfo.setProcessor(processor.getName());
        }
        getCurrentData().getFacetInfos().add(facetInfo);

        //LOGGER.info("Facet ''{0}'' added by processor ''{1}''", facet, processor.getName());
        facets.put(facetClass, facet);
    }

    // Register the given facet object with all the interfaces and super interfaces of this object
    protected <Facet> void addInnerFacet(Class<?> facetClass, Facet facet, NamedProcessor processor) {
        // for all interfaces
        Class<?>[] interfaces = facetClass.getInterfaces();
        for (Class<?> itf : interfaces) {
            addInnerFacet(itf, itf.cast(facet), processor);
        }

        DefaultFacetInfo facetInfo = new DefaultFacetInfo();
        facetInfo.setName(facetClass.getName());
        facetInfo.setFacetClass(facetClass);
        if (processor != null) {
            facetInfo.setProcessor(processor.getName());
            getCurrentData().getFacetInfos().add(facetInfo);
        }

        // add current interface
        facets.put(facetClass, facet);
    }


    @Override
    public <Facet> void addFacet(Facet facet, NamedProcessor processor) {
        if (facet == null) {
            throw new IllegalArgumentException("Invalid null facet");
        }
        Class<?> facetClass = facet.getClass();
        addInnerFacet(facetClass, facet, processor);
    }

    @Override
    public void addProcessorTime(String phase, long totalTime, NamedProcessor processor) {
        getCurrentData().getProcessors().add(new DefaultProcessorInfo(phase, processor.getName(), totalTime));
    }

    @Override
    public Map<Class<?>, Object> getFacets() {
        return facets;
    }


    @Override
    public List<FacetInfo> getFacetInfos() {
        return getCurrentData().getFacetInfos();
    }

    @Override
    public List<ProcessorInfo> getProcessors() {
        return getCurrentData().getProcessors();
    }

    @Override
    public void addTime(long time) {
        getCurrentData().addTime(time);
    }

    @Override
    public long getTotalTime() {
        return getCurrentData().getTotalTime();
    }

    @Override
    public void addException(Throwable e) {
        getCurrentData().addException(e);
    }

    @Override
    public List<Throwable> getExceptions() {
        return getCurrentData().getExceptions();
    }

    @Override
    public List<InternalFacetBuilderInfo> getFacetBuilders() {
        return getCurrentData().getFacetBuilders();
    }

    /**
     * Adds a new stack
     */
    @Override
    public void newDeploymentMode() {
        dataPerDeployment.push(new FacetArtifactData());
    }


    protected FacetArtifactData getCurrentData() {
        return dataPerDeployment.peek();
    }

    @Override
    public void reset() {
        // remove facets
        facets.clear();

        // remove capabilities
        getInnerCapabilities().clear();

    }
}
