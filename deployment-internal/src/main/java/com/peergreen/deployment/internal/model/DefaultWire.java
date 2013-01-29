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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.peergreen.deployment.model.WireType;

public class DefaultWire implements InternalWire {

    private final InternalArtifactModel from;

    private final InternalArtifactModel to;

    private final WireType type;

    private final Set<Class<?>> flags;



    public DefaultWire(InternalArtifactModel from, InternalArtifactModel to, WireType type) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.flags = new HashSet<Class<?>>();
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
    public WireType getType() {
        return type;
    }


    @Override
    public InternalArtifactModel getInternalFrom() {
        return from;
    }


    @Override
    public InternalArtifactModel getInternalTo() {
        return to;
    }

    @Override
    public boolean isFlagged(Class<?>... flagClasses) {
        return flags.containsAll(Arrays.asList(flagClasses));
    }

    @Override
    public boolean removeFlag(Class<?> flagClass) {
        return flags.remove(flagClass);
    }
    @Override
    public void addFlag(Class<?> flagClass) {
        flags.add(flagClass);
    }

}
