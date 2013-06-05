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

import java.io.File;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.facet.archive.DirectoryArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.JarArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;
import com.peergreen.deployment.internal.facet.content.FileContentImpl;
import com.peergreen.deployment.internal.facet.content.adapter.ContentFacetAdapter;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.Processor;

/**
 * file:// URI processor
 * @author Florent Benoit
 */
@Processor
@Uri("file")
@Discovery(DiscoveryPhasesLifecycle.URI_RESOLVER)
public class FileURIProcessor {

    private final FacetCapabilityAdapter<Archive> archiveFacetAdapter;
    private final FacetCapabilityAdapter<Content> contentFacetAdapter;

    public FileURIProcessor() {
        this.archiveFacetAdapter = new ArchiveFacetAdapter();
        this.contentFacetAdapter = new ContentFacetAdapter();
    }

    public void handle(Artifact artifact, ProcessorContext processorContext) {

        // It's a file URI from requirements, so get the file
        File file = getFile(artifact);

        // It's an archive as it's a directory
        if (file.isDirectory()) {
            processorContext.addFacet(Archive.class, new DirectoryArchiveImpl(file), archiveFacetAdapter);
            return;
        }

        // It's a file, check if it's a zip or a content
        if (checkZip(file)) {
            // It's a zip file so transform it into an archive
            processorContext.addFacet(Archive.class, new JarArchiveImpl(file), archiveFacetAdapter);
            return;
        }

        // it's a content
        processorContext.addFacet(Content.class,  new FileContentImpl(file), contentFacetAdapter);
    }

    protected File getFile(Artifact artifact) {
        return new File(artifact.uri().getPath());
    }


    protected boolean checkZip(File file) {
        try {
            return CheckZip.checkFile(file);
        } catch (CheckZipException e) {
            return false;
        }
    }

}
