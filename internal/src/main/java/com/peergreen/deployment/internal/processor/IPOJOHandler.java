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

/**
 * FIXME : only a reminder for adding a ipojo handler for building DelegateInternalProcessor based on simple processors
 * @author Florent Benoit
 */
public class IPOJOHandler {

    /* example to get the generic type of the processor
    Type[] types = processor.getClass().getGenericInterfaces();
    for (Type type : types) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ((ParameterizedType) type);
            Type rawType = parameterizedType.getRawType();
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (InternalProcessor.class.equals(rawType)) {
                Class<?> toCast =  (Class<?>) typeArguments[0];
                System.out.println("cast to " + toCast);
                }

            }

        }
    }
    */

}
