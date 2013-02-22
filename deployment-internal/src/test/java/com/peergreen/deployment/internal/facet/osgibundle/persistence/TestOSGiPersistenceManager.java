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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;

/**
 * Test of the persistence manager
 * @author Florent Benoit
 */
public class TestOSGiPersistenceManager {


    private Artifact artifact;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    private Dictionary<Object, Object> dictionary;


    private OSGiPersitenceArtifactManager manager;


    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.dictionary = new Properties();
        this.manager = new OSGiPersitenceArtifactManager(bundleContext);
        String name = "file:artifact";
        URI uri = new URI(name);

        doReturn(bundle).when(bundleContext).getBundle(uri.toURL().toString());
        doReturn(dictionary).when(bundle).getHeaders();

        this.artifact = new ImmutableArtifact(name, uri);
    }


    @Test
    public void testForgetOsgiBundle() throws MalformedURLException, BundleException {
        List<Artifact> artifacts = new ArrayList<>();

        artifacts.add(artifact);
        manager.forget(artifacts);

        verify(bundle).uninstall();
        verify(bundle).stop();
    }


    @Test
    public void testForgetFragmentOsgiBundle() throws MalformedURLException, BundleException {
        List<Artifact> artifacts = new ArrayList<>();

        dictionary.put(Constants.FRAGMENT_HOST, "fragment-test-bundle");

        artifacts.add(artifact);
        manager.forget(artifacts);

        // Fragment shouldn't be stopped
        verify(bundle, times(0)).stop();

        // but uninstall
        verify(bundle).uninstall();

    }



}
