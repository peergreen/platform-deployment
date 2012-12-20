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
package com.peergreen.deployment.internal.facet.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.peergreen.deployment.facet.content.Content;
import com.peergreen.deployment.facet.content.ContentException;

public class FileContentImpl implements Content {

    private final File file;

    public FileContentImpl(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws ContentException {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ContentException("Unable to get the inputstream", e);
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }


}
