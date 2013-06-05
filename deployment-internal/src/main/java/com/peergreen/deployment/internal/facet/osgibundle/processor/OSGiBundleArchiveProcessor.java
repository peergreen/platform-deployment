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
package com.peergreen.deployment.internal.facet.osgibundle.processor;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.facet.archive.OSGiBundle;
import com.peergreen.deployment.internal.facet.osgibundle.OSGiBundleImpl;
import com.peergreen.deployment.processor.Attribute;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Manifest;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.Processor;

/**
 * XML Content processor : Parse XML files to detect the namespace and provides a facet
 * @author Florent Benoit
 */
@Processor
@Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
@Uri(extension = "jar")
@Manifest(
        @Attribute(name = "Bundle-SymbolicName")
)
public class OSGiBundleArchiveProcessor {

    public void handle(Archive archive, ProcessorContext processorContext) throws ProcessorException {
        // Add facet
        try {
            processorContext.addFacet(OSGiBundle.class, new OSGiBundleImpl(archive));
        } catch (ArchiveException e) {
            throw new ProcessorException("Unable to add facet", e);
        }

    }

}
