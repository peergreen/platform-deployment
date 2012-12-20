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

import com.peergreen.deployment.internal.resource.AbsDeploymentCapability;
import com.peergreen.deployment.resource.artifact.content.XMLContentCapability;
import com.peergreen.deployment.resource.artifact.content.XMLContentNamespace;

public class XMLContentCapabilityImpl extends AbsDeploymentCapability implements XMLContentCapability {


    public XMLContentCapabilityImpl(Resource resource) {
        super(resource, XMLContentNamespace.XML_CONTENT_NAMESPACE, "");
    }

    @Override
    public XMLContentCapability setNamespace(String xmlNamespace) {

        String existingFileExtension = (String) getAttributes().get(XMLContentNamespace.CAPABILITY_XMLNAMESPACE_ATTRIBUTE);
        if (existingFileExtension != null) {
            throw new IllegalArgumentException("xml namespace already set");
        }
        addAttribute(XMLContentNamespace.CAPABILITY_XMLNAMESPACE_ATTRIBUTE, xmlNamespace);
        return this;
    }

}
