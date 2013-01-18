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
package com.peergreen.deployment.deploymentplan.ow2util.processor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.deploymentplan.ow2util.DeploymentPlan;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;

/**
 * Deployment plan scanner.
 * @author Florent Benoit
 */
public class DeploymentPlanFacetScannerProcessor implements Processor<Content> {

    private final XMLInputFactory xmlInputFactory;

    private static final QName QNAME_DEPLOYMENT = new QName("http://jonas.ow2.org/ns/deployment-plan/1.0", "deployment");
    private static final String  NAMESPACE_MAVEN2 = "http://jonas.ow2.org/ns/deployment-plan/maven2/1.0";



    public DeploymentPlanFacetScannerProcessor() {
        this.xmlInputFactory = XMLInputFactory.newInstance();
    }


    /**
     * Needs to parse the XML file which is the deployment plan.
     */
    @Override
    public void handle(Content content, ProcessorContext processorContext) throws ProcessorException {

        // Parse the content of the XML file
        InputStream is = null;
        try {
            is = content.getInputStream();
        } catch (ContentException e) {
            throw new ProcessorException("Unable to get inputstream", e);
        }

        DeploymentPlan deploymentPlan = new DeploymentPlan();



        XMLEventReader xmlEventReader;
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(new InputStreamReader(is));
        } catch (XMLStreamException e) {
            throw new ProcessorException("Unable to parse inputstream", e);
        }

        while(xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = nextEvent(xmlEventReader);

            try {
                if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {

                    StartElement startElement = xmlEvent.asStartElement();

                    // Got a deployment ?
                    if (QNAME_DEPLOYMENT.equals(startElement.getName())) {

                        Map<String, String> attributesMap = new HashMap<String, String>();

                        Iterator<Attribute> attributes = startElement.getAttributes();
                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            attributesMap.put(attribute.getName().getLocalPart(), attribute.getValue());
                        }

                        Maven2 maven2 = null;

                        // continue while we don't have the end element
                        boolean endOfDeploymentElementNotReached = true;
                        while(endOfDeploymentElementNotReached) {
                            // no more elements...
                            if (!xmlEventReader.hasNext()) {
                                endOfDeploymentElementNotReached = false;
                                break;
                            }

                            xmlEvent = nextEvent(xmlEventReader);


                            // M2 elements ?
                            if (xmlEvent.getEventType() == XMLEvent.START_ELEMENT) {
                                StartElement subStartElement = xmlEvent.asStartElement();
                                if (NAMESPACE_MAVEN2.equals(subStartElement.getName().getNamespaceURI())) {
                                    // not yet created ?
                                    if (maven2 == null) {
                                        maven2 = new Maven2();
                                    }

                                    // Read key
                                    QName m2QName = subStartElement.getName();
                                    String key = m2QName.getLocalPart();

                                    // Read value
                                    xmlEvent = nextEvent(xmlEventReader);
                                    Characters chars = xmlEvent.asCharacters();
                                    String value = chars.getData();


                                    if ("groupId".equals(key)) {
                                        maven2.setGroupId(value);
                                        continue;
                                    }

                                    if ("artifactId".equals(key)) {
                                        maven2.setArtifactId(value);
                                        continue;
                                    }

                                    if ("version".equals(key)) {
                                        maven2.setVersion(value);
                                        continue;
                                    }

                                    if ("type".equals(key)) {
                                        maven2.setType(value);
                                        continue;
                                    }

                                    System.out.println("found not yet analyzed key/Value = " + key + "/" + value);
                                }
                            }

                            if (xmlEvent.getEventType() == XMLEvent.END_ELEMENT && QNAME_DEPLOYMENT.equals(xmlEvent.asEndElement().getName())) {
                                endOfDeploymentElementNotReached = false;
                            }

                        }


                        // Found a maven2 resource
                        if (maven2 != null) {
                            // Add artifact
                            StringBuilder sb = new StringBuilder("mvn:");
                            sb.append(maven2.getGroupId());
                            sb.append("/");
                            sb.append(maven2.getArtifactId());
                            if (maven2.getVersion() != null) {
                                sb.append("/");
                                sb.append(maven2.getVersion());
                            }
                            if (maven2.getType() != null) {
                                sb.append("/");
                                sb.append(maven2.getType());
                            }
                            if (maven2.getClassifier() != null) {
                                sb.append("/");
                                sb.append(maven2.getClassifier());
                            }


                            try {
                                URI m2URI = new URI(sb.toString());
                                String type = "jar";
                                if (maven2.getType() != null) {
                                    type = maven2.getType();
                                }
                                String name = attributesMap.get("id").concat(maven2.getGroupId()).concat(".").concat(maven2.getArtifactId()).concat(".").concat(type);
                                Artifact artifact = processorContext.build(name, m2URI);
                                processorContext.addArtifact(artifact);
                            } catch (URISyntaxException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }

                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }

        // Add facet
        processorContext.addFacet(DeploymentPlan.class, deploymentPlan);
    }


    XMLEvent nextEvent(XMLEventReader xmlEventReader) throws ProcessorException {
        try {
            return xmlEventReader.nextEvent();
        } catch (XMLStreamException e) {
            throw new ProcessorException("Unable to parse inputstream", e);
        }
    }

}
