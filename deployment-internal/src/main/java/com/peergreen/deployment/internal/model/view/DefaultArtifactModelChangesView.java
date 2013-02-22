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

import static com.peergreen.deployment.model.ArtifactModelConstants.ARTIFACT_LENGTH;
import static com.peergreen.deployment.model.ArtifactModelConstants.CHECKING_ARTIFACT_LENGTH;
import static com.peergreen.deployment.model.ArtifactModelConstants.LAST_MODIFIED;

import com.peergreen.deployment.internal.model.InternalArtifactModel;

/**
 *
 *
 * @author Florent Benoit
 */
public class DefaultArtifactModelChangesView extends AbsDefaultArtifactModelView implements InternalArtifactModelChangesView {


    /**
     * Default constructor.
     * @param artifactModel
     */
    public DefaultArtifactModelChangesView(InternalArtifactModel artifactModel) {
        super(artifactModel);
    }


    @Override
    public long getLastModified() {
        return getLongAttribute(LAST_MODIFIED);
    }

    @Override
    public long getArtifactLength() {
        return getLongAttribute(ARTIFACT_LENGTH);
    }

    @Override
    public long getCheckingArtifactLength() {
        return getLongAttribute(CHECKING_ARTIFACT_LENGTH);
    }


    @Override
    public void setLastModified(long lastModified) {
        setAttribute(LAST_MODIFIED, lastModified);
    }

    @Override
    public void setArtifactLength(long artifactLength) {
        setAttribute(ARTIFACT_LENGTH, artifactLength);
    }

    @Override
    public void setCheckingArtifactLength(long checkingArtifactLength) {
        setAttribute(CHECKING_ARTIFACT_LENGTH, checkingArtifactLength);
    }



}
