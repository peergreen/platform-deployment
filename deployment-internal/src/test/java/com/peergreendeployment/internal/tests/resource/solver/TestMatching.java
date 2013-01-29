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
package com.peergreendeployment.internal.tests.resource.solver;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.resolver.Logger;
import org.apache.felix.resolver.ResolverImpl;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;
import org.testng.Assert;

import com.peergreen.deployment.internal.solver.ResolveContextImpl;

public abstract class TestMatching {

    private final Resolver resolver;

    public TestMatching() {
        this.resolver = new ResolverImpl(new Logger(Logger.LOG_DEBUG));
    }


    public  Capability match(Resource provider, Resource consumer) throws ResolutionException, URISyntaxException {

        // resources
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(provider);
        resources.add(consumer);

        // want to resolve the consumer
        List<Resource> mandatory = new ArrayList<Resource>();
        mandatory.add(consumer);


        // Create wirings
        Map<Resource, Wiring> wirings = new HashMap<Resource, Wiring>();

        // No optional resources
        List<Resource> optional = Collections.emptyList();


        ResolveContext resolveContext = new ResolveContextImpl(resources, wirings, mandatory, optional);

        Map<Resource, List<Wire>> wireMap = resolver.resolve(resolveContext);

        // wire ok
        Assert.assertNotNull(wireMap);

        // TWO resources
        Assert.assertEquals(wireMap.size(), 2);

        // consumer ?
        List<Wire> wires = wireMap.get(consumer);
        Assert.assertNotNull(wires);
        Assert.assertEquals(wires.size(), 1);

        // returns the capability of the requirement
        return wires.get(0).getCapability();

    }
}
