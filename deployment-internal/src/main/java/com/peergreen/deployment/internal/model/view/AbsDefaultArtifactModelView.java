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

import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * Common methods to make a view of the artifact model.
 * @author Florent Benoit
 */
public class AbsDefaultArtifactModelView {

    private final DefaultArtifactModel artifactModel;

    public AbsDefaultArtifactModelView(DefaultArtifactModel artifactModel) {
        this.artifactModel = artifactModel;
    }

    protected long getLongAttribute(String attributeName) {
        Long l = (Long) getAttribute(attributeName);
        if (l == null) {
            return Long.MIN_VALUE;
        }
        return l.longValue();
    }

    public boolean hasAttributes(String... attributesName) {
        return this.artifactModel.hasAttributes(attributesName);
    }

    public void setAttribute(String key, Object value) {
        this.artifactModel.setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getAttribute(String attributeName) {
        return (T) this.artifactModel.getAttribute(attributeName);
    }

}
