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
package com.peergreen.deployment.internal.phase.lifecycle;

import java.util.HashSet;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import com.peergreen.deployment.InternalFacetLifeCyclePhaseProvider;

@Component
@Provides
@Instantiate
public class DefaultFacetLifeCyclePhaseManager implements FacetLifeCycleManager {

    private final Set<InternalFacetLifeCyclePhaseProvider> providers;

    public DefaultFacetLifeCyclePhaseManager() {
        this.providers = new HashSet<InternalFacetLifeCyclePhaseProvider>();
    }

    @Override
    public Iterable<InternalFacetLifeCyclePhaseProvider> getProviders() {
        return providers;
    }



    @Bind(aggregate=true,optional=true)
    public void bindProvider(InternalFacetLifeCyclePhaseProvider provider) {
        this.providers.add(provider);
    }

    @Unbind(aggregate=true,optional=true)
    public void unbindProvider(InternalFacetLifeCyclePhaseProvider provider) {
        this.providers.remove(provider);
    }

}

