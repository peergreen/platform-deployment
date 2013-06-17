package com.peergreen.deployment.internal.handler;

import static com.peergreen.deployment.internal.handler.utils.ElementBuilder.element;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.DeploymentContext;
import com.peergreen.deployment.HandlerProcessor;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.internal.handler.desc.DescriptiveRequirementBuilder;
import com.peergreen.deployment.internal.handler.utils.Types;
import com.peergreen.deployment.internal.processor.NamedProcessor;
import com.peergreen.deployment.processor.Attribute;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Manifest;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.deployment.processor.XmlNamespace;
import com.peergreen.deployment.resource.artifact.ArtifactRequirement;
import com.peergreen.deployment.resource.artifact.archive.ArchiveRequirement;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

/**
 * User: guillaume
 * Date: 13/05/13
 * Time: 17:13
 */
@Handler(name = "processor",
         namespace = "com.peergreen.deployment")
@Provides(specifications = HandlerProcessor.class)
public class ProcessingHandler extends PrimitiveHandler implements HandlerProcessor, NamedProcessor {

    private Class<?> processorType;
    private Class<?> parameterType;
    private DelegateHandlerProcessor delegate;

    private DescriptiveRequirementBuilder builder;
    private ProcessorInstance instance = new InstanceManagerInstance();
    private Processor<?> facade;

    public void setInstance(ProcessorInstance factory) {
        this.instance = factory;
    }

    @Override
    public void configure(Element element, Dictionary dictionary) throws ConfigurationException {

    }

    @Override
    public void start() {
        // Retrieve the processor instance
        Object processor = instance.get();
        processorType = processor.getClass();

        // If the component is already a Processor, directly use it (avoid reflection, more efficient)
        if (processor instanceof Processor) {
            facade = (Processor<?>) processor;
            parameterType = Types.findParametrizedType(facade.getClass());
        } else {
            // Build the processor reflection wrapper
            Method method = Types.findMethod(processorType);
            parameterType = method.getParameterTypes()[0];
            facade = new ReflectiveProcessor<>(processor, method);
        }

    }

    @Override
    public void stop() {
        processorType = null;
        facade = null;
        parameterType = null;
    }

    @Validate
    public void validate() {
        buildProcessorDelegate();
        buildRequirements();
    }

    @Invalidate
    public void invalidate() {
        delegate = null;
    }

    private void buildProcessorDelegate() {
        delegate = new DelegateHandlerProcessor(facade, parameterType);
        delegate.bindRequirementBuilder(builder);
    }

    private void buildRequirements() {
        buildPhaseRequirements();
        buildArtifactRequirements();
        buildXmlContentRequirements();
        buildArchiveRequirements();
        buildFacetRequirements();
    }

    private void buildFacetRequirements() {
        // Handle parameter Type
        // DeploymentContext and Artifact are special case since they are not facet themselves
        if ((!DeploymentContext.class.isAssignableFrom(parameterType)
                && (!Artifact.class.isAssignableFrom(parameterType)))) {
            if (Content.class.equals(parameterType)) {
                delegate.addRequirement(builder.buildContentRequirement(delegate));
            } else {
                // Handle normal Facet requirement
                delegate.addRequirement(builder.buildFacetRequirement(delegate, parameterType));
            }
        }
    }

    private void buildArchiveRequirements() {
        if (processorType.isAnnotationPresent(Manifest.class)) {
            Manifest manifest = processorType.getAnnotation(Manifest.class);
            ArchiveRequirement requirement = builder.buildArchiveRequirement(delegate);
            for (Attribute attribute : manifest.value()) {
                if ("".equals(attribute.value())) {
                    requirement.addRequiredAttribute(attribute.name());
                } else  {
                    requirement.addRequiredAttribute(attribute.name(), attribute.value());
                }
            }
            delegate.addRequirement(requirement);
        }
    }

    private void buildXmlContentRequirements() {
        if (processorType.isAnnotationPresent(XmlNamespace.class)) {
            XmlNamespace ns = processorType.getAnnotation(XmlNamespace.class);
            delegate.addRequirement(builder.buildXMLContentRequirement(delegate).setNamespace(ns.value()));
        }
    }

    private void buildArtifactRequirements() {
        if (processorType.isAnnotationPresent(Uri.class)) {
            Uri uri = processorType.getAnnotation(Uri.class);
            ArtifactRequirement requirement = builder.buildArtifactRequirement(delegate);
            if (!"".equals(uri.extension())) {
                requirement.setPathExtension(uri.extension());
            }
            if (!"".equals(uri.value())) {
                requirement.setURIScheme(uri.value());
            }
            delegate.addRequirement(requirement);
        }
    }

    private void buildPhaseRequirements() {
        if (processorType.isAnnotationPresent(Phase.class)) {
            Phase phase = processorType.getAnnotation(Phase.class);
            delegate.addRequirement(builder.buildPhaseRequirement(delegate, phase.value()));
        }
        if (processorType.isAnnotationPresent(Discovery.class)) {
            Discovery discovery = processorType.getAnnotation(Discovery.class);
            delegate.addRequirement(builder.buildPhaseRequirement(delegate, discovery.value().name()));
        }
    }

    @Override
    public void handle(DeploymentContext deploymentContext) throws ProcessorException {
        delegate.handle(deploymentContext);
    }

    @Override
    public Class<?> getExpectedHandleType() {
        return delegate.getExpectedHandleType();
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        return delegate.getCapabilities(namespace);
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        return delegate.getRequirements(namespace);
    }

    @Bind
    public void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
        builder = new DescriptiveRequirementBuilder(requirementBuilder);
    }

    @Override
    public HandlerDescription getDescription() {
        return new HandlerDescription(this) {
            /**
             * Gets handler information.
             * This represent the actual state of the handler.
             *
             * @return the handler information.
             */
            @Override
            public Element getHandlerInfo() {
                Element info = super.getHandlerInfo();

                // General information about processor type
                info.addElement(element("processor").attribute("type", parameterType.getName()).build());
                if (builder != null) {
                    info.addElement(builder.getRequirements());
                }

                return info;
            }

        };
    }

    @Override
    public String getName() {
        return getFactory().getClassName();
    }

    public static interface ProcessorInstance {
        Object get();
    }

    public class InstanceManagerInstance implements ProcessorInstance {

        @Override
        public Object get() {
            return ProcessingHandler.this.getInstanceManager().getPojoObject();
        }

    }
}
