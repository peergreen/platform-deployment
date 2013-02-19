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

package com.peergreen.deployment.facet;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.peergreen.deployment.facet.builder.FacetBuilder;

/**
 * Defines the builder to use when this facet needs to be applied.
 * @author Florent Benoit
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface FacetBuilderReference {

    /**
     * Identifier of the {@link FacetBuilder} to use when re-loading persisted artifacts.
     */
    Class<?> value();
}
