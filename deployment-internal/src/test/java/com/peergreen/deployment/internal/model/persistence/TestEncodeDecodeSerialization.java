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

import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.decodeValue;
import static com.peergreen.deployment.internal.model.persistence.PersistenceModelConstants.encodeValue;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test encode/decode of primitive types.
 * @author Florent Benoit
 */
public class TestEncodeDecodeSerialization {

    @Test
    public void testInteger() {
        check(Integer.valueOf(100));
    }

    @Test
    public void testLong() {
        check(Long.valueOf(100L));
    }

    @Test
    public void testFloat() {
        check(Float.valueOf(10.25f));
    }

    @Test
    public void testDouble() {
        check(Double.valueOf(10.25d));
    }

    @Test
    public void testString() {
        check("florent");
    }

    @Test
    public void testShort() {
        check(Short.valueOf("5"));
    }

    @Test
    public void testByte() {
        check(Byte.valueOf("5"));
    }

    @Test
    public void testChar() {
        check(Character.valueOf('f'));
    }


    @Test
    public void testBoolean() {
        check(Boolean.valueOf(true));
        check(Boolean.valueOf(false));
    }


    <T> void check(T object) {
        Assert.assertEquals(decodeValue(encodeValue(object)), object);
    }



}
