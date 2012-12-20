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

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetLifeCyclePhaseProvider;
import com.peergreen.deployment.InternalFacetLifeCyclePhaseProvider;

public class DelegateInternalLifeCyclePhaseProvider<T> implements InternalFacetLifeCyclePhaseProvider {

    private final Class<T> facetType;

    private final FacetLifeCyclePhaseProvider<T> wrappedFacetLifeCyclePhaseProvider;

    public DelegateInternalLifeCyclePhaseProvider(FacetLifeCyclePhaseProvider<T> wrappedFacetLifeCyclePhaseProvider, Class<T> facetType) {
        this.wrappedFacetLifeCyclePhaseProvider = wrappedFacetLifeCyclePhaseProvider;
        this.facetType = facetType;
    }

    @Override
    public Class<?> getFacetType() {
        return facetType;
    }

    @Override
    public List<String> getLifeCyclePhases(DeploymentMode deploymentMode) {
        return wrappedFacetLifeCyclePhaseProvider.getLifeCyclePhases(deploymentMode);
    }


}
