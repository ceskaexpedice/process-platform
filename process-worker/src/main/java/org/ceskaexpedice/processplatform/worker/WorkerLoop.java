/*
 * Copyright (C) 2025 Inovatika
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ceskaexpedice.processplatform.worker;


import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.executor.PluginJvmLauncher;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * WorkerLoop
 * @author ppodsednik
 */
class WorkerLoop {
    public static final Logger LOGGER = Logger.getLogger(WorkerLoop.class.getName());

    private final ManagerClient managerClient;
    private final WorkerConfiguration workerConfiguration;
    private volatile boolean running = true;

    WorkerLoop(WorkerConfiguration workerConfiguration, ManagerClient managerClient) {
        this.managerClient = managerClient;
        this.workerConfiguration = workerConfiguration;
    }

    void start() {
        Thread pollingThread = new Thread(() -> {
            while (running) {
                try {
                    Optional<ScheduledProcess> taskOpt = pollManagerForTask();
                    if (taskOpt.isPresent()) {
                        ScheduledProcess scheduledProcess = taskOpt.get();
                        int exitCode = PluginJvmLauncher.launchJvm(scheduledProcess, workerConfiguration);
                        if (exitCode != 0) {
                            throw new ApplicationException("Failed to launch JVM");
                        }
                    } else {
                        int sleepSec = Integer.parseInt(workerConfiguration.get(WorkerConfiguration.WORKER_LOOP_SLEEP_SEC_KEY));
                        LOGGER.info("No process from the manager. Sleeping " + sleepSec + " seconds...");
                        Thread.sleep(sleepSec * 1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            LOGGER.info("Worker loop exited.");
        }, "WorkerPollingThread");
        pollingThread.setDaemon(true); // optional
        pollingThread.start();
    }

    public void stop() {
        running = false;
    }

    private Optional<ScheduledProcess> pollManagerForTask() {
        ScheduledProcess processTask = managerClient.getNextProcess();
        if (processTask != null) {
            return Optional.of(processTask);
        } else {
            return Optional.empty();
        }
    }

}
