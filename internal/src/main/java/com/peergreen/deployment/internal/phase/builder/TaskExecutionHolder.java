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

import com.peergreen.tasks.execution.TaskContextFactory;
import com.peergreen.tasks.model.group.Group;
import com.peergreen.tasks.model.group.GroupTaskContextFactory;
import com.peergreen.tasks.model.group.SubstituteExecutionContextProvider;

public class TaskExecutionHolder {

    private final Collection<Group> groups;

    private final SubstituteExecutionContextProvider substituteExecutionContextProvider;

    private final GroupTaskContextFactory groupTaskContextFactory;

    public TaskExecutionHolder() {
        this.groups = new HashSet<Group>();
        this.substituteExecutionContextProvider = new SubstituteExecutionContextProvider();
        this.groupTaskContextFactory = new GroupTaskContextFactory(groups, substituteExecutionContextProvider);

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

}
