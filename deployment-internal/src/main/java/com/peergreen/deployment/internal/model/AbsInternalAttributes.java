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

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a common class that allows to defines attributes.
 * @author Florent Benoit
 */
public abstract class AbsInternalAttributes implements InternalAttributes {

    private final Map<String, Object> attributes;

    public AbsInternalAttributes() {
        this.attributes = new HashMap<>();
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttribute(String key) {
        setAttribute(key, null);
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String attributeName) {
        return (T) this.attributes.get(attributeName);
    }


    @Override
    public boolean hasAttributes(String... attributesName) {
        // No attributes
        if (attributesName == null) {
            return false;
        }

        // one attribute
        if (attributesName.length == 1) {
            return this.attributes.containsKey(attributesName[0]);
        }

        // many attributes, loop
        boolean hasAttribute = true;
        for (String attributeName : attributesName) {
            hasAttribute = hasAttribute && hasAttributes(attributeName);
            if (!hasAttribute) {
                return false;
            }
        }
        return hasAttribute;
    }


    @Override
    public boolean unsetAttribute(String attributeName) {
        if (!this.attributes.containsKey(attributeName)) {
            return false;
        }
        this.attributes.remove(attributeName);
        return true;
    }

}
