package com.peergreen.deployment.internal.handler.internal.utils;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

/**
 * User: guillaume
 * Date: 27/05/13
 * Time: 10:47
 */
public class ElementBuilder {

    private final Element element;

    public ElementBuilder(String name) {
        this(new Element(name, null));
    }

    public ElementBuilder(final Element element) {
        this.element = element;
    }

    public ElementBuilder attribute(String name, String value) {
        element.addAttribute(new Attribute(name, value));
        return this;
    }

    public ElementBuilder element(Element child) {
        this.element.addElement(child);
        return this;
    }

    public Element build() {
        return element;
    }

    public static ElementBuilder element(String name) {
        return new ElementBuilder(name);
    }
}
