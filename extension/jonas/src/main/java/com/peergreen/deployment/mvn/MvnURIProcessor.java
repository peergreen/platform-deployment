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
package com.peergreen.deployment.mvn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;

/**
 * mvn: URI processor
 * @author Florent Benoit
 */
public class MvnURIProcessor implements Processor<Artifact> {

    public MvnURIProcessor() {
    }


    @Override
    public void handle(Artifact artifact, ProcessorContext processorContext) throws ProcessorException {

        String userDir = System.getProperty("user.dir");
        File cacheDir = new File(userDir + File.separator + "mvn-cache");

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        // Dump inputstream if not exists
        File dumpedFile = new File(cacheDir, artifact.name());

        // File doesn't exists
        if (!dumpedFile.exists()) {



            // It's a mvn URI from requirements, so get the resource
            try (InputStream is = artifact.uri().toURL().openStream(); FileOutputStream fos = new FileOutputStream(dumpedFile)) {


            int len;
            byte[] b = new byte[4096];
            // dump stream
            while ((len = is.read(b, 0, b.length)) != -1) {
                    fos.write(b, 0, len);
                }
            } catch (IOException e) {
                throw new ProcessorException("Unable to read inputstream from '" + artifact.uri() + "'", e);
            }

        }

        // Now, add the dumped artifact
        URI dumpedURI = dumpedFile.toURI();
        Artifact newArtifact = processorContext.build(artifact.name(), dumpedURI);
        processorContext.addArtifact(newArtifact);

    }


}
