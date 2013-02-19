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
package com.peergreen.deployment.internal.facet.osgibundle.persistence;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.peergreen.deployment.DelegatePersistenceArtifactManager;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

@Component
@Provides
@Instantiate
public class OSGiDelegatePersistenceArtifactManager extends DelegatePersistenceArtifactManager {

    public OSGiDelegatePersistenceArtifactManager(BundleContext bundleContext) throws ProcessorException {
        super(new OSGiPersitenceArtifactManager(bundleContext));
    }

    @Override
    @Bind(optional=false)
    public void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        super.bindRequirementBuilder(requirementBuilder);
    }

    @Validate
    protected void addRequirements() {

        // Wants to be notified only on artifacts with Bundle type
        addRequirement(getRequirementBuilder().buildFacetRequirement(this, Bundle.class));

    }

}
