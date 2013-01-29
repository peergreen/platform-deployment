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
package com.peergreen.deployment.internal.facet.osgibundle.processor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import com.peergreen.deployment.Processor;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;

/**
 * Start the OSGi bundles on the gateway.
 * @author Florent Benoit
 */
public class OSGiBundleStartProcessor implements Processor<Bundle> {

    private static final Log LOGGER = LogFactory.getLog(OSGiBundleStartProcessor.class);


    @Override
    public void handle(Bundle bundle, ProcessorContext processorContext) throws ProcessorException {
        //LOGGER.info("Bundle found is : " + bundle + " ID '" + bundle.getBundleId());

        // Start the bundle
        try {
            bundle.start();
        } catch (BundleException e) {
            throw new ProcessorException("Unable to start the bundle", e);
        }
    }

}
