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

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.report.ArtifactError;
import com.peergreen.deployment.report.ArtifactErrorDetail;

/**
 * Defines
 *
 * @author Florent Benoit
 */
public class DefaultArtifactError implements ArtifactError {

    private final List<ArtifactErrorDetail> details;

    public DefaultArtifactError(Throwable throwable) {
        this.details = new ArrayList<>();

        DefaultArtifactErrorDetail errorDetail = new DefaultArtifactErrorDetail(throwable.getMessage(), throwable.getStackTrace());
        details.add(errorDetail);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            details.add(new DefaultArtifactErrorDetail(cause.getMessage(),cause.getStackTrace()));
            cause = cause.getCause();
        }
    }

    @Override
    public List<ArtifactErrorDetail> getDetails() {
        return details;
    }

}
