package com.peergreen.deployment.internal.phase;

import java.util.Collection;

import com.peergreen.deployment.internal.processor.ProcessorJob;
import com.peergreen.deployment.internal.processor.WrapDeploymentContextJob;
import com.peergreen.tasks.model.Parallel;
import com.peergreen.tasks.model.Pipeline;
import com.peergreen.tasks.model.UnitOfWork;
import com.peergreen.tasks.model.group.Group;

public class ProcessorJobPhase {

    private final Pipeline pipeline;

    private final String phase;

    private final Collection<Group> groups;

    public ProcessorJobPhase(String phase, Pipeline pipeline) {
        this(phase, pipeline, null, true);
    }

    public ProcessorJobPhase(String phase, Pipeline pipeline, Collection<Group> groups, boolean parallel) {
        this.phase = phase;
        this.pipeline = pipeline;
        this.groups = groups;
        addProcessors(parallel);
    }

    public ProcessorJobPhase(String phase) {
        this(phase, new Pipeline("Processors_".concat(phase)));
    }

    /**
     * Needs to add each unit of work in a group.
     */
    public void addProcessors(boolean parallel) {
        ContainerTask prepareTask;
        if (!parallel) {
            prepareTask = new PipelineContainerTask(new Pipeline("PREPARE".concat(phase)));
        } else {
            prepareTask = new ParallelContainerTask(new Parallel("PREPARE".concat(phase)));
        }
        this.pipeline.add(prepareTask.getInnerTask());

        String[] PHASES = new String[] {"PRE_".concat(phase), phase, "POST_".concat(phase)};
        for (String subphase : PHASES) {
            ContainerTask containerTask = null;
            if (!parallel) {
                containerTask = new PipelineContainerTask(new Pipeline(subphase));
            } else {
                containerTask = new ParallelContainerTask(new Parallel(subphase));
            }

            if (groups != null) {
                for (Group group : groups) {
                    UnitOfWork unitOfWork = new UnitOfWork(new WrapDeploymentContextJob(new ProcessorJob(subphase, containerTask, group)), "PREPARE_".concat(subphase));
                    prepareTask.addTask(unitOfWork);
                    group.addTask(unitOfWork);
                }
            } else {
                UnitOfWork unitOfWork = new UnitOfWork(new WrapDeploymentContextJob(new ProcessorJob(subphase, containerTask)), "PREPARE_".concat(subphase));
                prepareTask.addTask(unitOfWork);
            }
            this.pipeline.add(containerTask.getInnerTask());
        }
    }



    public Pipeline getPipeline() {
        return pipeline;
    }

}
