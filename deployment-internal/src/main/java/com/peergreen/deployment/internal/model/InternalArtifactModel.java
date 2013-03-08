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
package com.peergreen.deployment.internal.model;

import java.util.Collection;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.model.ArtifactModel;
import com.peergreen.deployment.model.WireScope;

/**
 * Internal model used by the deployment system.
 * It allows to modify the model while the super interface is only a read-only interface
 * @author Florent Benoit
 */
public interface InternalArtifactModel extends InternalAttributes, ArtifactModel {

    /**
     * @return the facet artifact of this model.
     */
    IFacetArtifact getFacetArtifact();

    /**
     * Gets the wires using the given scope.
     * @param scope the scope to use
     * @param attributeNames the list of attributes that the wire needs to have
     * @return a list of matching wire
     */
    Collection<? extends InternalWire> getInternalWires(WireScope scope, String... attributeNames);

    /**
     * Adds a wire for this internal model.
     * @param wire the wire to add.
     */
    void addWire(InternalWire wire);

}
