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
package com.peergreen.deployment.deploymentplan.ow2util.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;

public class AutodeployProcessor  implements Processor<Content> {

    @Override
    public void handle(Content content, ProcessorContext processorContext) throws ProcessorException {
        try {



            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content.getInputStream()));
            String readLline = null;
            while((readLline = bufferedReader.readLine()) != null) {
                String[] lines = readLline.split(",");
                for (String line : lines) {
                    if (line.contains("\\")) {
                        continue;
                    }

                    // remove trailing spaces
                    line = line.replaceAll(",", "");

                    line = line.trim();

                    // split
                    String[] args = line.split(":");
                    String gId = args[0];
                    String aId = args[1];
                    String version = args[2];
                    String classifier = null;
                    if (args.length >= 4) {
                        if (args[3].startsWith("{")) {
                            classifier = args[3].substring(1, args[3].length() - 1);
                        }
                    }



                    // Add artifact
                    StringBuilder sb = new StringBuilder("mvn:");
                    sb.append(gId);
                    sb.append("/");
                    sb.append(aId);
                    sb.append("/");
                    sb.append(version);
                    if (classifier != null) {
                        sb.append("/jar/");
                        sb.append(classifier);
                    }

                    try {
                        URI m2URI = new URI(sb.toString());
                        String mType = ".jar";
                        String name = gId.concat(":").concat(aId).concat(":").concat(version);
                        if (classifier != null) {
                            name = name.concat("@".concat(classifier));
                        }

                        name = name.concat(mType);

                        Artifact artifact = processorContext.build(name, m2URI);
                        processorContext.addArtifact(artifact);
                    } catch (URISyntaxException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }
        } catch (ContentException | IOException e) {
            throw new ProcessorException("Inavalid content", e);
        }

    }

}
