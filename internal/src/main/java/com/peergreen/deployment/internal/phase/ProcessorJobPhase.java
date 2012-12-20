package com.peergreen.deployment.internal.phase;

import java.util.List;

import com.peergreen.deployment.internal.processor.ProcessorJob;
import com.peergreen.deployment.internal.processor.WrapDeploymentContextJob;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.group.Group;

public class ProcessorJobPhase {

    private final Pipeline pipeline;

    private final String phase;

    private final List<Group> groups;

    public ProcessorJobPhase(String phase, Pipeline pipeline) {
        this(phase, pipeline, null);
    }

    public ProcessorJobPhase(String phase, Pipeline pipeline, List<Group> groups) {
        this.phase = phase;
        this.pipeline = pipeline;
        this.groups = groups;
        addProcessors();
    }

    public ProcessorJobPhase(String phase) {
        this(phase, new Pipeline("Processors_".concat(phase)));
    }

    /**
     * Needs to add each unit of work in a group.
     */
    public void addProcessors() {
        Parallel preparePhases = new Parallel("PREPARE".concat(phase));
        this.pipeline.add(preparePhases);

        String[] PHASES = new String[] {"PRE_".concat(phase), phase, "POST_".concat(phase)};
        for (String subphase : PHASES) {
            Parallel parallelSubPhase = new Parallel(subphase);
            if (groups != null) {
                for (Group group : groups) {
                    UnitOfWork unitOfWork = new UnitOfWork(new WrapDeploymentContextJob(new ProcessorJob(subphase, parallelSubPhase, group)), "PREPARE_".concat(subphase));
                    preparePhases.add(unitOfWork);
                    group.addTask(unitOfWork);
                }
            } else {
                UnitOfWork unitOfWork = new UnitOfWork(new WrapDeploymentContextJob(new ProcessorJob(subphase, parallelSubPhase)), "PREPARE_".concat(subphase));
                preparePhases.add(unitOfWork);
            }
            this.pipeline.add(parallelSubPhase);
        }
    }



    public Pipeline getPipeline() {
        return pipeline;
    }

}
