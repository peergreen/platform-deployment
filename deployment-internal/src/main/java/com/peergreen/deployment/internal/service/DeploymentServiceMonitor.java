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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.internal.model.ArtifactModelManager;

/**
 * This tracker will check for update/deleted artifacts
 * If an artifact is updated, it will send the UPDATE order
 * if an artifact is deleted, it will send the UNDEPLOY order
 * @author Florent Benoit
 */
public class DeploymentServiceMonitor extends Thread {

    private final DeploymentService deploymentService;

    private final ArtifactModelManager artifactModelManager;

    private final ArtifactBuilder artifactBuilder;

    private boolean stopThread = false;

    public DeploymentServiceMonitor(DeploymentService deploymentService, ArtifactBuilder artifactBuilder, ArtifactModelManager artifactModelManager) {
        this.deploymentService = deploymentService;
        this.artifactBuilder = artifactBuilder;
        this.artifactModelManager = artifactModelManager;
    }


    public void stopTracking() {
        this.stopThread = true;
    }

    /**
     * Start the thread of this class. <br>
     * It will search and deploy files to deploy.<br>
     * In development mode, it will check the changes.
     */
    @Override
    public void run() {

        for (;;) {
            if (stopThread) {
                // Stop the thread
                return;
            }

            // Compute last modified
            Collection<URI> trackedURIs = artifactModelManager.getDeployedRootURIs();

            List<Artifact> toUndeploy = new ArrayList<Artifact>();


            //TODO: allow to plug protocol tracker
            // Track file:// URI
            for (URI uri : trackedURIs) {
                if ("file".equals(uri.getScheme())) {
                    File f = new File(uri.getPath());
                    if (!f.exists()) {
                        Artifact artifact = artifactBuilder.build(uri.toString(), uri);
                        toUndeploy.add(artifact);
                    }
                }
            }

            if (!toUndeploy.isEmpty()) {
                deploymentService.process(toUndeploy, DeploymentMode.UNDEPLOY);
            }


        }
    }

}
