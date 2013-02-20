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

import static com.peergreen.deployment.model.ArtifactModelConstants.PERSISTENT;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * Implementation of the {@link InternalArtifactModelChangesView}
 * @author Florent Benoit
 */
public class DefaultArtifactModelPersistenceView extends AbsDefaultArtifactModelView implements InternalArtifactModelPersistenceView {

    /**
     * Default constructor.
     * @param artifactModel
     */
    public DefaultArtifactModelPersistenceView(DefaultArtifactModel artifactModel) {
        super(artifactModel);
    }

    @Override
    public boolean isPersistent() {
        return hasAttributes(PERSISTENT);
    }

    @Override
    public void setPersistent(boolean persistent) {
        setAttribute(PERSISTENT, persistent);
    }


}
