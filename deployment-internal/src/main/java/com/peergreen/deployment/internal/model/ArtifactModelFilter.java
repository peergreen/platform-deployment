package com.peergreen.deployment.internal.model;

/**
 * User: guillaume
 * Date: 15/06/13
 * Time: 21:13
 */
public interface ArtifactModelFilter {
    boolean accept(InternalArtifactModel model);
}
