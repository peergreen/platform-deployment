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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This mainly reproduce the {@link ThreadFactory} returned by {@link java.util.concurrent.Executors#defaultThreadFactory()},
 * only allowing to specify the group to use.
 */
public class GroupingThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public GroupingThreadFactory() {
        this(defaultThreadGroup());
    }

    private static ThreadGroup defaultThreadGroup() {
        SecurityManager s = System.getSecurityManager();
        return (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    public GroupingThreadFactory(ThreadGroup threadGroup) {
        group = threadGroup;
        namePrefix = "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(
                group,
                r,
                namePrefix + threadNumber.getAndIncrement(),
                0
        );

        // DefaultThreadGroup has this behavior: reset some properties that have been set during the Thread initialization.
        // Keep it here for compatibility purpose.
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
