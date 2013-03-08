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
package com.peergreen.deployment.internal.artifact;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.content.Content;

public class TestImmutableArtifact {

    private final ArtifactBuilder artifactBuilder;

    private final static String NAME = "ArtifactName";
    private final URI uri;
    private final Artifact artifact;

    public TestImmutableArtifact() throws URISyntaxException {
        this.artifactBuilder = new ImmutableArtifactBuilder();
        this.uri = new URI("test://myURI");
        this.artifact = artifactBuilder.build(NAME, uri);
    }


    @Test
    public void testArtifact() {
        assertEquals(NAME,  artifact.name());
        assertEquals(uri,  artifact.uri());
    }


    @Test(dependsOnMethods="testArtifact")
    public void testImmutableArtifact() {
        assertNull(artifact.as(Archive.class));
        assertNull(artifact.as(Content.class));
     }

    @Test(dependsOnMethods="testArtifact")
    public void testToString() {
        assertEquals(artifact.toString(), "Artifact[name=ArtifactName, uri=test://myURI]");
    }



}