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
package com.peergreen.deployment.internal.facet.osgibundle.persistence;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.PersistenceArtifactManager;

public class OSGiPersitenceArtifactManager implements PersistenceArtifactManager {

    private final BundleContext bundleContext;

    public OSGiPersitenceArtifactManager(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    //FIXME : needs to send a report !!!
    @Override
    public void forget(Collection<Artifact> artifacts) {

        for (Artifact artifact : artifacts) {
            // Matching ?
            Bundle bundle = findBundle(artifact.uri());
            if (bundle != null) {
                // Stop and uninstall as it's not persistent
                try {
                    bundle.stop();
                } catch (BundleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    bundle.uninstall();
                } catch (BundleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    private Bundle findBundle(URI uri)  {
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException e) {
            return null;
        }
        Bundle found = bundleContext.getBundle(url.toString());
        if (found == null) {
            // try with reference:
            found = bundleContext.getBundle("reference:".concat(url.toString()));
        }

        return found;
    }



}
