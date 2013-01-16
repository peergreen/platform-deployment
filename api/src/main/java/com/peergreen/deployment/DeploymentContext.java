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

package com.peergreen.deployment;

import java.util.List;

import org.osgi.resource.Capability;


public interface DeploymentContext {

        /**
         * @return artifact on which we're working
         */
        Artifact getArtifact();


        // Manage properties
        <T> T get(Class<T> type);
        void remove(Object instance);
        void add(Object instance);

        /**
         * New artifacts to deploy
         */
        void addArtifact(Artifact artifact);
        void addArtifact(List<Artifact> artifacts);

        ArtifactBuilder getArtifactBuilder();

        // facets
        <Facet> Facet removeFacet(Class<Facet> facetClass);
        <Facet> void addFacet(Class<Facet> facetClass, Facet facet);
        <Facet> void addFacet(Class<Facet> facetClass, Facet facet, FacetCapabilityAdapter<Facet> facetAdapter);


        List<Capability> getCapabilities(String namespace);

        void setProperty(String name, Object value);
        Object getProperty(String name);


        // Errors/Exceptions from the deployment
        boolean hasFailed();
}
