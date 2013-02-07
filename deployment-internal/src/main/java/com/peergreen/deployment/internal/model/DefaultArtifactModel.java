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

import java.util.HashSet;
import java.util.Set;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.model.Wire;

public class DefaultArtifactModel implements InternalArtifactModel {

    private final IFacetArtifact facetArtifact;
    private boolean deploymentRoot = false;
    private boolean persistent = false;

    private final Set<InternalWire> wires;
    private final Set<InternalWire> fromWires;
    private final Set<InternalWire> toWires;

    private boolean undeployed = false;

    private long lastModified = Long.MIN_VALUE;
    private long artifactLength = Long.MIN_VALUE;
    private long checkingArtifactLength = Long.MIN_VALUE;


    public DefaultArtifactModel(IFacetArtifact facetArtifact) {
        this.facetArtifact = facetArtifact;
        this.wires = new HashSet<InternalWire>();
        this.fromWires = new HashSet<InternalWire>();
        this.toWires = new HashSet<InternalWire>();
    }

    @Override
    public void setUndeployed(boolean undeployed) {
        this.undeployed = undeployed;
    }

    @Override
    public boolean isUndeployed() {
        return undeployed;
    }

    @Override
    public void addWire(InternalWire wire) {
        wires.add(wire);
        if (this.equals(wire.getFrom())) {
            fromWires.add(wire);
        }
        if (this.equals(wire.getTo())) {
            toWires.add(wire);
        }
        //FIXME: check that it's strange to add a wire with being neither from and to
    }


    @Override
    public IFacetArtifact getFacetArtifact() {
        return facetArtifact;
    }

    @Override
    public Iterable<? extends Wire> getWires(Class<?>... flags) {
        return getInternalWires(flags);
    }

    @Override
    public Iterable<? extends Wire> getFromWires(Class<?>... flags) {
        return getInternalFromWires(flags);
    }

    @Override
    public Iterable<? extends Wire> getToWires(Class<?>... flags) {
        return getInternalToWires(flags);
    }

    @Override
    public boolean isUsed() {
        return fromWires.isEmpty();
    }

    @Override
    public boolean isLeaf() {
        return wires.isEmpty();
    }

    @Override
    public boolean isDeploymentRoot() {
        return deploymentRoot;
    }

    @Override
    public Iterable<InternalWire> getInternalWires(Class<?>... flags) {
        return getFlags(wires, flags);
    }

    @Override
    public Iterable<InternalWire> getInternalFromWires(Class<?>... flags) {
        return getFlags(fromWires, flags);
    }

    @Override
    public Iterable<InternalWire> getInternalToWires(Class<?>... flags) {
        return getFlags(toWires, flags);
    }


    public Iterable<InternalWire> getFlags(Set<InternalWire> wires, Class<?>... flags) {
        if (flags == null) {
            return wires;
        }

        //search flags
        Set<InternalWire> flagsWire = new HashSet<InternalWire>();
        for (InternalWire wire : wires) {
            if (wire.isFlagged(flags)) {
                flagsWire.add(wire);
            }
        }

        return flagsWire;

    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public long getArtifactLength() {
        return artifactLength;
    }

    @Override
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public void setArtifactLength(long artifactLength) {
        this.artifactLength = artifactLength;
    }

    @Override
    public void setCheckingArtifactLength(long checkingArtifactLength) {
        this.checkingArtifactLength = checkingArtifactLength;
    }

    @Override
    public long getCheckingArtifactLength() {
        return checkingArtifactLength;
    }

    public void setDeploymentRoot(boolean rootDeployment) {
        deploymentRoot = rootDeployment;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
