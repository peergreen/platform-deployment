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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.peergreen.deployment.internal.context.BasicDeploymentContext;
import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.group.GroupTaskContextFactory;
import com.peergreen.tasks.model.group.SubstituteExecutionContextProvider;

public class TaskExecutionHolder {

    private Collection<Group> groups;

    private SubstituteExecutionContextProvider substituteExecutionContextProvider;

    private TaskContextFactory groupTaskContextFactory;


    private boolean onlyDiscoveryPhases = false;

    private List<BasicDeploymentContext> deploymentContexts;


    public TaskExecutionHolder() {
        this.groups = new HashSet<Group>();
        this.substituteExecutionContextProvider = new SubstituteExecutionContextProvider();
        this.groupTaskContextFactory = new GroupTaskContextFactory(groups, substituteExecutionContextProvider);
        this.deploymentContexts = new CopyOnWriteArrayList<BasicDeploymentContext>();

    }

    public boolean isOnlyDiscoveryPhases() {
        return onlyDiscoveryPhases;
    }

    public void setTaskContextFactory(TaskContextFactory groupTaskContextFactory) {
        this.groupTaskContextFactory = groupTaskContextFactory;
    }

    public void setGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    public void setSubstituteExecutionContextProvider(SubstituteExecutionContextProvider substituteExecutionContextProvider) {
        this.substituteExecutionContextProvider = substituteExecutionContextProvider;
    }


    public void setOnlyDiscoveryPhases(boolean onlyDiscoveryPhases) {
        this.onlyDiscoveryPhases = onlyDiscoveryPhases;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    public SubstituteExecutionContextProvider getSubstituteExecutionContextProvider() {
        return substituteExecutionContextProvider;
    }

   public TaskContextFactory getTaskContextFactory() {
       return groupTaskContextFactory;
   }

   public List<BasicDeploymentContext> getDeploymentContexts() {
       return deploymentContexts;
   }

   public void setDeploymentContexts(List<BasicDeploymentContext> deploymentContexts) {
       this.deploymentContexts = deploymentContexts;
   }

}
