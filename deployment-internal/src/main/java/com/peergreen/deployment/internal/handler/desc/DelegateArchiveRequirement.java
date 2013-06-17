package com.peergreen.deployment.internal.handler.desc;

import java.util.Map;

import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;

/**
 * User: guillaume
 * Date: 28/05/13
 * Time: 11:00
 */
public class DelegateArchiveRequirement implements ArchiveRequirement {
    private final ArchiveRequirement delegate;

    public DelegateArchiveRequirement(final ArchiveRequirement delegate) {
        this.delegate = delegate;
    }

    @Override
    public ArchiveRequirement addRequiredAttribute(final String attributeName) {
        return delegate.addRequiredAttribute(attributeName);
    }

    @Override
    public ArchiveRequirement addRequiredAttribute(final String attributeName, final String expectedValue) {
        return delegate.addRequiredAttribute(attributeName, expectedValue);
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
