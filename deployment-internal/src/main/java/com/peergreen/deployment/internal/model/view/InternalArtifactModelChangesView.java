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

import com.peergreen.deployment.model.view.ArtifactModelChangesView;

/**
 * Defines setters for deployment view
 * @author Florent Benoit
 */
public interface InternalArtifactModelChangesView extends ArtifactModelChangesView {

    void setLastModified(long lastModified);
    void setArtifactLength(long length);
    void setCheckingArtifactLength(long length);
    long getCheckingArtifactLength();

}
