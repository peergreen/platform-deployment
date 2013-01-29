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

package com.peergreen.deployment;

import java.net.URI;


/**
 * Defines the deployment unit.
 * @author Florent Benoit
 */
public interface Artifact {

    /**
     * @return name of this artifact.
     */
    String name();

    /**
     * @return URI of this artifact.
     */
    URI uri();

    /**
     * Gets the internal facet of this artifact.
     * @param clazz the given expected type of the facet
     * @return instance of the facet or null if not found
     */
    <T> T as(Class<T> clazz);

}
