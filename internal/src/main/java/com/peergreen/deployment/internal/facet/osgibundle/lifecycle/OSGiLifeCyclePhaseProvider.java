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
package com.peergreen.deployment.internal.facet.osgibundle.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetLifeCyclePhaseProvider;
import com.peergreen.deployment.facet.archive.OSGiBundle;

public class OSGiLifeCyclePhaseProvider implements FacetLifeCyclePhaseProvider<OSGiBundle> {

    private final List<String> deployPhases;
    private final List<String> undeployPhases;

    public OSGiLifeCyclePhaseProvider() {
        this.deployPhases = new ArrayList<String>();
        deployPhases.add("INSTALL");
        deployPhases.add("START");


        this.undeployPhases = new ArrayList<String>();
        undeployPhases.add("STOP");
        undeployPhases.add("UNINSTALL");

    }

    @Override
    public List<String> getLifeCyclePhases(DeploymentMode deploymentMode) {
        switch (deploymentMode) {
            case DEPLOY:
            case UPDATE:
                return deployPhases;
            case UNDEPLOY:
                return undeployPhases;
                default : throw new IllegalStateException("Deployment mode '" + deploymentMode + "' not supported");
        }
    }



}
