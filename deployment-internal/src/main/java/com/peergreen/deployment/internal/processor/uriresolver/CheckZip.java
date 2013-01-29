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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Helper class to check if a file has a ZIP signature or not.
 *
 * @author Florent Benoit
 */
public class CheckZip {

    /**
     * The first 4 bytes of a zip file.
     */
    private static final byte[] ZIP_SIGNATURE = { 'P', 'K', 0x3, 0x4 };

    /**
     * Check the given stream
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static boolean checkURI(final URI uri) throws CheckZipException {
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new CheckZipException("Unable to check for uri '" + uri + "'", e);
        }
        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();
        } catch (IOException e) {
            throw new CheckZipException("Unable to open connection for uri '" + uri + "'", e);
        }
        urlConnection.setDefaultUseCaches(false);
        byte[] buffer = new byte[ZIP_SIGNATURE.length];
        InputStream is = null;
        try {
            is = urlConnection.getInputStream();
            int read = is.read(buffer);
            if (read != ZIP_SIGNATURE.length) {
                return false;
            }
            return checkBuffer(buffer);
        } catch (IOException e) {
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }


    /**
     * Check the given file
     *
     * @param f
     * @return
     */
    public static boolean checkFile(final RandomAccessFile randomAccessFile) {
        byte[] buffer = new byte[ZIP_SIGNATURE.length];
        try {
            randomAccessFile.readFully(buffer);
            return checkBuffer(buffer);
        } catch (IOException e) {
            return false;
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    return true;
                }
            }
        }
    }

    /**
     * Check the given file
     *
     * @param f
     * @return
     */
    public static boolean checkFile(final File file) throws CheckZipException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            throw new CheckZipException("Unable to open the file '" + file + "'.", e);
        }
        return checkFile(randomAccessFile);
   }





    /**
     * Check the given file
     *
     * @param f
     * @return
     */
    public static boolean checkBuffer(final byte[] buffer) {
        if (buffer == null || buffer.length != ZIP_SIGNATURE.length) {
            return false;
        }

        for (int i = 0; i < ZIP_SIGNATURE.length; i++) {
            if (ZIP_SIGNATURE[i] != buffer[i]) {
                // not matching the signature
                return false;
            }
        }
        return true;
    }
}