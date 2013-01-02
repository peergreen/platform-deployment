package com.peergreen.deployment.launcher;

import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackage;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.spi.PaxExamRuntime;

public class LaunchClient {

    public static void main(String[] args) throws Exception {
//

     //  Thread.sleep(15000L);

        // The usual configuration
        String gogoVersion = "0.10.0";
        Option[] options = options(
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo").version("1.8.4"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.arch.gogo").version("1.0.1"),
                mavenBundle().groupId("org.ow2.bundles").artifactId("ow2-util-log").version("1.0.34"),
                mavenBundle().groupId("org.ow2.bundles").artifactId("ow2-util-i18n").version("1.0.34"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.resolver"),
                mavenBundle().groupId("com.peergreen.prototype.deployment").artifactId("deployment-api"),
                mavenBundle().groupId("com.peergreen.prototype.deployment").artifactId("deployment-internal"),
                mavenBundle().groupId("com.peergreen.prototype.deployment").artifactId("deployment-extension-deploymentplanjonas"),
                mavenBundle().groupId("com.peergreen.prototype.deployment").artifactId("deployment-client"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.gogo.runtime").version(gogoVersion),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.gogo.shell").version(gogoVersion),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.gogo.command").version(gogoVersion),

                bootDelegationPackage("org.w3c.dom.traversal"),
                systemPackage("org.w3c.dom.traversal"),
                systemPackage("javax.xml.bind;version=5"),
                systemPackage("javax.transaction;version=1.1.0"),
                systemPackage("javax.transaction.xa;version=1.1.0")

        );

        // create a proper ExamSystem with your options. Focus on
        // "createServerSystem"
        ExamSystem system = PaxExamRuntime.createTestSystem(options);

        // create Container (you should have exactly one configured!) and start.
        TestContainer container = PaxExamRuntime.createContainer(system);
        container.start();

    }
}
