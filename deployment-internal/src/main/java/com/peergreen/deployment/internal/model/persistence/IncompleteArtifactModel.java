package com.peergreen.deployment.internal.model.persistence;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.internal.model.DefaultArtifactModel;

/**
 * Created with IntelliJ IDEA.
 * User: guillaume
 * Date: 16/01/13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
public class IncompleteArtifactModel {
    private DefaultArtifactModel model;
    private List<FacetBuilderReference> references = new ArrayList<>();

    public DefaultArtifactModel getModel() {
        return model;
    }

    public void setModel(DefaultArtifactModel model) {
        this.model = model;
    }

    public List<FacetBuilderReference> getReferences() {
        return references;
    }

}
