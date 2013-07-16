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
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.deployment.ArtifactBuilder;
import com.peergreen.deployment.ArtifactProcessRequest;
import com.peergreen.deployment.DeploymentService;
import com.peergreen.deployment.ProcessorInfo;
import com.peergreen.deployment.internal.artifact.DefaultFacetInfo;
import com.peergreen.deployment.internal.artifact.IFacetArtifact;
import com.peergreen.deployment.internal.artifact.ImmutableArtifactBuilder;
import com.peergreen.deployment.internal.processor.DefaultProcessorInfo;
import com.peergreen.deployment.internal.report.DefaultArtifactError;
import com.peergreen.deployment.internal.report.DefaultArtifactStatusReport;
import com.peergreen.deployment.internal.report.DefaultDeploymentStatusReport;
import com.peergreen.deployment.report.ArtifactError;
import com.peergreen.deployment.report.DeploymentStatusReport;

/**
 * Test the MBeans
 * @author Florent Benoit
 */
public class TestMBeans {

    @Mock
    private DeploymentService deploymentService;

    @Mock
    private IFacetArtifact facetArtifact;

    private ArtifactBuilder artifactBuilder;

    private DeployComponent deployComponent;

    private MBeanServer mBeanServer;

    private ObjectName objectName;


    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.objectName = new ObjectName("peergreen:type=Deployment");

        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();

        this.artifactBuilder = new ImmutableArtifactBuilder();

        this.deployComponent = new DeployComponent();
        this.deployComponent.bindArtifactBuilder(artifactBuilder);
        this.deployComponent.bindDeploymentService(deploymentService);

        this.deployComponent.start();
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        this.deployComponent.stop();
    }


    @Test
    public void testRegistered() throws Exception {
        assertTrue(mBeanServer.isRegistered(objectName));
    }


    @Test
    public void testReport() throws Exception {

        DefaultDeploymentStatusReport deploymentStatusReport = new DefaultDeploymentStatusReport();
        DefaultArtifactStatusReport artifactStatusReport = new DefaultArtifactStatusReport(facetArtifact);
        deploymentStatusReport.addChild(artifactStatusReport);


        Exception e1 = new Exception("problem1");
        ArtifactError artifactError1 = new DefaultArtifactError(e1);
        artifactStatusReport.getExceptions().add(artifactError1);

        Exception subException = new Exception("Root cause");
        Exception e2 = new Exception("problem2", subException);
        ArtifactError artifactError2 = new DefaultArtifactError(e2);
        artifactStatusReport.getExceptions().add(artifactError2);

        ProcessorInfo myProcessorInfo = new DefaultProcessorInfo("phase1", "name1", 2503L);
        artifactStatusReport.getProcessors().add(myProcessorInfo);

        DefaultFacetInfo myFacetInfo1 = new DefaultFacetInfo();
        myFacetInfo1.setName("facetName1");
        myFacetInfo1.setProcessor("processor1");
        artifactStatusReport.getFacets().add(myFacetInfo1);

        DefaultFacetInfo myFacetInfo2 = new DefaultFacetInfo();
        myFacetInfo2.setName("facetName2");
        myFacetInfo2.setProcessor("processor2");
        artifactStatusReport.getFacets().add(myFacetInfo2);

        deploymentStatusReport.setFailure();
        doReturn(deploymentStatusReport).when(deploymentService).process(anyCollection());
        Object result = mBeanServer.invoke(objectName, "process", new Object[] {"DEPLOY",  "test://my-uri"}, new String[] {String.class.getName(), String.class.getName()});

       CompositeData compositeData = (CompositeData) result;
       assertNotNull(compositeData);
       CompositeData[] artifactStatusReports = (CompositeData[]) compositeData.get("artifactStatusReports");

       assertNotNull(artifactStatusReports);

       // 1 item
       assertEquals(artifactStatusReports.length, 1);

       CompositeData artifactReport = artifactStatusReports[0];

       // Check failed is true
       boolean failed = (boolean) compositeData.get("failed");
       assertTrue(failed);

       // Check exceptions
       CompositeData[] exceptionsCompositeData = (CompositeData[]) artifactReport.get("exceptions");
       assertEquals(exceptionsCompositeData.length, 2);

       // check exception 1
       CompositeData exceptionsCompositeData1 = exceptionsCompositeData[0];
       CompositeData[] errorsDetailsCompositeData = (CompositeData[]) exceptionsCompositeData1.get("details");
       // Only one level (caused by)
       assertEquals(errorsDetailsCompositeData.length, 1);
       CompositeData errorDetailsCompositeData1 = errorsDetailsCompositeData[0];

       String message1 = (String) errorDetailsCompositeData1.get("message");
       assertEquals(message1, "problem1");

       // check that we contains our method
       String stackTrace1 = getStackTrace(errorDetailsCompositeData1);
       assertTrue(stackTrace1.contains("com.peergreen.deployment.internal.mbean.TestMBeans.testReport"));


       // check exception 2
       CompositeData exceptionsCompositeData2 = exceptionsCompositeData[1];
       CompositeData[] errorsDetailsCompositeData2 = (CompositeData[]) exceptionsCompositeData2.get("details");
       // Two levels (caused by)
       assertEquals(errorsDetailsCompositeData2.length,2);
       CompositeData errorDetailsCompositeData2a = errorsDetailsCompositeData2[0];
       CompositeData errorDetailsCompositeData2b = errorsDetailsCompositeData2[1];

       // check detail error 2a
       String message2a = (String) errorDetailsCompositeData2a.get("message");
       assertEquals(message2a, "problem2");
       String stackTrace2a = getStackTrace(errorDetailsCompositeData2a);
       assertTrue(stackTrace2a.contains("com.peergreen.deployment.internal.mbean.TestMBeans.testReport"));

       // check detail error 2b
       String message2b = (String) errorDetailsCompositeData2b.get("message");
       assertEquals(message2b, "Root cause");
       String stackTrace2b = getStackTrace(errorDetailsCompositeData2a);
       assertTrue(stackTrace2b.contains("com.peergreen.deployment.internal.mbean.TestMBeans.testReport"));


       // Check facets
       CompositeData[] facetsCompositeData = (CompositeData[]) artifactReport.get("facets");
       assertEquals(facetsCompositeData.length, 2);
       CompositeData facetCompositeData1 = facetsCompositeData[0];
       String facetName = (String) facetCompositeData1.get("name");
       String processorName = (String) facetCompositeData1.get("processor");
       assertEquals(facetName, "facetName1");
       assertEquals(processorName, "processor1");

       CompositeData facetCompositeData2 = facetsCompositeData[1];
       String facetName2 = (String) facetCompositeData2.get("name");
       String processorName2 = (String) facetCompositeData2.get("processor");
       assertEquals(facetName2, "facetName2");
       assertEquals(processorName2, "processor2");


       // Check procesors
       CompositeData[]  processorsCompositeData = (CompositeData[]) artifactReport.get("processors");
       assertEquals(processorsCompositeData.length, 1);
       CompositeData processorCompositeData = processorsCompositeData[0];
       String phase = (String) processorCompositeData.get("phase");
       String name = (String) processorCompositeData.get("name");
       long time = (long) processorCompositeData.get("time");
       assertEquals(phase, "phase1");
       assertEquals(name, "name1");
       assertEquals(time, 2503L);

    }

    protected String getStackTrace(CompositeData errorDetailsCompositeData) {
        CompositeData[] stackTraceCompositeData = (CompositeData[]) errorDetailsCompositeData.get("stackTrace");
        String stackTrace = "";
        for (CompositeData stack : stackTraceCompositeData) {
            String className = (String) stack.get("className");
            int lineNumber = (Integer) stack.get("lineNumber");
            String methodName = (String) stack.get("methodName");
            String fileName = (String) stack.get("fileName");
            stackTrace = stackTrace.concat(className).concat(".").concat(methodName).concat("(").concat(fileName).concat(":").concat(String.valueOf(lineNumber)).concat(")").concat(System.lineSeparator());
        }
        return stackTrace;

    }



    @Test
    public void testMBeanDeploy() throws InstanceNotFoundException, ReflectionException, MBeanException, URISyntaxException  {
        ArtifactProcessAnswer answer = new ArtifactProcessAnswer();
        String uritest = "test://my-uri";
        when(deploymentService.process(anyCollection())).thenAnswer(answer);
        mBeanServer.invoke(objectName, "process", new Object[] {"DEPLOY", uritest}, new String[] {String.class.getName(), String.class.getName()});
        ArtifactProcessRequest artifactProcessRequest = answer.getArtifactProcessRequest();
        assertEquals(artifactProcessRequest.getDeploymentMode(), DEPLOY);
        assertEquals(artifactProcessRequest.getArtifact().uri(), new URI(uritest));
    }

    @Test
    public void testMBeanUndeploy() throws InstanceNotFoundException, ReflectionException, MBeanException, URISyntaxException  {
        ArtifactProcessAnswer answer = new ArtifactProcessAnswer();
        String uritest = "test://my-uri";
        when(deploymentService.process(anyCollection())).thenAnswer(answer);
        mBeanServer.invoke(objectName, "process", new Object[] {"UNDEPLOY", uritest}, new String[] {String.class.getName(), String.class.getName()});
        ArtifactProcessRequest artifactProcessRequest = answer.getArtifactProcessRequest();
        assertEquals(artifactProcessRequest.getDeploymentMode(), UNDEPLOY);
        assertEquals(artifactProcessRequest.getArtifact().uri(), new URI(uritest));
    }

    @Test
    public void testMBeanUpdate() throws InstanceNotFoundException, ReflectionException, MBeanException, URISyntaxException  {
        ArtifactProcessAnswer answer = new ArtifactProcessAnswer();
        String uritest = "test://my-uri";
        when(deploymentService.process(anyCollection())).thenAnswer(answer);
        mBeanServer.invoke(objectName, "process", new Object[] {"UPDATE", uritest}, new String[] {String.class.getName(), String.class.getName()});
        ArtifactProcessRequest artifactProcessRequest = answer.getArtifactProcessRequest();
        assertEquals(artifactProcessRequest.getDeploymentMode(), UPDATE);
        assertEquals(artifactProcessRequest.getArtifact().uri(), new URI(uritest));
    }



}


class ArtifactProcessAnswer implements Answer<DeploymentStatusReport> {

    private ArtifactProcessRequest artifactProcessRequest;

    @SuppressWarnings("rawtypes")
    @Override
    public DeploymentStatusReport answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Collection collection = (Collection) args[0];
        artifactProcessRequest = (ArtifactProcessRequest) collection.iterator().next();
        return new DefaultDeploymentStatusReport();
    }

    public ArtifactProcessRequest getArtifactProcessRequest() {
        return artifactProcessRequest;
    }


}
