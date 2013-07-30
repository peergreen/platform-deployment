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

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetCapabilityAdapter;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.facet.archive.URIArchiveImpl;
import com.peergreen.deployment.internal.facet.archive.adapter.ArchiveFacetAdapter;
import com.peergreen.deployment.internal.facet.content.URIContentImpl;
import com.peergreen.deployment.internal.facet.content.adapter.ContentFacetAdapter;

/**
 * Abstract URI processor (for input streams)
 * @author Florent Benoit
 */
public class AbstractURIProcessor {

    private final FacetCapabilityAdapter<Archive> archiveFacetAdapter;
    private final FacetCapabilityAdapter<Content> contentFacetAdapter;

    public AbstractURIProcessor() {
        this.archiveFacetAdapter = new ArchiveFacetAdapter();
        this.contentFacetAdapter = new ContentFacetAdapter();
    }

    /**
     * Handle the given artifact with the given processor Context
     */
    public void handle(Artifact artifact, ProcessorContext processorContext) throws ProcessorException {

        // Now check if it's a zip or not
        boolean isZip = false;
        try {
            isZip = CheckZip.checkURI(artifact.uri());
        } catch (CheckZipException e) {
            throw new ProcessorException("Unable to check if it's a zip", e);
        }

        // it's a zip ?
        if (isZip) {
            try {
                processorContext.addFacet(Archive.class, new URIArchiveImpl(artifact.uri()), archiveFacetAdapter);
            } catch (ArchiveException e) {
                throw new ProcessorException("Unable to build URI archive", e);
            }
        } else {
            // not a zip, but a content
            processorContext.addFacet(Content.class, new URIContentImpl(artifact.uri()), contentFacetAdapter);
        }
    }

}
