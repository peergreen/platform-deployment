package com.peergreen.deployment.internal.handler.desc;

import java.util.Map;

import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.artifact.ArtifactRequirement;

/**
 * User: guillaume
 * Date: 28/05/13
 * Time: 11:07
 */
public class DelegateArtifactRequirement implements ArtifactRequirement {
    private final ArtifactRequirement delegate;

    public DelegateArtifactRequirement(final ArtifactRequirement delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T extends ArtifactRequirement> T setPathExtension(final String pathExtension) {
        return delegate.setPathExtension(pathExtension);
    }

    @Override
    public <T extends ArtifactRequirement> T setURIScheme(final String scheme) {
        return delegate.setURIScheme(scheme);
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
