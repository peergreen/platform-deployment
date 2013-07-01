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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.facet.FacetBuilderReference;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.internal.facet.archive.builder.JarArchiveFacetBuilder;

/**
 * Creates wrapper around jar file.
 * @author Florent Benoit
 */
@FacetBuilderReference(JarArchiveFacetBuilder.class)
public class JarArchiveImpl implements Archive {

    /**
     * Logger.
     */
    private static final Log logger = LogFactory.getLog(JarArchiveImpl.class);

    /**
     * Internal resource used as archive.
     */
    private JarFile jarFile;


    /**
     * Cached URL of this archive.
     */
    private URI uri = null;

    /**
     * Metadata analyzed ?
     */
    private boolean metadataAnalyzed = false;


    private Map<String, String> manifestEntries;

    private final File file;

    /**
     * Creates new instance of an Archive for a jar file.
     * @param file the given jar file.
     * @throws IOException
     */
    public JarArchiveImpl(final File file) {
        this.file = file;
        this.uri = file.toURI();
    }


    protected void init() throws ArchiveException {
        if (this.jarFile != null) {
            return;
        }

        try {
            this.jarFile = new JarFile(file);
        } catch (IOException e) {
            throw new ArchiveException("Unable to build archive around '" + file  + "'.", e);
        }
    }


    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getName()
     */
    @Override
    public String getName() {
        return file.getName();
    }

    /**
     * Close the underlying Resource.
     */
    public boolean close() {

        // Not yet used
        if (jarFile == null) {
            return true;
        }
        try {
            jarFile.close();
        } catch (IOException e) {
            return false;
        }

        jarFile = null;
        return true;
    }

    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getResource(java.lang.String)
     */
    @Override
    public URI getResource(final String resourceName) throws ArchiveException {
        URI resourceUri = null;
        // Open jar file.
        init();
        try {
            JarEntry jarEntry = jarFile.getJarEntry(resourceName);
            if (jarEntry != null) {
                try {
                    resourceUri = new URI("jar:" + uri + "!/" + resourceName);
                } catch (URISyntaxException e) {
                    throw new ArchiveException("Invalid url", e);
                }
            }
        } finally {
            close();
        }
        return resourceUri;
    }

    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getResources()
     */
    @Override
    public Iterator<URI> getResources() throws ArchiveException {
        List<URI> listResources = new ArrayList<URI>();
        init();
        try {
            Enumeration<? extends ZipEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                ZipEntry zipEntry = en.nextElement();
                String name = zipEntry.getName();
                try {
                    listResources.add(new URI("jar:" + uri + "!/" + name));
                } catch (URISyntaxException e) {
                    throw new ArchiveException("Invalid uri", e);
                }
            }
        } finally {
            close();
        }
        return listResources.iterator();
    }

    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getResources(java.lang.String)
     */
    @Override
    public Iterator<URI> getResources(final String resourceName) throws ArchiveException {
        List<URI> listResources = new ArrayList<URI>();
        URI uri = getResource(resourceName);
        if (uri != null) {
            listResources.add(uri);
        }

        return listResources.iterator();
    }

    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getURI()
     */
    @Override
    public URI getURI() throws ArchiveException {
        return this.uri;
    }

    /**
     * Is that the given object is equals to our instance.
     * @param o the object to compare.
     * @return true if equals, else false.
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof JarArchiveImpl)) {
            return false;
        }
        JarArchiveImpl other = (JarArchiveImpl) o;
        return this.file.equals(other.file);
    }

    /**
     * Gets hashcode for this object.
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return file.hashCode();
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Init metadata by reading the Manifest file.
     * @throws ArchiveException if metadata are not initialized
     */

    protected void readManifest() throws ArchiveException {
        // Open Jar File
        init();

        // Get manifest
        Manifest manifest = null;
        try {
            manifest = jarFile.getManifest();
        } catch (IOException e) {
            throw new ArchiveException("Cannot analyze the manifest", e);
        } finally {
            // close jar
            close();
        }

        // Read manifest
        if (manifest != null) {
            this.manifestEntries = ManifestParser.readManifest(manifest);
        } else {
            this.manifestEntries = new HashMap<>();
        }

    }


    @Override
    public Map<String, String> getManifestEntries() {
        if (!metadataAnalyzed) {
            try {
                readManifest();
            } catch (ArchiveException e) {
                logger.error("Cannot analyze metadata for archive " + jarFile, e);
            }
            metadataAnalyzed = true;
        }
        return manifestEntries;

    }




    /* (non-Javadoc)
     * @see com.peergreen.deployment.internal.artifact.archive.IArchive#getEntries()
     */
    @Override
    public Iterator<String> getEntries() throws ArchiveException {
        List<String> listResourcesName = new ArrayList<String>();
        init();
        try {
            Enumeration<? extends ZipEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                ZipEntry zipEntry = en.nextElement();
                String name = zipEntry.getName();
                listResourcesName.add(name);
            }
        } finally {
            close();
        }
        return listResourcesName.iterator();
    }

}
