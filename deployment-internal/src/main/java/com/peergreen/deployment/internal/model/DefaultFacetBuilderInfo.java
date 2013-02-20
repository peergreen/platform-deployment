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
package com.peergreen.deployment.internal.model;

import com.peergreen.deployment.internal.artifact.InternalFacetBuilderInfo;

/**
 * Defines a basic facet builder data.
 * @author Florent Benoit
 */
public class DefaultFacetBuilderInfo implements InternalFacetBuilderInfo {

    private String name;

    private String provides;

    private Throwable throwable;

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setProvides(String provides) {
        this.provides = provides;
    }

    @Override
    public String getProvides() {
        return provides;
    }



    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (name == null ? 0 : name.hashCode());
        return hash * 31 + (provides == null ? 0 : provides.hashCode());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!object.getClass().equals(this.getClass())) {
            return false;
        }

        DefaultFacetBuilderInfo other = (DefaultFacetBuilderInfo) object;

        if (name == null && other.name != null) {
            return false;
        }
        if (provides == null && other.provides != null) {
            return false;
        }

        return name.equals(other.name) && provides.equals(other.provides);

    }

}
