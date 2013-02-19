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
package com.peergreen.deployment.internal.tests.facet.archive;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.resource.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;
import com.peergreen.deployment.resource.artifact.archive.ArchiveCapability;
import com.peergreen.deployment.resource.artifact.archive.ArchiveNamespace;

public class TestFacetAdapter {

    @Mock
    private Resource resource;

    @Mock
    private Archive archive;


    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testArchiveFacetAdapter() {

        Map<String, String> entries = new HashMap<String, String>();
        // Manifest entries
        entries.put("key1", "value1");
        entries.put("key2", "value2");

        // populate archive
        when(archive.getManifestEntries()).thenReturn(entries);

        FacetCapabilityAdapter<Archive> facetAdapter = new ArchiveFacetAdapter();
        ArchiveCapability archiveCapability = facetAdapter.getCapability(resource, archive);

        // Check capability
        Assert.assertEquals(archiveCapability.getNamespace(), ArchiveNamespace.ARCHIVE_NAMESPACE);

        Assert.assertEquals(archiveCapability.getResource(), resource);

        Assert.assertNotNull(archiveCapability.getAttributes());
        Assert.assertEquals(archiveCapability.getAttributes().size(), 3);
        Assert.assertEquals(archiveCapability.getAttributes().get("key1"), "value1");

        Assert.assertEquals(archiveCapability.getAttributes().get(ArchiveNamespace.ARCHIVE_NAMESPACE), "");

    }
}
