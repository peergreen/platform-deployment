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

package com.peergreen.deployment.internal.phase.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.adapter.ArtifactFacetAdapter;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.deployment.internal.context.BasicProcessorContext;
import com.peergreen.deployment.internal.deploymentmode.adapter.DeploymentModeFacetAdapter;
import com.peergreen.deployment.internal.model.ArtifactModelManager;
import com.peergreen.deployment.internal.model.Created;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.phase.DiscoveryPhase;
import com.peergreen.deployment.internal.phase.Phases;
import com.peergreen.deployment.internal.phase.ProcessorJobPhase;
import com.peergreen.deployment.internal.phase.job.DeployerCreationJob;
import com.peergreen.deployment.internal.service.InjectionContext;
import com.peergreen.deployment.model.WireType;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.deploymentmode.DeploymentModeCapability;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.group.MutableExecutionContext;

public class DeploymentBuilder {


    private final InjectionContext injectionContext;

    private final ArtifactModelManager artifactModelManager;

    private final FacetCapabilityAdapter<Artifact> artifactFacetAdapter;

    private final FacetCapabilityAdapter<DeploymentMode> deploymentModeFacetCapabilityAdapter;

    public DeploymentBuilder(ArtifactModelManager artifactModelManager, InjectionContext injectionContext) {
        this.artifactModelManager = artifactModelManager;
        this.injectionContext = injectionContext;
        this.artifactFacetAdapter = new ArtifactFacetAdapter();
        this.deploymentModeFacetCapabilityAdapter = new DeploymentModeFacetAdapter();
    }


    public Task buildTaskModel(List<Artifact> artifacts, DeploymentMode deploymentMode, TaskExecutionHolder taskExecutionHolder, InternalArtifactModel rootArtifactModel) {
        long tStart = System.currentTimeMillis();

        // Build a new deployment named Phases
        Phases phases = new Phases("Phases");

        // List of groups for the taskcontext factory
        Collection<Group> allgroups = taskExecutionHolder.getGroups();

        // One provider for switching the DeploymentContext for selected
        // group/job
        Set<Group> artifactGroups = new HashSet<Group>();
        List<DeploymentContext> deploymentContexts = new ArrayList<DeploymentContext>();

        // For each artifact, build a deployment context and the associated group
        for (Artifact artifact : artifacts) {

            // Create group
            Group artifactGroup = new Group(artifact.uri().toString());
            artifactGroups.add(artifactGroup);

            // Create mutable context
            MutableExecutionContext mutableExecutionContext = new MutableExecutionContext();

            // inject OSGi services into the execution context
            injectionContext.addInjection(mutableExecutionContext);

            // add the artifact group
            taskExecutionHolder.getSubstituteExecutionContextProvider().addGroup(artifactGroup, mutableExecutionContext);

            // Existing Artifact model
            DefaultArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());


            boolean createdArtifactModel = false;

            // No model yet
            if (artifactModel == null) {

                // Create a Facet Artifact
                IFacetArtifact facetArtifact = new FacetArtifact(artifact);

                // creating a model
                createdArtifactModel = true;
                artifactModel = new DefaultArtifactModel(facetArtifact);

                // register it
                artifactModelManager.addArtifactModel(artifact.uri(), artifactModel);

            }
            // model already exists, update it

            // do we have a parent ?
            if (deploymentMode  == DeploymentMode.DEPLOY && rootArtifactModel != null) {
                // we have a parent that want to use this artifact, add a link to this one
                InternalWire wire = new DefaultWire(rootArtifactModel, artifactModel, WireType.USE);
                // wire for creating node ? add created flag
                if (createdArtifactModel) {
                    wire.addFlag(Created.class);
                }
                // Add wire
                rootArtifactModel.addWire(wire);

                // and reverse order (bi-directional link)
                artifactModel.addWire(wire);

            }


            // Artifact is retrieved from the model
            IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();

            // Build deployment context around the facet artifact
            BasicDeploymentContext deploymentContext = new BasicDeploymentContext(facetArtifact,
                    mutableExecutionContext);


            // Add the artifact capability
            ArtifactCapability artifactCapability = artifactFacetAdapter.getCapability(facetArtifact, artifact);
            facetArtifact.addCapability(artifactCapability);

            // Add deployment builder
            deploymentContext.add(this);

            // Add artifact model
            deploymentContext.add(artifactModel);

            // Add task holder
            deploymentContext.add(taskExecutionHolder);

            // Build processor context
            ProcessorContext processorContext = new BasicProcessorContext(deploymentContext);
            deploymentContext.add(processorContext);

            deploymentContexts.add(deploymentContext);

            // add the Group inside the deployment context
            deploymentContext.add(artifactGroup);

            // Add the deployment mode capability
            deploymentContext.add(deploymentMode);
            DeploymentModeCapability deploymentModeCapability = deploymentModeFacetCapabilityAdapter.getCapability(
                    deploymentContext, deploymentMode);
            deploymentContext.addCapability(deploymentModeCapability);

            // sets deployment context as a property of the execution context
            mutableExecutionContext.add(deploymentContext);

        }

        // Add discovery phase only for deploy mode
        if (deploymentMode == DeploymentMode.DEPLOY) {
            phases.add(getDiscoveryPhase(artifactGroups));
        }

        // It will send the artifacts to the registered deployer (with the associated lifecycle)
        Pipeline deployers = new Pipeline("deployers");

        MutableExecutionContext deployerExecutionContext = new MutableExecutionContext();
        // inject OSGi services into the mutableExecutionContext
        injectionContext.addInjection(deployerExecutionContext);
        deployerExecutionContext.setProperty("deploymentContexts", deploymentContexts);
        deployerExecutionContext.add(deploymentMode);
        // Add ourself
        deployerExecutionContext.add(this);

        Group deployerGroup = new Group("deployer");
        allgroups.add(deployerGroup);
        taskExecutionHolder.getSubstituteExecutionContextProvider().addGroup(deployerGroup, deployerExecutionContext);
        UnitOfWork deployerUnitOfWork = new UnitOfWork(new DeployerCreationJob(deployers),
                "Deployer Creation Job");
        deployerGroup.addTask(deployerUnitOfWork);
        phases.add(deployerUnitOfWork);

        phases.add(deployers);

        // Create the task context factory
        allgroups.addAll(artifactGroups);

        // return phases
        return phases;
    }

    protected Task getDiscoveryPhase(Iterable<Group> groups) {
        // Needs to prepare a pipeline for the deployment
        DiscoveryPhase discoveryPhase = new DiscoveryPhase();
        for (Group group : groups) {
            discoveryPhase.addPhaseForEachGroup(group);
        }
        return discoveryPhase.getTask();

    }

    public Task getDeploymentPhases(String deploymentName, List<String> phases, List<Group> groups) {

        // Create root pipeline
        Pipeline pipeline = new Pipeline(deploymentName);

        // no phases ?
        if (phases == null) {
            return pipeline;
        }

        // In a deployment, all phases are parallel
        for (String phaseName : phases) {
            new ProcessorJobPhase(phaseName, pipeline, groups);
        }

        return pipeline;
    }

}
