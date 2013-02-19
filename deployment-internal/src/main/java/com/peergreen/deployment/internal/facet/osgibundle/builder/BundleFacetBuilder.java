/*
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

package com.peergreen.deployment.internal.facet.osgibundle.builder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.peergreen.deployment.facet.archive.OSGiBundle;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilder;
import com.peergreen.deployment.facet.builder.FacetBuilderException;

/**
 * Defines builder for Bundle facet
 * @author Guillaume Sauthier
 */
public class BundleFacetBuilder implements FacetBuilder<Bundle> {

    private final BundleContext bundleContext;

    public BundleFacetBuilder(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void build(BuilderContext<Bundle> context) throws FacetBuilderException {

        // Lookup the expected OSGiBundle facet (should have been build before)
        OSGiBundle facet = context.getArtifact().as(OSGiBundle.class);
        if (facet == null) {
            throw new FacetBuilderException(String.format(
                    "Missing OSGiBundle facet in artifact %s",
                    context.getArtifact().name()
            ));
        }

        // Rebuild the Bundle facet
        context.addFacet(findBundle(facet),
                         null);
    }

    private Bundle findBundle(OSGiBundle facet) throws FacetBuilderException {
        Bundle found = bundleContext.getBundle(facet.location());
        if (found == null) {
            // try with reference:
            found = bundleContext.getBundle("reference:".concat(facet.location()));
        }
        if (found == null) {
            throw new FacetBuilderException(String.format(
                    "Bundle %s (%s) is missing",
                    facet.symbolicName(),
                    facet.location()
            ));
        }
        return found;
    }

}
