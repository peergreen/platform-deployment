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
package com.peergreen.deployment.internal.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.FacetBuilderInfo;
import com.peergreen.deployment.InternalFacetBuilder;
import com.peergreen.deployment.InternalPersistenceArtifactManager;
import com.peergreen.deployment.facet.builder.BuilderContext;
import com.peergreen.deployment.facet.builder.FacetBuilderException;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.model.persistence.ArtifactModelPersistence;
import com.peergreen.deployment.internal.model.persistence.BuilderContextFactory;
import com.peergreen.deployment.internal.model.persistence.PersistenceException;
import com.peergreen.deployment.internal.solver.MissingCapability;
import com.peergreen.deployment.internal.solver.ResolveContextImpl;
import com.peergreen.deployment.model.ArtifactModelManager;
import com.peergreen.deployment.monitor.URITrackerException;
import com.peergreen.deployment.monitor.URITrackerManager;
import com.peergreen.tree.Graph;
import com.peergreen.tree.Node;
import com.peergreen.tree.graph.SimpleGraph;
import com.peergreen.tree.node.SimpleNode;
import com.peergreen.tree.visitor.topological.TopologicalSortGraphVisitor;

@Component
@Provides
@Instantiate
public class DefaultArtifactModelManager implements ArtifactModelManager, InternalArtifactModelManager {

    /**
     * Persistence model file name.
     */
    private static final String PERSISTENCE_FILENAME = "artifact-model.xml";


    /**
     * Temporary Persistence model file name.
     */
    private static final String PERSISTENCE_FILENAME_TEMP = "artifact-model.xml.tmp";


    /**
     * Map between the root artifact URI and the associated artifact model.
     */
    private final Map<URI, InternalArtifactModel> artifactsByURI;

    /**
     * Collection of PersistenceArtifactManager.
     */
    private final Set<InternalPersistenceArtifactManager> persistenceArtifactManagers;

    /**
     * URI tracker.
     */
    @Requires
    private URITrackerManager uriTrackerManager;

    /**
     * OSGi solver.
     */
    private Resolver resolver;


    private final BuilderContextFactory builderContextFactory;

    /**
     * Persistence of the model.
     */
    private ArtifactModelPersistence artifactModelPersistence;

    /**
     * File for persistence.
     */
    private final File persistenceFile;

    /**
     * Temporary file for persistence.
     */
    private final File persistenceFileTmp;

    /**
     * Model loaded ?
     */
    private boolean loadedModel;


    /**
     * List of registered facet builders
     */
    private final Set<InternalFacetBuilder<?>> facetBuilders;


    /**
     * Default constructor.
     * @param bundleContext the bundle context used to get persistence data.
     */
    public DefaultArtifactModelManager(BundleContext bundleContext) {
        this.persistenceFile = bundleContext.getDataFile(PERSISTENCE_FILENAME);
        this.persistenceFileTmp = bundleContext.getDataFile(PERSISTENCE_FILENAME_TEMP);
        this.persistenceArtifactManagers = new HashSet<>();
        this.artifactsByURI = Collections.synchronizedMap(new HashMap<URI, InternalArtifactModel>());
        this.builderContextFactory = new BuilderContextFactory();
        this.facetBuilders = new HashSet<>();
    }

    @Override
    public void addArtifactModel(URI uri, InternalArtifactModel artifactModel) {
        artifactsByURI.put(uri, artifactModel);
    }

    @Override
    public InternalArtifactModel getArtifactModel(URI uri) {
        return artifactsByURI.get(uri);
    }

    public InternalArtifactModel getView(URI uri) {
        return artifactsByURI.get(uri);
    }

    /**
     * @return a snapshot view of the deployed URIs
     */
    @Override
    public Collection<URI> getDeployedRootURIs() {
        List<URI> uris = new ArrayList<URI>();
        // Make a copy in a synchronized block to avoid concurrent modification exceptions
        Set<Entry<URI, InternalArtifactModel>> artifactsEntries;
        synchronized (artifactsByURI) {
            artifactsEntries = new HashSet<>(artifactsByURI.entrySet());
        }
        for (Entry<URI, InternalArtifactModel> entry : artifactsEntries) {
            // Exclude artifacts being un-deployed
            if (entry.getValue().isUndeployed()) {
                continue;
            }
            uris.add(entry.getKey());
        }

        return uris;
    }

    @Override
    public Collection<InternalArtifactModel> getDeployedRootArtifacts() {
        List<InternalArtifactModel> artifactModels = new ArrayList<InternalArtifactModel>();
        // Make a copy in a synchronized block to avoid concurrent modification exceptions
        Set<InternalArtifactModel> artifactModelsSet;
        synchronized (artifactsByURI) {
            artifactModelsSet = new HashSet<>(artifactsByURI.values());
        }
        for (InternalArtifactModel artifactModel : artifactModelsSet) {
            // Exclude artifacts being un-deployed
            if (artifactModel.isUndeployed()) {
                continue;
            }
            artifactModels.add(artifactModel);
        }

        return artifactModels;
    }

    @Override
    public void updateLengthLastModified(InternalArtifactModel artifactModel) {
        URI uri = artifactModel.getFacetArtifact().uri();
        try {
            artifactModel.setLastModified(uriTrackerManager.getLastModified(uri));
        } catch (URITrackerException e) {
            // Unable to read
            artifactModel.setLastModified(-1);
        }

        try {
            artifactModel.setArtifactLength(uriTrackerManager.getLength(uri));
        } catch (URITrackerException e) {
            // Unable to read
            artifactModel.setArtifactLength(-1);
        }

    }

    public void store() {

        // First, we write on the temporary file

        try (Writer writer = new FileWriter(persistenceFileTmp)) {
            try {
            // store the model
            artifactModelPersistence.store(this, writer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // No problem so move the temp file to the right file
        try {
            Files.move(persistenceFileTmp.toPath(), persistenceFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    protected void load() {

        if (!persistenceFile.exists()) {
            loadedModel = true;
            return;
        }

        Reader reader;
        try {
            reader = new FileReader(persistenceFile);
            artifactModelPersistence.load(this, reader);
        } catch (FileNotFoundException | PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Notify all persistence artifact manager
        for (InternalPersistenceArtifactManager persistenceArtifactManager : persistenceArtifactManagers) {
            notify(persistenceArtifactManager);
        }



        loadedModel = true;

    }

    protected void notify(InternalPersistenceArtifactManager persistenceArtifactManager) {
        // Notify with all artifacts that are not persistent
        // Get all artifacts
        Collection<InternalArtifactModel> artifactModels = artifactsByURI.values();
        Collection<Artifact> forgetArtifacts = new HashSet<>();
        for (InternalArtifactModel artifactModel : artifactModels) {
            if (!artifactModel.isPersistent()) {
                forgetArtifacts.add(artifactModel.getFacetArtifact());
            }
        }

        persistenceArtifactManager.forget(forgetArtifacts);
    }


    /**
     *
     * @param persistenceArtifactManager
     */
    @Bind(aggregate=true)
    public void bindInternalPersistenceArtifactManager(InternalPersistenceArtifactManager persistenceArtifactManager) {
        // add it to the list
        persistenceArtifactManagers.add(persistenceArtifactManager);

        // already load the model so notify this persistence artifact manager
        if (loadedModel) {
            notify(persistenceArtifactManager);
        }

        // else we delay the notification until that the persistence implementation is here

    }

    @Unbind(aggregate=true)
    public void unbindInternalPersistenceArtifactManager(InternalPersistenceArtifactManager persistenceArtifactManager) {
        // remove it from the list
        persistenceArtifactManagers.remove(persistenceArtifactManager);

    }



    @Bind
    public void bindArtifactModelPersistence(ArtifactModelPersistence artifactModelPersistence) {
        // we're receiving the artifact model persistence.
        this.artifactModelPersistence = artifactModelPersistence;

        // load the model
        try {
        load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unbind
    public void unbindArtifactModelPersistence(ArtifactModelPersistence artifactModelPersistence) {
        // Save before that the component becomes not available
        store();
        loadedModel = false;
    }

    @Override
    public void save() {
        // store
        store();
    }

    private boolean validated = false;


    @Validate
    public void init() {
        validated = true;


        // All has been injected, check the builders
        applyBuildersOnArtifacts();

    }

    @Invalidate
    public void stop() {
        validated = false;
    }


    /**
     * Apply all the builders on the persistent artifact.
     */
    protected void applyBuildersOnArtifacts() {
        // list empty, this will be for the next time
        if (facetBuilders.isEmpty()) {
            return;
        }

        // Calls the builders for the selected artifact model
        for (InternalArtifactModel artifactModel : artifactsByURI.values()) {
            // Apply only on persistent artifact
            // (do not need to do a recovery for non persistent artifacts)
            if (artifactModel.isPersistent()) {
                applyArtifactFacetBuilder(artifactModel);
            }
        }
    }

    @Bind(aggregate=true)
    public void bindInternalFacetBuilder(InternalFacetBuilder<?> facetBuilder) {

        // add the facet builder to the list
        facetBuilders.add(facetBuilder);

        // We don't have yet all the dependencies
        if (!validated) {
            return;
        }

        // Check the remaining builders
        applyBuildersOnArtifacts();


    }


    @Unbind(aggregate=true)
    public void unbindInternalFacetBuilder(InternalFacetBuilder<?> facetBuilder) {

        // Remove the facet builder from the list
        facetBuilders.remove(facetBuilder);
    }


    protected InternalFacetBuilder<?> getFacetBuilder(String name) {
        for (InternalFacetBuilder<?> facetBuilder : facetBuilders) {
            if (name.equals(facetBuilder.getName())) {
                // found
                return facetBuilder;
            }
        }
        // not found
        return null;
    }


    protected List<InternalFacetBuilder<?>> getBuildersWithSatistiedRequirements(InternalArtifactModel artifactModel) {

        // list of resources for the solver
        Collection<Resource> resources = new HashSet<>();
        Collection<Resource> mandatoryResources = new HashSet<>();

        // list of resolution facet builders
        Collection<ResolutionFacetBuilder> resolutionFacetBuilders = new HashSet<>();

        Set<Node<InternalFacetBuilder<?>>> nodes = new HashSet<>();
        Map<Resource, SimpleNode<InternalFacetBuilder<?>>> facet2node = new HashMap<>();


        // add all builders required and bound for the given artifact
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();
        List<FacetBuilderInfo> facetBuilderInfos = facetArtifact.getFacetBuilders();
        for (FacetBuilderInfo facetBuilderInfo : facetBuilderInfos) {
            InternalFacetBuilder<?> foundFacetBuilder = getFacetBuilder(facetBuilderInfo.getName());
            // not yet found, continue
            if (foundFacetBuilder == null) {
                continue;
            }
            ResolutionFacetBuilder resolutionFacetBuilder = new ResolutionFacetBuilder(facetBuilderInfo, foundFacetBuilder);
            resolutionFacetBuilders.add(resolutionFacetBuilder);
            SimpleNode<InternalFacetBuilder<?>> node = new SimpleNode<InternalFacetBuilder<?>>(foundFacetBuilder);
            facet2node.put(resolutionFacetBuilder, node);
            facet2node.put(resolutionFacetBuilder.getFacetBuilder(), node);
        }


        // Add the builders on resource, mandatory
        resources.addAll(resolutionFacetBuilders);
        mandatoryResources.addAll(resources);

        // No optional resources (we need to know all valid builders)
        List<Resource> optionalResources = Collections.emptyList();

        // Create wirings
        Map<Resource, Wiring> wirings = new HashMap<Resource, Wiring>();

        // Create the context
        ResolveContext resolveContext = new ResolveContextImpl(resources, wirings, mandatoryResources, optionalResources);

        Map<Resource, List<Wire>> wireMap = null;
        try {
            wireMap = resolver.resolve(resolveContext);
        } catch (ResolutionException e) {
            //FIXME: fix throw of exception
            e.printStackTrace();
        }

        // Now check that for each facet builder, requirements (and transitive requirements) are OK
        for (ResolutionFacetBuilder builder : resolutionFacetBuilders) {
            List<Wire> wires = wireMap.get(builder);

            // No dependency so it's OK, this is satisfied
            boolean foundMissing = false;

            // Get current node
            SimpleNode<InternalFacetBuilder<?>> nodeFacetBuilder = facet2node.get(builder);

            // If one requirement is not provided, one requirement is missing
            for (Wire wire : wires) {
                if (wire.getCapability() instanceof MissingCapability) {
                    foundMissing = true;
                    break;
                }

                // check the facet builder
                if (!hasAllRequirementsProvided((InternalFacetBuilder<?>) wire.getProvider(), wireMap)) {
                    // dependency doesn't have all requirements so we break
                    foundMissing = true;
                    break;
                }


                // add a link between provider and consumer
                InternalFacetBuilder<?> provider = (InternalFacetBuilder<?>) wire.getCapability().getResource();
                Node<InternalFacetBuilder<?>> linkedFacetBuilder =  facet2node.get(provider);
                nodeFacetBuilder.addChild(linkedFacetBuilder);

            }

            // No missing requirement so we can add this item
            // Needs to check that if we've a dependency on a dependency wich is not satisified it should fail !!
            // For example if FacetABuilder needs a capability provided by FacetB Builder but FacetB builder still has a missing requirement it should fail !
            if (!foundMissing) {
                // Create node
                nodes.add(nodeFacetBuilder);
            }
        }

        Graph<InternalFacetBuilder<?>> graph = new SimpleGraph<>(nodes);
        TopologicalSortGraphVisitor<InternalFacetBuilder<?>> visitor = new TopologicalSortGraphVisitor<>();
        try {
            graph.walk(visitor);
        } catch (Exception e) {
            //FIXME: fix throw of exception
            e.printStackTrace();
            return Collections.emptyList();
        }

        // Now, needs to sort the list
        List<InternalFacetBuilder<?>> sortedBuilders = visitor.getSortedInnerList();

        return sortedBuilders;
    }


    protected boolean hasAllRequirementsProvided(InternalFacetBuilder<?> facetBuilder, Map<Resource, List<Wire>> wireMap) {
        boolean hasAllRequirements = true;

        List<Wire> wires = wireMap.get(facetBuilder);

        // If one requirement is not provided, one requirement is missing
        for (Wire wire : wires) {
            if (wire.getCapability() instanceof MissingCapability) {
                hasAllRequirements = false;
                break;
            }
            // dependency
            // check if dependency has all its requirements too
            hasAllRequirements = (hasAllRequirements && hasAllRequirementsProvided((InternalFacetBuilder<?>) wire.getProvider(), wireMap));
        }

        return hasAllRequirements;
    }


    /**
     * Apply all the matching builders in a correct oder for the given artifact model.
     * @param artifactModel the model on which we needs to recover the facets.
     */
    protected void applyArtifactFacetBuilder(InternalArtifactModel artifactModel) {

        // Facet Artifact ?
        IFacetArtifact facetArtifact = artifactModel.getFacetArtifact();

        List<InternalFacetBuilder<?>> builders = getBuildersWithSatistiedRequirements(artifactModel);
        for (InternalFacetBuilder<?> facetBuilder: builders) {

            Class<?> clazz = facetBuilder.getFacetClass();
            @SuppressWarnings("rawtypes")
            BuilderContext builderContext = builderContextFactory.build(clazz, facetArtifact, facetBuilder.getName());

            // call the builder
            try {
                facetBuilder.build(builderContext);
            } catch (FacetBuilderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    protected boolean referenceFacetBuilder(IFacetArtifact facetArtifact, String facetBuilderName) {
        List<FacetBuilderInfo> facetBuilderInfos = facetArtifact.getFacetBuilders();
        for (FacetBuilderInfo facetBuilderInfo :facetBuilderInfos) {
            if (facetBuilderInfo.getName().equals(facetBuilderName)) {
                return true;
            }
        }
        return false;
    }


    @Bind
    protected void bindResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    @Unbind
    protected void unbindResolver(Resolver resolver) {
        this.resolver = null;
    }

}
