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

import static org.mockito.Mockito.doReturn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.internal.processor.NamedProcessor;

public class TestFacetArtifact {

    private IFacetArtifact artifact;

    private final static String NAME = "FacetArtifact";

    private URI URI;

    @Mock
    private NamedProcessor processor;


    @BeforeMethod
    public void init() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
        this.URI = new URI("test://myURI");
        this.artifact = new FacetArtifact(new ImmutableArtifact(NAME, URI));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testAddNullFacet() {
        artifact.addFacet(null, null);
    }


    @Test
    public void testAddAndCheckException() {
        Exception e = new IllegalStateException();
        artifact.addException(e);
        List<Throwable> exceptions = artifact.getExceptions();
        Assert.assertEquals(exceptions.size(), 1);
        Assert.assertEquals(exceptions.iterator().next(), e);
    }


    @Test
    public void testAddWithProcessor() {
        Toto toto = new Toto();
        String processorName = "myProcessorName";
        doReturn(processorName).when(processor).getName();

        artifact.addFacet(toto, processor);

        Collection<FacetInfo> facetInfos = artifact.getFacetInfos();
        Assert.assertEquals(facetInfos.size(), 1);
        FacetInfo facetInfo = facetInfos.iterator().next();
        Assert.assertEquals(facetInfo.getName(), Toto.class.getName());
        Assert.assertEquals(facetInfo.getProcessor(), processorName);
    }


    @Test
    public void testAddWithNullProcessor() {
        Toto toto = new Toto();
        artifact.addFacet(Toto.class, toto, null);

        Collection<FacetInfo> facetInfos = artifact.getFacetInfos();
        Assert.assertEquals(facetInfos.size(), 1);
        FacetInfo facetInfo = facetInfos.iterator().next();
        Assert.assertEquals(facetInfo.getName(), Toto.class.getName());
        Assert.assertNull(facetInfo.getProcessor());
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