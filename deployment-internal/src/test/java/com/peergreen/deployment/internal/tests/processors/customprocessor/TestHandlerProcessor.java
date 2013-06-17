/**
 * Copyright 2013 Peergreen S.A.S.
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
package com.peergreen.deployment.internal.tests.processors.customprocessor;

import com.peergreen.deployment.internal.handler.DelegateHandlerProcessor;
import com.peergreen.deployment.Processor;
import com.peergreen.deployment.resource.builder.RequirementBuilder;

public class TestHandlerProcessor<T> extends DelegateHandlerProcessor<T>{

        public TestHandlerProcessor(Processor<T> processor, Class<T> expectedHandleType) {
        super(processor, expectedHandleType);
    }

        @Override
        public void bindRequirementBuilder(RequirementBuilder requirementBuilder) {
            super.bindRequirementBuilder(requirementBuilder);
        }
}
