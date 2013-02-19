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
package com.peergreen.deployment.internal.model.persistence;

/**
 * Define some constants used by READER and WRITER
 * @author Florent Benoit
 */
public class PersistenceModelConstants {

    /**
     * Attribute Name.
     */
    public static final String ATTRIBUTE_KEY = "attribute-";

    /**
     * Value prefix for the type.
     */
    public static final String VALUETYPE_PREFIX = "{";

    /**
     * Value suffix for the type.
     */
    public static final String VALUETYPE_SUFFIX = "}";

    /**
     * Value type for Long.
     */
    public static final String LONG_VALUETYPE = VALUETYPE_PREFIX.concat(Long.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Byte.
     */
    public static final String BYTE_VALUETYPE = VALUETYPE_PREFIX.concat(Byte.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Float.
     */
    public static final String FLOAT_VALUETYPE = VALUETYPE_PREFIX.concat(Float.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Boolean.
     */
    public static final String BOOLEAN_VALUETYPE = VALUETYPE_PREFIX.concat(Boolean.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Double.
     */
    public static final String DOUBLE_VALUETYPE = VALUETYPE_PREFIX.concat(Double.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Integer.
     */
    public static final String INTEGER_VALUETYPE = VALUETYPE_PREFIX.concat(Integer.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Character.
     */
    public static final String CHARACTER_VALUETYPE = VALUETYPE_PREFIX.concat(Character.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    /**
     * Value type for Short.
     */
    public static final String SHORT_VALUETYPE = VALUETYPE_PREFIX.concat(Short.class.getSimpleName()).concat(VALUETYPE_SUFFIX);

    public static <T> String encodeValue(T object) {
        Class<T> objectClass = (Class<T>) object.getClass();

        if (Long.class.equals(objectClass)) {
            return LONG_VALUETYPE.concat(String.valueOf(object));
        }
        if (Byte.class.equals(objectClass)) {
            return BYTE_VALUETYPE.concat(String.valueOf(object));
        }
        if (Float.class.equals(objectClass)) {
            return FLOAT_VALUETYPE.concat(String.valueOf(object));
        }
        if (Boolean.class.equals(objectClass)) {
            return BOOLEAN_VALUETYPE.concat(String.valueOf(object));
        }
        if (Double.class.equals(objectClass)) {
            return DOUBLE_VALUETYPE.concat(String.valueOf(object));
        }
        if (Integer.class.equals(objectClass)) {
            return INTEGER_VALUETYPE.concat(String.valueOf(object));
        }
        if (Character.class.equals(objectClass)) {
            return CHARACTER_VALUETYPE.concat(String.valueOf(object));
        }
        if (Short.class.equals(objectClass)) {
            return SHORT_VALUETYPE.concat(String.valueOf(object));
        }
        // Do not encode
        if (String.class.equals(objectClass)) {
            return String.valueOf(object);
        }
        throw new IllegalStateException(String.format("Unssuported serialization format for value %s", object));
    }

    public static <T> T decodeValue(String encodedValue) {
        if (encodedValue.startsWith(LONG_VALUETYPE)) {
            return (T) Long.valueOf(encodedValue.substring(LONG_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(BYTE_VALUETYPE)) {
            return (T) Byte.valueOf(encodedValue.substring(BYTE_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(FLOAT_VALUETYPE)) {
            return (T) Float.valueOf(encodedValue.substring(FLOAT_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(BOOLEAN_VALUETYPE)) {
            return (T) Boolean.valueOf(encodedValue.substring(BOOLEAN_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(DOUBLE_VALUETYPE)) {
            return (T) Double.valueOf(encodedValue.substring(DOUBLE_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(INTEGER_VALUETYPE)) {
            return (T) Integer.valueOf(encodedValue.substring(INTEGER_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(SHORT_VALUETYPE)) {
            return (T) Short.valueOf(encodedValue.substring(SHORT_VALUETYPE.length()));
        }
        if (encodedValue.startsWith(CHARACTER_VALUETYPE)) {
            return (T) Character.valueOf(encodedValue.substring(CHARACTER_VALUETYPE.length()).charAt(0));
        }
        // Do not decode for other type
        return (T) encodedValue;
    }

    /**
     * Utility class.
     */
    private PersistenceModelConstants() {

    }
}
