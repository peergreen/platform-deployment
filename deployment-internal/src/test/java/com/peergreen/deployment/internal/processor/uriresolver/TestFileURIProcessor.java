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
package com.peergreen.deployment.internal.processor.uriresolver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.facet.archive.DirectoryArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.JarArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;
import com.peergreen.deployment.internal.facet.content.FileContentImpl;
import com.peergreen.deployment.internal.facet.content.adapter.ContentFacetAdapter;
import com.peergreen.deployment.internal.processor.uriresolver.FileURIProcessor;

public class TestFileURIProcessor {

    @Mock
    private Artifact artifact;

    @Mock
    private ProcessorContext processorContext;

    @Mock
    private File file;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDirectory() throws URISyntaxException, IOException {
        URI directoryURI = new URI("file:///my-directory");


        // build processor
        FileURIProcessor fileURIProcessor = spy(new FileURIProcessor());
        doReturn(directoryURI).when(artifact).uri();
        doReturn(file).when(fileURIProcessor).getFile(artifact);
        doReturn(true).when(file).isDirectory();


        // call the handle
        fileURIProcessor.handle(artifact, processorContext);

        // verify
        verify(processorContext).addFacet(eq(Archive.class), any(DirectoryArchiveImpl.class), any(FacetCapabilityAdapter.class));

    }



    @Test
    public void testContent() throws URISyntaxException, IOException {

        URI fileUri = new URI("file:///my-file");

        // build processor
        FileURIProcessor fileURIProcessor = new FileURIProcessor();

        // init variables
        when(artifact.uri()).thenReturn(fileUri);

        // call the handle
        fileURIProcessor.handle(artifact, processorContext);

        // verify
        verify(processorContext).addFacet(eq(Content.class), any(FileContentImpl.class), any(ContentFacetAdapter.class));
    }



    @Test
    public void testArchive() throws URISyntaxException, IOException {

        URI archiveUri = new URI("florent:///my-file");

        // build processor
        FileURIProcessor archiveUriURIProcessor = spy(new FileURIProcessor());
        doReturn(archiveUri).when(artifact).uri();
        doReturn(file).when(archiveUriURIProcessor).getFile(artifact);
        doReturn(true).when(archiveUriURIProcessor).checkZip(file);
        doReturn(false).when(file).isDirectory();


        // call the handle
        archiveUriURIProcessor.handle(artifact, processorContext);

        // verify
        verify(processorContext).addFacet(eq(Archive.class), any(JarArchiveImpl.class), any(ArchiveFacetAdapter.class));

    }



}
