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
package com.peergreen.deployment.internal.processor;

import com.peergreen.deployment.ProcessorInfo;

public class DefaultProcessorInfo implements ProcessorInfo {

    private final String phase;
    private final String name;
    private final long time;


    public DefaultProcessorInfo(String phase, String name, long time) {
        this.phase = phase;
        this.name = name;
        this.time = time;
    }



    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public long getTime() {
        return time;
    }

}
