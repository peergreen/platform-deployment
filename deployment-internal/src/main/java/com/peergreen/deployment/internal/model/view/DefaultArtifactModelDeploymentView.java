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
package com.peergreen.deployment.internal.model.view;

import static com.peergreen.deployment.model.ArtifactModelConstants.DEPLOYMENT_ROOT;
import static com.peergreen.deployment.model.ArtifactModelConstants.UNDEPLOYED;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * Implementation of the {@link InternalArtifactModelChangesView}
 * @author Florent Benoit
 */
public class DefaultArtifactModelDeploymentView extends AbsDefaultArtifactModelView implements InternalArtifactModelDeploymentView {

    /**
     * Default constructor.
     * @param artifactModel
     */
    public DefaultArtifactModelDeploymentView(DefaultArtifactModel artifactModel) {
        super(artifactModel);
    }


    @Override
    public boolean isDeploymentRoot() {
        return getBooleanAttribute(DEPLOYMENT_ROOT);
    }

    @Override
    public boolean isUndeployed() {
        return getBooleanAttribute(UNDEPLOYED);
    }

    @Override
    public void setUndeployed(boolean undeployed) {
        setAttribute(UNDEPLOYED, undeployed);
    }

    @Override
    public void setDeploymentRoot(boolean value) {
        setAttribute(DEPLOYMENT_ROOT, value);

    }


}
