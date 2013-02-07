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

import java.util.Collection;
import java.util.List;

import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.processor.NamedProcessor;

/**
 * Allows to modify the artifact
 *
 * @author Florent Benoit
 */
public interface IFacetArtifact extends Artifact, Resource {

    <Facet> Facet removeFacet(Class<Facet> facetClass);
    <Facet> void addFacet(Class<Facet> facetClass, Facet facet, NamedProcessor processor);
    <Facet> void addFacet(Facet facet, NamedProcessor processor);


    void addProcessorTime(String phase, long totalTime, NamedProcessor processor);
    void addTime(long time);
    long getTotalTime();

    void addCapability(Capability capability);

    void addException(Exception e);
    List<Exception> getExceptions();


    Collection<FacetInfo> getFacets();
    Collection<ProcessorInfo> getProcessors();
    List<FacetBuilderReference> getFacetBuilders();

}
