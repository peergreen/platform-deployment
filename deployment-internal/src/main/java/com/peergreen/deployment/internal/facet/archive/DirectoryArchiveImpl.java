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
package com.peergreen.deployment.internal.facet.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.facet.FacetBuilderReference;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.internal.facet.archive.builder.DirectoryArchiveFacetBuilder;

@FacetBuilderReference(DirectoryArchiveFacetBuilder.class)
public class DirectoryArchiveImpl implements Archive {

    /**
     * Manifest Path.
     */
    private static final String MANIFEST_PATH = "META-INF" + File.separator + "MANIFEST.MF";

    /**
     * Logger.
     */
    private static final Log logger = LogFactory.getLog(DirectoryArchiveImpl.class);

    /**
     * Internal resource used as archive.
     */
    private final File directory;

    /**
     * URI of this directory.
     */
    private final URI uri;

    /**
     * Metadata analyzed ?
     */
    private boolean metadataAnalyzed = false;

    private Map<String, String> manifestEntries;

    /**
     * Creates new instance of an IArchive for a directory.
     *
     * @param directory
     *            the given directory.
     */
    public DirectoryArchiveImpl(final File directory) {
        super();
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        this.uri = directory.toURI();
        this.directory = directory;
    }

    /**
     * @return a description of this archive. This name could be used in logger
     *         info.
     */
    @Override
    public String getName() {
        return directory.getPath();
    }

    @Override
    public Map<String, String> getManifestEntries() {
        if (!metadataAnalyzed) {
            try {
                readManifest();
            } catch (ArchiveException e) {
                logger.error("Cannot analyze metadata for archive " + getName(), e);
            }
            metadataAnalyzed = true;
        }
        return manifestEntries;

    }

    /**
     * Init metadata by reading the Manifest file.
     *
     * @throws ArchiveException
     *             if metadata are not initialized
     */

    protected void readManifest() throws ArchiveException {
        File manifestFile = new File(directory, MANIFEST_PATH);

        // Manifest exists, load it
        if (manifestFile.exists()) {
            Manifest manifest = null;
            InputStream is = null;
            try {
                try {
                    is = new FileInputStream(manifestFile);
                } catch (FileNotFoundException e) {
                    throw new ArchiveException("Cannot read the manifest file '" + manifestFile + "'", e);
                }
                try {
                    manifest = new Manifest(is);
                } catch (IOException e) {
                    throw new ArchiveException("Cannot read the manifest file '" + manifestFile + "'", e);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new ArchiveException("Cannot close the manifest file '" + manifestFile + "'", e);
                    }
                }
            }

            // Fill metadata
            this.manifestEntries = ManifestParser.readManifest(manifest);
        }


      }

    /**
     * Encode resource name to be used on Unix/Windows systems.
     *
     * @param resourceName
     *            the name to encode.
     * @return the encoded name.
     */
    private String encode(final String resourceName) {
        String[] tokens = resourceName.split("/");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (sb.length() > 0) {
                sb.append(File.separator);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * @param resourceName
     *            The resource name to be looked up.
     * @return Returns the resource URL if the resource has been found. null
     *         otherwise.
     * @throws ArchiveException
     *             if method fails.
     */
    @Override
    public URI getResource(final String resourceName) throws ArchiveException {
        // lookup the directory on filesystem
        File f = new File(directory, encode(resourceName));
        if (f.exists()) {
            return f.toURI();
        }
        return null;
    }

    /**
     * @return Returns an Iterator of Resource's URI.
     * @throws ArchiveException
     *             if method fails.
     */
    @Override
    public Iterator<URI> getResources() throws ArchiveException {
        Map<String, URI> listResources = new HashMap<String, URI>();
        // Get all files and subdirectories
        addFiles(directory, listResources);
        return listResources.values().iterator();
    }

    /**
     * Methods that loop on directories to find the children (files).
     *
     * @param file
     *            the given directory/file.
     * @param listResources
     *            the list on which to add new files.
     */
    private void addFiles(final File file, final Map<String, URI> listResources) {
        if (!file.exists() || !file.isDirectory()) {
            return;
        }

        // directory
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                // loop
                addFiles("", f, listResources);
            }
        }
    }

    /**
     * Methods that loop on directories to find the children (files).
     *
     * @param name
     *            the resource name
     * @param file
     *            the given directory/file.
     * @param listResources
     *            the list on which to add new files.
     */
    private void addFiles(final String name, final File file, final Map<String, URI> listResources) {
        if (file.isDirectory()) {
            // directory
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    // loop
                    addFiles(name + file.getName() + "/", f, listResources);
                }
            }
        } else {
            // single file
            listResources.put(name + file.getName(), file.toURI());
        }
    }

    /**
     * @param resourceName
     *            The resource name to be looked up.
     * @return Returns an Iterator of matching resources.
     * @throws ArchiveException
     *             if method fails.
     */
    @Override
    public Iterator<URI> getResources(final String resourceName) throws ArchiveException {
        List<URI> listResources = new ArrayList<URI>();
        File f = new File(directory, encode(resourceName));
        if (f.exists()) {
            listResources.add(f.toURI());
        }
        return listResources.iterator();

    }

    /**
     * @return Returns the resource URI.
     * @throws ArchiveException
     *             if method fails.
     */
    @Override
    public URI getURI() throws ArchiveException {
        return this.uri;
    }

    /**
     * Is that the given object is equals to our instance.
     *
     * @param o
     *            the object to compare.
     * @return true if equals, else false.
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DirectoryArchiveImpl)) {
            return false;
        }
        DirectoryArchiveImpl other = (DirectoryArchiveImpl) o;
        return this.directory.equals(other.directory);
    }

    /**
     * Gets hashcode for this object.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return directory.hashCode();
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return all resources name
     */
    @Override
    public Iterator<String> getEntries() {
        Map<String, URI> listResources = new HashMap<String, URI>();
        // Get all files and subdirectories
        addFiles(directory, listResources);
        return listResources.keySet().iterator();
    }
}
