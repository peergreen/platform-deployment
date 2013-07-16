/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.deployment.internal.mbean;

import static com.peergreen.deployment.DeploymentMode.DEPLOY;
import static com.peergreen.deployment.DeploymentMode.UNDEPLOY;
import static com.peergreen.deployment.DeploymentMode.UPDATE;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.deployment.Artifact;
import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.mbean.DeploymentMXBean;
import com.peergreen.deployment.report.DeploymentStatusReport;

/**
 * Register MBean
 * @author Florent Benoit
 */
@Component
@Instantiate
public class DeployComponent implements DeploymentMXBean {

    /**
     * Instance of the MBean server.
     */
    private final MBeanServer mBeanServer;

    /**
     * ObjectName to use.
     */
    private final ObjectName objectName;

    /**
     * Deployment Service to use.
     */
    private DeploymentService deploymentService;

    /**
     * Artifact builder.
     */
    private ArtifactBuilder artifactBuilder;

    /**
     * Default constructor.
     * @throws MalformedObjectNameException
     */
    public DeployComponent() throws MalformedObjectNameException {
        this.objectName = new ObjectName("peergreen:type=Deployment");
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Bind
    public void bindDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Bind
    public void bindArtifactBuilder(ArtifactBuilder artifactBuilder) {
        this.artifactBuilder = artifactBuilder;
    }

    /**
     * Start the component.
     */
    @Validate
    public void start() {
        try {
            mBeanServer.registerMBean(this, objectName);
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            throw new IllegalStateException("Unable to register the MBean", e);
        }
    }

    /**
     * Stop the component.
     */
    @Invalidate
    public void stop() {
        try {
            mBeanServer.unregisterMBean(objectName);
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new IllegalStateException("Unable to unregister the MBean", e);
        }
    }


    /**
     * Process the given URI and send a report
     * @param deploymentMode the deployment mode
     * @param uri the URI to deploy
     * @return the report
     */
    @Override
    public DeploymentStatusReport process(String mode, String uri) {
        if ("deploy".equalsIgnoreCase(mode)) {
            return process(DEPLOY, uri);
        } else if ("undeploy".equalsIgnoreCase(mode)) {
            return process(UNDEPLOY, uri);
        } else if ("update".equalsIgnoreCase(mode)) {
            return process(UPDATE, uri);
        }
        throw new IllegalStateException(String.format("Unable to use the given unkown mode %s", mode));
    }


    /**
     * Process the given URI and send a report
     * @param deploymentMode the deployment mode
     * @param uri the URI to deploy
     * @return the report
     */
    public DeploymentStatusReport process(DeploymentMode deploymentMode, String uri) {
        Artifact artifact;
        try {
            artifact = artifactBuilder.build(uri, new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
        artifactProcessRequest.setDeploymentMode(deploymentMode);
        return deploymentService.process(Collections.singleton(artifactProcessRequest));
    }

}
