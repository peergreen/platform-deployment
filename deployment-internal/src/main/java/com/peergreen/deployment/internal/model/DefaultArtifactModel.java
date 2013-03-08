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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.view.DefaultArtifactModelChangesView;
import com.peergreen.deployment.internal.model.view.DefaultArtifactModelDeploymentView;
import com.peergreen.deployment.internal.model.view.DefaultArtifactModelPersistenceView;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelChangesView;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelDeploymentView;
import com.peergreen.deployment.internal.model.view.InternalArtifactModelPersistenceView;
import com.peergreen.deployment.model.Wire;
import com.peergreen.deployment.model.WireScope;
import com.peergreen.deployment.model.view.ArtifactModelChangesView;
import com.peergreen.deployment.model.view.ArtifactModelDeploymentView;
import com.peergreen.deployment.model.view.ArtifactModelPersistenceView;

/**
 * Default implementation of the artifaft model.
 * The model is storing all data about a given artifact being managed/tracked by the deployment system.
 * The model will be restored for persistent artifact only.
 * @author Florent Benoit
 */
public class DefaultArtifactModel extends AbsInternalAttributes implements InternalArtifactModel {

    /**
     * Facet artifact used inside the artifact model.
     */
    private final IFacetArtifact facetArtifact;

    /**
     * All the wires using this model.
     */
    private final Set<InternalWire> wires;

    /**
     * Wires starting from this node.
     */
    private final Set<InternalWire> fromWires;

    /**
     * Wires targeting this node.
     */
    private final Set<InternalWire> toWires;

    /**
     * Manages the view of this artifact.
     */
    private final Map<Class<?>, Object> views;

    /**
     * Build default model on a given {@link IFacetArtifact}
     * @param facetArtifact the facet artifact
     */
    public DefaultArtifactModel(IFacetArtifact facetArtifact) {
        super();
        this.facetArtifact = facetArtifact;
        this.wires = new HashSet<>();
        this.fromWires = new HashSet<>();
        this.toWires = new HashSet<>();
        this.views = new HashMap<>();
        addDefaultViews();
    }

    /**
     * Adds the default view.
     */
    protected void addDefaultViews() {
        addView(new DefaultArtifactModelChangesView(this), InternalArtifactModelChangesView.class, ArtifactModelChangesView.class);
        addView(new DefaultArtifactModelDeploymentView(this), InternalArtifactModelDeploymentView.class, ArtifactModelDeploymentView.class);
        addView(new DefaultArtifactModelPersistenceView(this), InternalArtifactModelPersistenceView.class, ArtifactModelPersistenceView.class);
    }

    public void addView(Object o,  Class<?>... classes) {
        for (Class<?> clazz : classes) {
            views.put(clazz, o);
        }
    }


    /**
     * @return a view for this model.
     */
    @Override
    public <View> View as(Class<View> viewClass) {
        return viewClass.cast(views.get(viewClass));
    }


    @Override
    public void addWire(InternalWire wire) {
        // Check that the wire is linked to this node
        if (!this.equals(wire.getTo())  && !this.equals(wire.getFrom())) {
            throw new IllegalStateException("Cannot add a wire that doesn't use the node");
        }

        wires.add(wire);
        if (this.equals(wire.getFrom())) {
            fromWires.add(wire);
        }
        if (this.equals(wire.getTo())) {
            toWires.add(wire);
        }

    }


    @Override
    public IFacetArtifact getFacetArtifact() {
        return facetArtifact;
    }


    /**
     * Gets the wires using the given scope.
     * @param scope the scope to use
     * @param attributeNames the list of attributes that the wire needs to have
     * @return a list of matching wire
     */
    @Override
    public Collection<? extends InternalWire> getInternalWires(WireScope scope, String... attributeNames) {
        Set<InternalWire> scopedWires;
        if (scope == WireScope.ALL) {
            scopedWires = this.wires;
        } else if (scope == WireScope.FROM) {
            scopedWires = this.fromWires;
        } else if (scope == WireScope.TO) {
            scopedWires = this.toWires;
        } else {
            return Collections.emptySet();
        }

        // No search
        if (attributeNames == null || attributeNames.length == 0) {
            return scopedWires;
        }

        //search flags
        Set<InternalWire> flagsWire = new HashSet<InternalWire>();
        for (InternalWire wire : scopedWires) {
            if (wire.hasAttributes(attributeNames)) {
                flagsWire.add(wire);
            }
        }

        return flagsWire;

    }

    @Override
    public Iterable<? extends Wire> getWires(WireScope scope, String... attributeNames) {
        return getInternalWires(scope, attributeNames);
    }


}
