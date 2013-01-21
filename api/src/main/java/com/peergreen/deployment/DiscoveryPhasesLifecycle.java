/**
 * Copyright 2012-2013 Peergreen S.A.S.
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
package com.peergreen.deployment;

public enum DiscoveryPhasesLifecycle  {

    /**
     * Manage to download/fetch artifacts. For example it can download/fetch maven2 resources.
     */
    URI_FETCHER ,

    /**
     * URI resolver is in general applied on a local resource (file, bundle) on which the type can be detected (Archive or Content).
     */
    URI_RESOLVER,

    /**
     * Scanners are called on the selected artifact. It will flag the archive or content with expected facets.
     */
    FACET_SCANNER,

    /**
     * Try to Remove or add facets on artifacts if some of the facets are not compliant together.
     */
    FACET_CONFLICTS,

    /**
     * Dependency finder : It tries to find all dependencies of a selected artifact. (for example a deployment plan can reference several other artifacts)
     */
    DEPENDENCY_FINDER,

}
