package com.peergreen.deployment.internal.model.persistence.read;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 14/01/13
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public interface Parser {
    void build(XMLStreamReader reader) throws XMLStreamException;
}
