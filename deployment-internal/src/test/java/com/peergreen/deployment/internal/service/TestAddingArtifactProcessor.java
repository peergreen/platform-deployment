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
package com.peergreen.deployment.internal.service;

import java.net.URI;
import java.net.URISyntaxException;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;

public class TestAddingArtifactProcessor implements Processor<DeploymentContext> {

    private final String prefix;

    public TestAddingArtifactProcessor(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public void handle(DeploymentContext deploymentContext, ProcessorContext processorContext) throws ProcessorException {

        Artifact artifact = deploymentContext.getArtifact();

        // could be done with requirements
        if (artifact.name().endsWith(".xml")) {
            // Add 15 dependencies
            for (int i = 0; i < 15; i++) {
                processorContext.addArtifact(build(prefix.concat(String.valueOf(i))));
            }
            processorContext.addFacet(XmlPlanFacet.class, new XmlPlanFacet());

        }
    }


    protected Artifact build(String name) throws ProcessorException {
        try {
            return new ImmutableArtifact(name, new URI("test:" + name + ".jar"));
        } catch (URISyntaxException e) {
            throw new ProcessorException("Unable to add dependency", e);
        }
    }


}
