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


/**
 * Defines an implementation of a wire.
 * @author Florent Benoit
 */
public class DefaultWire extends AbsInternalAttributes implements InternalWire {

    private final InternalArtifactModel from;

    private final InternalArtifactModel to;


    public DefaultWire(InternalArtifactModel from, InternalArtifactModel to) {
        super();
        this.from = from;
        this.to = to;
    }

    @Override
    public InternalArtifactModel getFrom() {
        return getInternalFrom();
    }

    @Override
    public InternalArtifactModel getTo() {
        return getInternalTo();
    }

    @Override
    public InternalArtifactModel getInternalFrom() {
        return from;
    }

    @Override
    public InternalArtifactModel getInternalTo() {
        return to;
    }


}
