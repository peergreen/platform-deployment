/**
 * Copyright 2013 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import com.peergreen.deployment.monitor.URITracker;
import com.peergreen.deployment.monitor.URITrackerException;

/**
 * Checks URI that are local
 * @author Florent Benoit
 */
@Component
@Provides(properties={@StaticServiceProperty(name="scheme", type="java.lang.String", value="file")})
@Instantiate(name="file:// change tracker")
public class FileTracker implements URITracker {

    /**
     * Some actions needs to be done on Windows only.
     */
    private final static boolean WINDOWS_OS = System.getProperty("os.name").indexOf("win") >= 0;

    @Override
    public long getLastModified(URI uri) {
        return getLastModified(getFile(uri));
    }

    public long getLastModified(File artifact) {
        // A file, get the value
        if (artifact.isFile()) {
            return artifact.lastModified();
        }

        // Directory, needs to compute the upper last modified file
        File[] files = artifact.listFiles();
        long lastModified = 0;
        if (files != null) {
            for (File f : files) {
                lastModified = Math.max(lastModified, getLastModified(f));
            }
        }
        return lastModified;
    }

    protected File getFile(URI uri) {
        return new File(uri.getPath());
    }

    @Override
    public long getLength(final URI uri) throws URITrackerException {
        return getLength(getFile(uri));
    }

    public long getLength(final File artifact) throws URITrackerException {

        // File mode
       if (artifact.isFile()) {
           if (WINDOWS_OS) {
               checkWindowsCompletedFile(artifact);
           }
           return artifact.length();
       }

       // Directory mode
       long size = 0;
       File[] children = artifact.listFiles();
       if (children != null) {
           for (File child : children) {
               // Add the size of the child
               size += getLength(child);
           }
       }

       return size;

   }


    /**
     * Windows method to test if the file is being manipulated or not.
     * @param artifact the artifact to test
     * @throws IOException if the given file is not completed (only thrown
     *                     under windows when file is not completed)
     */
    protected void checkWindowsCompletedFile(final File artifact) throws URITrackerException {
        if (artifact.isFile()) {
            //  Throw an exception if the file being copied is not yet completed
            try (FileInputStream fis = new FileInputStream(artifact)) {

            } catch (IOException e) {
                throw new URITrackerException("Cannot read length of the file '" + artifact + "' as the file is being updated", e);
            }
        }
    }

    @Override
    public boolean exists(final URI uri) {
        return exists(getFile(uri));
    }

    public boolean exists(final File artifact) {
        return artifact.exists();
    }

}
