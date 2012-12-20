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
package com.peergreen.deployment.internal.facet.xmlcontent.resource;

import org.osgi.resource.Resource;
import org.ow2.util.osgi.toolkit.filter.Filters;
import org.ow2.util.osgi.toolkit.filter.IFilter;

import com.peergreen.deployment.internal.resource.AbsDeploymentRequirement;
import com.peergreen.deployment.resource.artifact.content.XMLContentNamespace;
import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;

public class XMLContentRequirementImpl extends AbsDeploymentRequirement implements XMLContentRequirement {

    private String xmlNamespace;

    public XMLContentRequirementImpl(Resource resource) {
        super(resource, XMLContentNamespace.XML_CONTENT_NAMESPACE);
    }

    @Override
    public XMLContentRequirement setNamespace(String xmlNamespace) {
        if (this.xmlNamespace != null) {
            throw new IllegalArgumentException("XML namespace already set");
        }
        this.xmlNamespace = xmlNamespace;
        recomputeFilterValue();
        return this;
    }

    @Override
    protected IFilter computeFilter(IFilter filter) {
        if (xmlNamespace != null) {
            return Filters.and(filter, Filters.equal(XMLContentNamespace.CAPABILITY_XMLNAMESPACE_ATTRIBUTE, xmlNamespace));
        }
        return filter;
    }
}
