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
package com.peergreen.deployment.internal.thread;

import java.util.concurrent.ThreadFactory;

/**
 * Defines a Peergreen Thread factory
 * @author Florent Benoit
 */
public class PeergreenThreadFactory implements ThreadFactory {

    private final ThreadFactory wrappedThreadFactory;

    private final String name;

    public PeergreenThreadFactory(ThreadFactory wrappedThreadFactory, String name) {
        this.wrappedThreadFactory = wrappedThreadFactory;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = wrappedThreadFactory.newThread(r);
        thread.setName("Peergreen ".concat(name).concat(" / ").concat(thread.getName()));
        return thread;
    }
}
