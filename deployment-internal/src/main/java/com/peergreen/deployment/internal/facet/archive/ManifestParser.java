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
package com.peergreen.deployment.internal.facet.archive;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestParser {

    public static Map<String, String> readManifest(final Manifest manifest) {
        Map<String, String> manifestEntries = new HashMap<String, String>();
        // Now manifest is here, analyze it and populate metadata
        Attributes attributes = manifest.getMainAttributes();
        Set<Map.Entry<Object, Object>> entries = attributes.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            manifestEntries.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return manifestEntries;
    }
}
