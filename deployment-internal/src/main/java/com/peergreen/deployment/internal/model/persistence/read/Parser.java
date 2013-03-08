package com.peergreen.deployment.internal.model.persistence.read;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Guillaume Sauthier
 */
public interface Parser {
    void build(XMLStreamReader reader) throws XMLStreamException;
}
