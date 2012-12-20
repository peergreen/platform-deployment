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
package com.peergreen.deployment.internal.processor.current;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import com.peergreen.deployment.internal.processor.InternalProcessor;

@Component
@Provides
@Instantiate(name="Current Processor")
public class DefaultCurrentProcessor implements CurrentProcessor {


    private final InheritableThreadLocal<InternalProcessor> currentProcessor;

    public DefaultCurrentProcessor() {
        this.currentProcessor = new InheritableThreadLocal<InternalProcessor>();
    }

    @Override
    public InternalProcessor getCurrent() {
        return currentProcessor.get();
    }


    @Override
    public void setCurrent(InternalProcessor internalProcessor) {
        this.currentProcessor.set(internalProcessor);
    }


}
