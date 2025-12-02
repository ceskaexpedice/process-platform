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
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.executor.PluginJvmLauncher;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WorkerLoop
 * @author ppodsednik
 */
class WorkerLoop {
    private static final Logger LOGGER = Logger.getLogger(WorkerLoop.class.getName());

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

                        try {
                            int exitCode = PluginJvmLauncher.launchJvm(scheduledProcess, workerConfiguration);
                            if (exitCode != 0) {
                                throw new ApplicationException("Failed to launch JVM, exit code: " + exitCode);
                            }
                        } catch (Exception e) {
                            LOGGER.severe("Error during task execution: " + e.getMessage());
                            // Optionally: retry, skip, or escalate
                        }

                    } else {
                        int sleepSec = workerConfiguration.getWorkerLoopSleepSecs();
                        ProcessRegistry.getInstance().markIdle();
                        LOGGER.info("No process from the manager. Sleeping " + sleepSec + " seconds...");
                        Thread.sleep(sleepSec * 1000L);
                    }

                } catch (InterruptedException e) {
                    LOGGER.info("Worker thread interrupted, stopping...");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected error in polling loop", e);
                    stop();
                }
            }
            LOGGER.info("Worker loop exited.");
        }, "WorkerPollingThread");

        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    public void stop() {
        running = false;
    }

    private Optional<ScheduledProcess> pollManagerForTask() {
        ScheduledProcess processTask = managerClient.getNextScheduledProcess();
        if (processTask != null) {
            return Optional.of(processTask);
        } else {
            return Optional.empty();
        }
    }

}
