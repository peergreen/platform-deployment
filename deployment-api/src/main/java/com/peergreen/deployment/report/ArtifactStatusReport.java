/**
 * Copyright 2013 Peergreen S.A.S.
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
package com.peergreen.deployment.report;

import java.net.URI;
import java.util.List;

import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.facet.FacetInfo;
import com.peergreen.deployment.facet.endpoint.Endpoint;

public interface ArtifactStatusReport {

    List<ArtifactError> getExceptions();
    List<ProcessorInfo> getProcessors();
    String name();
    URI uri();

    List<ArtifactStatusReport> children();

    List<FacetInfo> getFacets();

    String toString(String indent);

    List<Endpoint> getEndpoints();

}
