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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.facet.Facet;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.internal.facet.archive.builder.UriArchiveFacetBuilder;

@Facet(UriArchiveFacetBuilder.ID)
public class URIArchiveImpl implements Archive {

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(URIArchiveImpl.class);

    /**
     * Entries in jar.
     */
    private final Map<String, URI> entries;

    /**
     * Jar url.
     */
    private final URI uri;

    /**
     * Metadata analyzed ?
     */
    private boolean metadataAnalyzed = false;

    private Map<String, String> manifestEntries;

    private Manifest manifest;


    /**
     * Constructor.
     *
     * @param url
     *            the url targeting a jar
     * @throws IOException
     *             if jar could not be read
     * @throws URISyntaxException
     */
    public URIArchiveImpl(final URI uri) throws ArchiveException {
        this.uri = uri;
        entries = new HashMap<String, URI>();

        URLConnection urlConnection;
        try {
            urlConnection = uri.toURL().openConnection();
        } catch (IOException e) {
            throw new ArchiveException("Unable to open connection on URI '" + uri + "'.", e);
        }

        urlConnection.setDefaultUseCaches(false);
        InputStream is = null;
        JarInputStream jarInputStream = null;
        try {
            is = urlConnection.getInputStream();


            jarInputStream = new JarInputStream(is);
            this.manifest = jarInputStream.getManifest();

            ZipEntry zipEntry = jarInputStream.getNextEntry();
            while (zipEntry != null) {
                String name = zipEntry.getName();
                entries.put(name, new URI("jar:" + uri + "!/" + name));
                zipEntry = jarInputStream.getNextEntry();
            }
        } catch (IOException | URISyntaxException e) {
            throw new ArchiveException("Unable to open connection on URI '" + uri + "'.", e);
        } finally {

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOGGER.error("Unable to close streams", e);
                    }

                }
                if (jarInputStream != null) {
                    try {
                        jarInputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Unable to close streams", e);
                    }
                }

        }
    }

    /**
     * Close the archive.
     *
     * @return true
     */
    public boolean close() {
        return true;
    }

    /**
     * Get archive name.
     *
     * @return the url path
     */
    @Override
    public String getName() {
        return uri.getPath();
    }

    /**
     * @param resourceName
     *            the resource name
     * @return a resource with resourceName
     */
    @Override
    public URI getResource(final String resourceName) {
        return entries.get(resourceName);
    }

    /**
     * @return all resources in jar
     */
    @Override
    public Iterator<URI> getResources() {
        return entries.values().iterator();
    }

    /**
     * @param resourceName
     *            the resource name
     * @return all resources with resourceName
     */
    @Override
    public Iterator<URI> getResources(final String resourceName) {
        List<URI> uriList = new LinkedList<URI>();
        URI uri = entries.get(resourceName);
        if (uri != null) {
            uriList.add(uri);
        }
        return uriList.iterator();
    }

    /**
     * @return the uri of archive
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * @return all resources name
     */
    @Override
    public Iterator<String> getEntries() {
        return entries.keySet().iterator();
    }

    @Override
    public Map<String, String> getManifestEntries() {
        if (!metadataAnalyzed) {
            this.manifestEntries = ManifestParser.readManifest(manifest);
            // not need anymore
            this.manifest = null;
            metadataAnalyzed = true;
        }
        return manifestEntries;

    }

}
