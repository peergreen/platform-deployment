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
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.adapter.ArtifactFacetAdapter;
import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.deployment.internal.context.BasicProcessorContext;
import com.peergreen.deployment.internal.deploymentmode.adapter.DeploymentModeFacetAdapter;
import com.peergreen.deployment.internal.model.DefaultArtifactModel;
import com.peergreen.deployment.internal.model.DefaultWire;
import com.peergreen.deployment.internal.model.InternalArtifactModel;
import com.peergreen.deployment.internal.model.InternalArtifactModelManager;
import com.peergreen.deployment.internal.model.InternalWire;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelDeploymentView;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelPersistenceView;
import com.peergreen.deployment.internal.phase.DiscoveryPhase;
import com.peergreen.deployment.internal.phase.InternalPhases;
import com.peergreen.deployment.internal.phase.Phases;
import com.peergreen.deployment.internal.phase.ProcessorJobPhase;
import com.peergreen.deployment.internal.phase.job.DeployerCreationJob;
import com.peergreen.deployment.internal.phase.job.NewArtifactsDiscoveryCreationJob;
import com.peergreen.deployment.internal.service.InjectionContext;
import com.peergreen.deployment.model.ArtifactModelDeploymentState;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.flag.Created;
import com.peergreen.deployment.model.flag.Use;
import com.peergreen.deployment.resource.artifact.ArtifactCapability;
import com.peergreen.deployment.resource.deploymentmode.DeploymentModeCapability;
import com.peergreen.tasks.model.Container;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.group.MutableExecutionContext;

/**
 * Deployments Builder creates the task model that will be executed in order to deploy/update/undeploy a given set of artifacts.
 * @author Florent Benoit
 */
public class DeploymentBuilder {

    private final InjectionContext injectionContext;

    private final InternalArtifactModelManager artifactModelManager;

    private final FacetCapabilityAdapter<Artifact> artifactFacetAdapter;

    private final FacetCapabilityAdapter<DeploymentMode> deploymentModeFacetCapabilityAdapter;

    public DeploymentBuilder(InternalArtifactModelManager artifactModelManager, InjectionContext injectionContext) {
        this.artifactModelManager = artifactModelManager;
        this.injectionContext = injectionContext;
        this.artifactFacetAdapter = new ArtifactFacetAdapter();
        this.deploymentModeFacetCapabilityAdapter = new DeploymentModeFacetAdapter();
    }

    public Task buildTaskModel(TaskModelParameters parameters) {

        Collection<ArtifactProcessRequest> taskArtifacts = parameters.getArtifactProcessRequests();
        DeploymentMode deploymentMode = parameters.getDeploymentMode();
        TaskExecutionHolder taskExecutionHolder = parameters.getTaskExecutionHolder();
        InternalArtifactModel rootArtifactModel = parameters.getRootArtifactModel();



        // Build a new deployment named Phases
        Phases phases = new Phases("Phases");

        // List of groups for the taskcontext factory
        Collection<Group> allgroups = taskExecutionHolder.getGroups();

        // In UNDEPLOY mode, needs to find all dependencies of the given artifacts
        List<ArtifactProcessRequest> artifactProcessRequests;

        if (DeploymentMode.UNDEPLOY == deploymentMode) {
            artifactProcessRequests = new ArrayList<ArtifactProcessRequest>();
            for (ArtifactProcessRequest artifactProcessRequest : taskArtifacts) {
                checkUndeployArtifactProcessRequest(artifactProcessRequest);
                addInnerArtifacts(artifactProcessRequests, artifactProcessRequest, artifactModelManager);
            }
        } else {
            artifactProcessRequests = new ArrayList<>();
            // Check
            if (DeploymentMode.DEPLOY == deploymentMode) {
                for (ArtifactProcessRequest artifactProcessRequest : taskArtifacts) {
                    checkDeployArtifactProcessRequest(artifactProcessRequest);
                }
            }
            artifactProcessRequests.addAll(taskArtifacts);
        }


        // One provider for switching the DeploymentContext for selected
        // group/job
        Set<Group> artifactGroups = new HashSet<Group>();
        List<BasicDeploymentContext> deploymentContexts = taskExecutionHolder.getDeploymentContexts();

        // For each artifact, build a deployment context and the associated group
        for (ArtifactProcessRequest artifactProcessRequest : artifactProcessRequests) {

            Artifact artifact = artifactProcessRequest.getArtifact();

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
            InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());

            boolean createdArtifactModel = false;

            // No model yet, create it
            if (artifactModel == null) {

                // Create a Facet Artifact
                IFacetArtifact facetArtifact = new FacetArtifact(artifact);

                // creating a model
                createdArtifactModel = true;
                artifactModel = new DefaultArtifactModel(facetArtifact);

                // register it
                artifactModelManager.addArtifactModel(artifact.uri(), artifactModel);

            } else {
                // start a new deployment mode
                artifactModel.getFacetArtifact().newDeploymentMode();
            }
            // model is available from now

            // do we have a parent ?
            if ((deploymentMode == DeploymentMode.DEPLOY)) {
                // Set persistent mode
                artifactModel.as(InternalArtifactModelPersistenceView.class).setPersistent(artifactProcessRequest.isPersistent());

                if ((rootArtifactModel != null)) {
                    // we have a parent that want to use this artifact, add a link to this one
                    InternalWire wire = new DefaultWire(rootArtifactModel, artifactModel);

                    // Flag mode
                    wire.setAttribute(Use.class.getName());

                    // first order
                    rootArtifactModel.addWire(wire);

                    // and reverse order (bi-directional link)
                    artifactModel.addWire(wire);

                    // wire for creating node ? add created flag
                    if (createdArtifactModel) {
                        wire.setAttribute(Created.class.getName());
                    }
                } else {
                    // no parent, this is a root artifact (explicitly deployed)
                    // Mark the model as a deployment-root
                    artifactModel.as(InternalArtifactModelDeploymentView.class).setDeploymentRoot(true);
                }
            }

            if (DeploymentMode.UNDEPLOY == deploymentMode) {
                InternalArtifactModelDeploymentView artifactModelDeploymentView =  artifactModel.as(InternalArtifactModelDeploymentView.class);
                artifactModelDeploymentView.setDeploymentState(ArtifactModelDeploymentState.UNDEPLOYED);
            }

            // Needs to update the lastModified/Length
            if (DeploymentMode.UPDATE == deploymentMode || DeploymentMode.DEPLOY == deploymentMode) {
                // Ask the manager to update this artifact model
                artifactModelManager.updateLengthLastModified(artifactModel);
                InternalArtifactModelDeploymentView artifactModelDeploymentView =  artifactModel.as(InternalArtifactModelDeploymentView.class);
                artifactModelDeploymentView.setDeploymentState(ArtifactModelDeploymentState.DEPLOYED);
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

        // Add new artifacts phase only for deploy mode
        if (deploymentMode == DeploymentMode.DEPLOY) {
            DiscoveryPhase discoveryPhase = getDiscoveryPhase(artifactGroups);
            phases.add(discoveryPhase.getTask());

            // Add job for analysing new artifacts found on discovery phase
            Pipeline newArtifactsPipeline = new Pipeline("analysing_new_artifacts");
            Container discoveryPostNewArtifacts = new Parallel("analysing_new_artifacts");

            MutableExecutionContext artifactsDeployerExecutionContext = new MutableExecutionContext();
            // inject OSGi services into the mutableExecutionContext
            injectionContext.addInjection(artifactsDeployerExecutionContext);
            artifactsDeployerExecutionContext.add(deploymentMode);
            artifactsDeployerExecutionContext.add(taskExecutionHolder);

            // Add ourself
            artifactsDeployerExecutionContext.add(this);

            Group newArtifactsFinderGroup = new Group("new_artifacts_finders");
            allgroups.add(newArtifactsFinderGroup);
            taskExecutionHolder.getSubstituteExecutionContextProvider().addGroup(newArtifactsFinderGroup, artifactsDeployerExecutionContext);
            UnitOfWork newArtifactsDiscoveryUnitOfWork = new UnitOfWork(new NewArtifactsDiscoveryCreationJob(discoveryPostNewArtifacts),
                    "Artifacts Disovery Creation Job");
            newArtifactsFinderGroup.addTask(newArtifactsDiscoveryUnitOfWork);
            newArtifactsPipeline.add(newArtifactsDiscoveryUnitOfWork);
            discoveryPhase.getPostConfigurationTask().add(newArtifactsPipeline);

            // add task where // stuff will be executed
            newArtifactsPipeline.add(discoveryPostNewArtifacts);
        }

        if (!taskExecutionHolder.isOnlyDiscoveryPhases()) {
            // It will send the artifacts to the registered deployer (with the associated lifecycle)
            Container deployers = new Pipeline("deployers");

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
        }


        // Add post configuration undeploy phase for removing entries after undeploy mode
        if (deploymentMode == DeploymentMode.UNDEPLOY) {
            Task undeployPostConfigTask = getUndeployPostConfigurationPhase(artifactGroups);
            phases.add(undeployPostConfigTask);
        }


        // Create the task context factory
        allgroups.addAll(artifactGroups);

        // return phases
        return phases;
    }

    protected Task getUndeployPostConfigurationPhase(Set<Group> groups) {
        Pipeline postConfig = new Pipeline("UNDEPLOY_POSTCONFIG_PREPARE");
        // Add processors for UNDEPLOY_POST_CONFIG
        new ProcessorJobPhase(InternalPhases.UNDEPLOY_POSTCONFIG.toString(), postConfig, groups, true);
        return postConfig;
    }

    protected DiscoveryPhase getDiscoveryPhase(Iterable<Group> groups) {
        // Needs to prepare a pipeline for the deployment
        DiscoveryPhase discoveryPhase = new DiscoveryPhase();
        for (Group group : groups) {
            discoveryPhase.addPhaseForEachGroup(group);
        }
        return discoveryPhase;

    }

    public Task getDeploymentPhases(String deploymentName, Iterable<String> phases, List<Group> groups, boolean inParallel) {

        // Create root pipeline
        Pipeline pipeline = new Pipeline(deploymentName);

        // no phases ?
        if (phases == null) {
            return pipeline;
        }

        // In a deployment, all phases are parallel
        for (String phaseName : phases) {
            new ProcessorJobPhase(phaseName, pipeline, groups, inParallel);
        }

        return pipeline;
    }

    /**
     * Check that the given artifacts are existing before undeploying them
     * @param artifactProcessRequest
     */
    protected void checkUndeployArtifactProcessRequest(ArtifactProcessRequest artifactProcessRequest) {
        Artifact artifact = artifactProcessRequest.getArtifact();
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());
        if (artifactModel == null) {
            throw new IllegalArgumentException(String.format("Cannot undeploy URI %s which is not a tracked element", artifact.uri()));
        }
        InternalArtifactModelDeploymentView artifactModelDeploymentView =  artifactModel.as(InternalArtifactModelDeploymentView.class);
        // check that this is a root deployment
        if (!artifactModelDeploymentView.isDeploymentRoot()) {
            // not a root deployment so needs to skip it
            throw new IllegalArgumentException(String.format("Cannot undeploy URI %s which is not a root element", artifactModel.getFacetArtifact().uri()));
        }

        if (artifactModelDeploymentView.isUndeployed()) {
            // already undeployed
            throw new IllegalArgumentException(String.format("Cannot undeploy URI %s as it's already undeployed", artifactModel.getFacetArtifact().uri()));
        }


    }

    /**
     * Check that the given artifacts is not existing or in undeployed mode before trying to deploy it again
     * @param artifactProcessRequest
     */
    protected void checkDeployArtifactProcessRequest(ArtifactProcessRequest artifactProcessRequest) {
        Artifact artifact = artifactProcessRequest.getArtifact();
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());
        // not existing, ok
        if (artifactModel == null) {
            return;
        }
        InternalArtifactModelDeploymentView artifactModelDeploymentView = artifactModel.as(InternalArtifactModelDeploymentView.class);
        // check that this is not already deployed
        if (artifactModelDeploymentView.isDeployed()) {
            // not undeployed
            throw new IllegalArgumentException(String.format("Cannot deploy URI %s as it is already in a DEPLOY state", artifactModel.getFacetArtifact().uri()));
        }

    }


    protected void addInnerArtifacts(List<ArtifactProcessRequest> artifactProcessRequests, ArtifactProcessRequest artifactProcessRequest, InternalArtifactModelManager artifactModelManager) {
        // First, add ourself
        if (!artifactProcessRequests.contains(artifactProcessRequest)) {
            artifactProcessRequests.add(artifactProcessRequest);
        }

        Artifact artifact = artifactProcessRequest.getArtifact();

        // Then add all dependencies created by this artifact
        InternalArtifactModel artifactModel = artifactModelManager.getArtifactModel(artifact.uri());
        if (artifactModel != null) {
            for (InternalWire wire : artifactModel.getInternalWires(WireScope.FROM)) {
                InternalArtifactModel child = wire.getInternalTo();
                Artifact childArtifact = child.getFacetArtifact();
                ArtifactProcessRequest childArtifactProcessRequest = new ArtifactProcessRequest(childArtifact);
                childArtifactProcessRequest.setPersistent(child.as(InternalArtifactModelPersistenceView.class).isPersistent());


                addInnerArtifacts(artifactProcessRequests, childArtifactProcessRequest, artifactModelManager);
            }
        }

    }


}