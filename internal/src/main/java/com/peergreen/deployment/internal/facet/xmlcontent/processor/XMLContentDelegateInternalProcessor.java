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
package com.peergreen.deployment.internal.facet.xmlcontent.processor;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.deployment.DelegateHandlerProcessor;
import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

@Component
@Provides
@Instantiate(name="xml content facet scanner")
public class XMLContentDelegateInternalProcessor extends DelegateHandlerProcessor<Content> {

    public XMLContentDelegateInternalProcessor() throws ProcessorException {
        super(new XMLContentProcessor(), Content.class);
    }


    @Override
    @Bind(optional=false)
    public void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        super.bindRequirementBuilder(requirementBuilder);
    }

    @Validate
    protected void addRequirements() {

        // Execute only on artifacts with XML path extension
        addRequirement(getRequirementBuilder().buildArtifactRequirement(this).setPathExtension("xml"));

        // Execute only on a content
        addRequirement(getRequirementBuilder().buildContentRequirement(this));

        // Execute at the FACET_SCANNER lifecycle
        addRequirement(getRequirementBuilder().buildPhaseRequirement(this, DiscoveryPhasesLifecycle.FACET_SCANNER.toString()));

    }

}
