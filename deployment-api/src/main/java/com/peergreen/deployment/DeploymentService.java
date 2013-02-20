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

package com.peergreen.deployment;

import java.util.Collection;

import com.peergreen.deployment.report.ArtifactStatusReport;
import com.peergreen.deployment.report.ArtifactStatusReportException;
import com.peergreen.deployment.report.DeploymentStatusReport;

/**
 * Interface of the deployment service.
 * It handles the list of artifacts or request and return the result of the processing
 * @author Florent Benoit
 */
public interface DeploymentService {

    /**
     * Process of the deployment of the given list of requests.
     * It accepts a collection, so the order it accepts is based on the underlying collection.
     * For an ordered deployment, a list should be given.
     * @param artifactProcessRequests the list of the requests.
     * @return a report for the given request
     */
    DeploymentStatusReport process(Collection<ArtifactProcessRequest> artifactProcessRequests);


    ArtifactStatusReport getReport(String uriPath) throws ArtifactStatusReportException;

}
