/**
 * Copyright 2012-2013 Peergreen S.A.S.
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
 * Model of the artifacts managed by the deployment system.
 * The model is based on ArtifactModel and wires between these artifact models
 * @author Florent Benoit
 */
public interface ArtifactModel extends Attributes {

    /**
     * Gets the wires using the given scope.
     * @param scope the scope to use
     * @param attributeNames the list of attributes that the wire needs to have
     * @return a list of matching wire
     */
    Iterable<? extends Wire> getWires(WireScope scope, String... attributeNames);

    /**
     * Gets the view for this model.
     * @param clazz the given expected type of the view
     * @return instance of the view or null if not found
     */
    <T> T as(Class<T> clazz);

}
