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
package com.peergreen.deployment.internal.processor.uriresolver;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.peergreen.deployment.internal.processor.uriresolver.CheckZip;

public class TestCheckZip {

    @Test
    public void testCheckMissingBytes() {
        byte[] buffer = new byte[] {5, 10 };
        Assert.assertFalse(CheckZip.checkBuffer(buffer));
    }

    @Test
    public void testCheckWrongBytes() {
        byte[] buffer = new byte[] {5, 10, 15, 20 };
        Assert.assertFalse(CheckZip.checkBuffer(buffer));
    }

    @Test
    public void testCheckGoodSignature() {
        byte[] buffer =  new byte[] { 'P', 'K', 0x3, 0x4 };
        Assert.assertTrue(CheckZip.checkBuffer(buffer));
    }

}
