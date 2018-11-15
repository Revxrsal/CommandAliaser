/*
 * * Copyright 2018 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reflxction.commandaliaser.commons;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A utility that uses Java's concurrency for multithreading
 */
public class Multithreading {

    // The executor service, which uses a thread pool of 100 threads
    public static final ExecutorService SERVICE = Executors.newFixedThreadPool(100, Thread::new);

    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    /**
     * Runs a task asynchronously on the thread pool
     *
     * @param task Task to run
     */
    public static void runAsync(Runnable task) {
        Multithreading.SERVICE.execute(task);
    }

}