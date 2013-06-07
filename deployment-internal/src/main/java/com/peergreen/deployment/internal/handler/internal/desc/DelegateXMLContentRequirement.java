package com.peergreen.deployment.internal.handler.internal.desc;

import java.util.Map;

import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;

/**
 * User: guillaume
 * Date: 28/05/13
 * Time: 11:04
 */
public class DelegateXMLContentRequirement implements XMLContentRequirement {
    private final XMLContentRequirement delegate;

    public DelegateXMLContentRequirement(final XMLContentRequirement delegate) {
        this.delegate = delegate;
    }

    @Override
    public XMLContentRequirement setNamespace(final String namespace) {
        return delegate.setNamespace(namespace);
    }

    @Override
    public String getNamespace() {
        return delegate.getNamespace();
    }

    @Override
    public Map<String, String> getDirectives() {
        return delegate.getDirectives();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Resource getResource() {
        return delegate.getResource();
    }

    @Override
    public boolean equals(final Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
