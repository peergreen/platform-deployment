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
package com.peergreen.deployment.internal.tests.artifact;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.peergreen.deployment.internal.artifact.FacetArtifact;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifact;

public class TestFacetArtifact {

    private final IFacetArtifact artifact;

    private final static String NAME = "FacetArtifact";

    private final URI URI;

    public TestFacetArtifact() throws URISyntaxException {
        this.URI = new URI("test://myURI");
        this.artifact = new FacetArtifact(new ImmutableArtifact(NAME, URI));
    }

    @Test
    public void testAddRemoveFacet() {
        // check not available before
        Assert.assertNull(artifact.as(Toto.class));

        Toto toto = new Toto();
        artifact.addFacet(toto, null);

        // Check facet
        Toto getToto = artifact.as(Toto.class);
        Assert.assertNotNull(getToto);
        Assert.assertEquals(getToto, toto);

        // Check remove
        artifact.removeFacet(Toto.class);
        Toto getToto2 = artifact.as(Toto.class);
        Assert.assertNull(getToto2);

    }

    @Test
    public void testAddRemoveInheritanceFacet() {
        // check not available before
        Assert.assertNull(artifact.as(Titi.class));

        Titi titi = new Titi();
        artifact.addFacet(titi, null);

        // Check facet
        Titi getTitiClass = artifact.as(Titi.class);
        SuperInterface getTitiSuperInterface = artifact.as(SuperInterface.class);
        SuperSuperInterface getTitiSuperSuperInterface = artifact.as(SuperSuperInterface.class);

        // not null
        Assert.assertNotNull(getTitiClass);
        Assert.assertNotNull(getTitiSuperInterface);
        Assert.assertNotNull(getTitiSuperSuperInterface);

        // equals to our value
        Assert.assertEquals(getTitiClass,titi);
        Assert.assertEquals(getTitiSuperInterface,titi);
        Assert.assertEquals(getTitiSuperSuperInterface,titi);

        // Check remove
        artifact.removeFacet(Titi.class);
        Titi getTitiClass2 = artifact.as(Titi.class);
        SuperInterface getTitiSuperInterface2 = artifact.as(SuperInterface.class);
        SuperSuperInterface getTitiSuperSuperInterface2 = artifact.as(SuperSuperInterface.class);

        // it removes the associated facet
        Assert.assertNull(getTitiClass2);

        // but not the inherited facets
        Assert.assertNotNull(getTitiSuperInterface2);
        Assert.assertNotNull(getTitiSuperSuperInterface2);
        Assert.assertEquals(getTitiSuperInterface2,titi);
        Assert.assertEquals(getTitiSuperSuperInterface2,titi);

    }


    static class Toto {
        public String getValue() {
            return "DUMMY";
        }
    }

    static interface SuperSuperInterface {
        String getFirstValue();
    }


    static interface SuperInterface extends SuperSuperInterface {
        String getSecondValue();
    }

    static class Titi implements SuperInterface {

        @Override
        public String getFirstValue() {
            return "TITIA";
        }

        @Override
        public String getSecondValue() {
            return "TITIB";
        }

    }

}