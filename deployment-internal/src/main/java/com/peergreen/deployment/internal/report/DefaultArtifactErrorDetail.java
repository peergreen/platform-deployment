/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.report;

import com.peergreen.deployment.report.ArtifactErrorDetail;

public class DefaultArtifactErrorDetail implements ArtifactErrorDetail {

    private final String message;

    private final StackTraceElement[] stackTrace;

    public DefaultArtifactErrorDetail(String message, StackTraceElement[] strackTrace) {
        this.message = message;
        this.stackTrace = strackTrace.clone();
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTrace.clone();
    }
}
