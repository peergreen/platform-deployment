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
package com.peergreen.deployment.facet.archive;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;

public interface Archive {
    /**
     * @return a description of this archive. This name could be used in logger
     *         info.
     */
    String getName();

    /**
     * @param resourceName The resource name to be looked up.
     * @return Returns the resource URL if the resource has been found. null
     *         otherwise.
     * @throws ArchiveException if method fails.
     */
    URI getResource(String resourceName) throws ArchiveException;

    /**
     * @return Returns an Iterator of Resource's URL.
     * @throws ArchiveException if method fails.
     */
     Iterator<URI> getResources() throws ArchiveException;

    /**
     * @param resourceName The resource name to be looked up.
     * @return Returns an Iterator of matching resources.
     * @throws ArchiveException if method fails.
     */
    Iterator<URI> getResources(String resourceName) throws ArchiveException;

    /**
     * @return Returns the resource URI.
     * @throws ArchiveException if method fails.
     */
     URI getURI() throws ArchiveException;

    /**
     * @return all resources name
     * @throws ArchiveException if error occurs
     */
    Iterator<String> getEntries() throws ArchiveException;

    Map<String, String> getManifestEntries();
}
