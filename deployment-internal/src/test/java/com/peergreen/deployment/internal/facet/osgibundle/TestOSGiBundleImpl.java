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
package com.peergreen.deployment.internal.facet.osgibundle;

import static org.mockito.Mockito.doReturn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;

/**
 *
 * @author Florent Benoit
 */
public class TestOSGiBundleImpl {

    @Mock
    private Archive archive;

    @Mock
    private Map<String, String> manifestEntries;


    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);

    }


    @Test(expectedExceptions=ArchiveException.class)
    public void testBadURLOsgiBundle() throws ArchiveException, URISyntaxException {
        doReturn(manifestEntries).when(archive).getManifestEntries();

        String symbolicName = "toto";
        // invalid URL
        String uriString = "test://myURI";

        doReturn(symbolicName).when(manifestEntries).get(Constants.BUNDLE_SYMBOLICNAME);

        URI uri = new URI(uriString);
        doReturn(uri).when(archive).getURI();

        new OSGiBundleImpl(archive);
        fail("Should throw exception");
    }

    @Test
    public void testOsgiBundle() throws ArchiveException, URISyntaxException {
        doReturn(manifestEntries).when(archive).getManifestEntries();

        String symbolicName = "toto";
        // invalid URL
        String uriString = "http://myURI";

        doReturn(symbolicName).when(manifestEntries).get(Constants.BUNDLE_SYMBOLICNAME);

        URI uri = new URI(uriString);
        doReturn(uri).when(archive).getURI();

        OSGiBundleImpl osgiBundleImpl = new OSGiBundleImpl(archive);


        assertNotNull(osgiBundleImpl);
        assertEquals(osgiBundleImpl.symbolicName(), symbolicName);
        assertEquals(osgiBundleImpl.location(), uriString);
    }



}
