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


import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.executor.PluginJvmLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WorkerLoop
 *
 * @author ppodsednik
 */
class WorkerLoop {
    private static final Logger LOGGER = Logger.getLogger(WorkerLoop.class.getName());

    private static final long INITIAL_BACKOFF_MS = 2_000;
    private static final long MAX_BACKOFF_MS = 30_000;

    private final ManagerClient managerClient;
    private final WorkerConfiguration workerConfiguration;
    private final Node node;
    private final List<PluginInfo> pluginsList;

    private volatile boolean running = false;
    private volatile Thread pollingThread;

    private long lastIdleLogTime = 0;
    private final Object sleepMonitor = new Object();

    WorkerLoop(
            WorkerConfiguration workerConfiguration,
            ManagerClient managerClient,
            Node node,
            List<PluginInfo> pluginsList) {

        this.managerClient = managerClient;
        this.workerConfiguration = workerConfiguration;
        this.node = node;
        this.pluginsList = new ArrayList<>(pluginsList);
    }

    synchronized void start() {
        if (pollingThread != null) {
            return;
        }
        running = true;
        pollingThread = new Thread(() -> {
            try {
                runLoop();
            } finally {
                LOGGER.info("Worker loop exited.");
            }
        }, "WorkerPollingThread");

        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    private void runLoop() {
        if (!registerWithManager()) {
            return;
        }
        LOGGER.info("Worker successfully registered. Starting polling.");
        long backoffMs = INITIAL_BACKOFF_MS;
        while (running) {
            try {
                Optional<ScheduledProcess> taskOpt = pollManagerForTask();
                // Successful communication - reset backoff
                backoffMs = INITIAL_BACKOFF_MS;
                if (taskOpt.isPresent()) {
                    executeTask(taskOpt.get());
                } else {
                    handleIdle();
                    sleep(workerConfiguration.getWorkerLoopSleepSecs() * 1000);
                }
            } catch (Exception e) {
                if (!running) {
                    break;
                }
                LOGGER.log(Level.SEVERE, "Unexpected error in polling loop", e);
                if (!sleep(backoffMs)) {
                    break;
                }
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            }
        }
    }

    private boolean registerWithManager() {
        long backoffMs = INITIAL_BACKOFF_MS;
        while (running) {
            try {
                registerNode();
                if (!running) {
                    return false;
                }
                if (!registerPlugins()) {
                    return false;
                }
                if (!running) {
                    return false;
                }
                LOGGER.info("Node and plugins successfully registered.");
                return true;
            } catch (Exception e) {
                if (!running) {
                    return false;
                }
                LOGGER.log(Level.SEVERE, "Failed to register worker with manager. Retrying...", e);
                if (!sleep(backoffMs)) {
                    return false;
                }
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            }
        }
        return false;
    }

    private void registerNode() {
        LOGGER.info(String.format("Register node [%s]", node.getNodeId()));
        managerClient.registerNode(node);
    }

    private boolean registerPlugins() {
        for (PluginInfo pluginInfo : pluginsList) {
            if (!running) {
                return false;
            }
            LOGGER.info("Register plugin: " + pluginInfo.getPluginId());
            managerClient.registerPlugin(pluginInfo);
        }
        return running;
    }

    private void executeTask(ScheduledProcess scheduledProcess) {
        int exitCode = -1;
        boolean launchFailed = false;
        try {
            exitCode = PluginJvmLauncher.launchJvm(scheduledProcess, workerConfiguration);
            if (exitCode != 0) {
                launchFailed = true;
                LOGGER.severe("Plugin JVM failed for process "
                        + scheduledProcess.getProcessId()
                        + ", exit code: " + exitCode);
            }
        } catch (Exception e) {
            launchFailed = true;
            LOGGER.log(Level.SEVERE, "Error during task execution: "
                    + scheduledProcess.getProcessId(), e);
        }
        if (launchFailed) {
            try {
                managerClient.updateProcessState(scheduledProcess.getProcessId(), ProcessState.FAILED);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to update process state to FAILED: "
                        + scheduledProcess.getProcessId(), e);
            }
        }
    }

    private void handleIdle() {
        double printSec = workerConfiguration.getWorkerLoopPrintSecs();
        ProcessRegistry.getInstance().markIdle();
        long currentTime = System.currentTimeMillis();
        double elapsedSec = (currentTime - lastIdleLogTime) / 1000.0;
        if (elapsedSec >= printSec) {
            double sleepSec = workerConfiguration.getWorkerLoopSleepSecs();
            LOGGER.info(String.format(
                    "Elapsed: %.2fs. Configured print interval: %.2fs",
                    elapsedSec,
                    printSec
            ));
            LOGGER.info("No process from the manager. Worker is alive, polling every: "
                    + sleepSec + "s...");
            lastIdleLogTime = currentTime;
        }
    }

    private boolean sleep(double millis) {
        synchronized (sleepMonitor) {
            if (!running) {
                return false;
            }
            try {
                sleepMonitor.wait(Math.round(millis));
                return running;
            } catch (InterruptedException e) {
                LOGGER.info("Worker thread interrupted, stopping...");
                Thread.currentThread().interrupt();
                running = false;
                return false;
            }
        }
    }

    public synchronized void stop() {
        running = false;
        synchronized (sleepMonitor) {
            sleepMonitor.notifyAll();
        }
    }

    private Optional<ScheduledProcess> pollManagerForTask() {
        ScheduledProcess processTask = managerClient.getNextScheduledProcess();
        if (processTask != null) {
            return Optional.of(processTask);
        }
        return Optional.empty();
    }
}
