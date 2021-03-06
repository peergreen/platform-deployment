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
package com.peergreen.deployment.model;

/**
 * Defines a wire between two ArtifactModel nodes.
 * @author Florent Benoit
 */
public interface Wire extends Attributes {

    /**
     * @return the origin of this wire.
     */
    ArtifactModel getFrom();

    /**
     * @return the target of this wire.
     */
    ArtifactModel getTo();

}
