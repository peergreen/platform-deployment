package com.peergreen.deployment.internal.phase;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.Task;
import com.peergreen.tasks.model.group.Group;

public class DiscoveryPhase {

    private final Pipeline innerPipeline;

    private final Parallel parallelDiscovery;

    private final Pipeline postConfiguration;


    public DiscoveryPhase() {
        this.innerPipeline = new Pipeline("DiscoveryPhase");
        this.parallelDiscovery = new Parallel("DiscoveryPhase_Parallel");
        this.postConfiguration = new Pipeline("DiscoveryPhase_PostConfiguration");

        innerPipeline.addFirst(parallelDiscovery);
        innerPipeline.addLast(postConfiguration);
    }

    public void addPhaseForEachGroup(Group group) {

        // Create analyzer for the given group/artifact
        Pipeline artifactAnalyzer = new Pipeline("Artifact_Analyzer");
        // ... and register analyzer into the group
        group.addTask(artifactAnalyzer);

        // Now inside the analyzer pipeline, we've to add each phase
        for (DiscoveryPhasesLifecycle phase : DiscoveryPhasesLifecycle.values()) {
            ProcessorJobPhase processorJobPhase = new ProcessorJobPhase(phase.toString());
            artifactAnalyzer.add(processorJobPhase.getPipeline());
        }

        // Add it to our parallel stuff
        parallelDiscovery.add(artifactAnalyzer);

    }

    public Task getTask() {
        return innerPipeline;
    }

    public Pipeline getPostConfigurationTask() {
        return postConfiguration;
    }


}
