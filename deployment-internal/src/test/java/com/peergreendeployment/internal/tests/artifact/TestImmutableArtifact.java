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
package com.peergreendeployment.internal.tests.artifact;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.artifact.ImmutableArtifactBuilder;

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
        Assert.assertEquals(NAME,  artifact.name());
        Assert.assertEquals(uri,  artifact.uri());
    }


    @Test(dependsOnMethods="testArtifact")
    public void testImmutableArtifact() {
        Assert.assertNull(artifact.as(Archive.class));
        Assert.assertNull(artifact.as(Content.class));
     }




}