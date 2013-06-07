package com.peergreen.deployment.internal.handler.internal.desc;

import static com.peergreen.deployment.internal.handler.internal.utils.ElementBuilder.element;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.resource.Resource;

import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;
import com.peergreen.deployment.resource.artifact.content.ContentRequirement;
import com.peergreen.deployment.resource.artifact.content.XMLContentRequirement;
import com.peergreen.deployment.resource.builder.RequirementBuilder;
import com.peergreen.deployment.resource.facet.FacetRequirement;
import com.peergreen.deployment.resource.phase.PhaseRequirement;

/**
 * User: guillaume
 * Date: 28/05/13
 * Time: 10:52
 */
public class DescriptiveRequirementBuilder implements RequirementBuilder {
    private final RequirementBuilder delegate;
    private Element requirements = new Element("requirements", null);

    public DescriptiveRequirementBuilder(final RequirementBuilder delegate) {
        this.delegate = delegate;
    }

    public Element getRequirements() {
        return requirements;
    }

    @Override
    public ContentRequirement buildContentRequirement(final Resource resource) {
        requirements.addElement(element("content").build());
        return delegate.buildContentRequirement(resource);
    }

    @Override
    public ArchiveRequirement buildArchiveRequirement(final Resource resource) {
        final Element manifest = new Element("manifest", null);
        requirements.addElement(manifest);
        return new DelegateArchiveRequirement(delegate.buildArchiveRequirement(resource)) {
            @Override
            public ArchiveRequirement addRequiredAttribute(final String attributeName) {
                manifest.addElement(element("attr").attribute("name", attributeName).build());
                return super.addRequiredAttribute(attributeName);
            }

            @Override
            public ArchiveRequirement addRequiredAttribute(final String attributeName, final String expectedValue) {
                manifest.addElement(
                        element("attr")
                                .attribute("name", attributeName)
                                .attribute("value", expectedValue)
                                .build());
                return super.addRequiredAttribute(attributeName, expectedValue);
            }
        };
    }

    @Override
    public PhaseRequirement buildPhaseRequirement(final Resource resource, final String phaseName) {
        return delegate.buildPhaseRequirement(resource, phaseName);
    }

    @Override
    public XMLContentRequirement buildXMLContentRequirement(final Resource resource) {
        final Element xml = new Element("xml", null);
        requirements.addElement(xml);
        return new DelegateXMLContentRequirement(delegate.buildXMLContentRequirement(resource)) {
            @Override
            public XMLContentRequirement setNamespace(final String namespace) {
                xml.addAttribute(new Attribute("namespace", namespace));
                return super.setNamespace(namespace);
            }
        };
    }

    @Override
    public ArtifactRequirement buildArtifactRequirement(final Resource resource) {
        final Element uri = new Element("uri", null);
        requirements.addElement(uri);
        return new DelegateArtifactRequirement(delegate.buildArtifactRequirement(resource)) {
            @Override
            public <T extends ArtifactRequirement> T setPathExtension(final String pathExtension) {
                uri.addAttribute(new Attribute("extension", pathExtension));
                return super.setPathExtension(pathExtension);
            }

            @Override
            public <T extends ArtifactRequirement> T setURIScheme(final String scheme) {
                uri.addAttribute(new Attribute("scheme", scheme));
                return super.setURIScheme(scheme);
            }
        };
    }

    @Override
    public FacetRequirement buildFacetRequirement(final Resource resource, final Class<?> facetClass) {
        requirements.addElement(element("facet").attribute("type", facetClass.getName()).build());
        return delegate.buildFacetRequirement(resource, facetClass);
    }
}
