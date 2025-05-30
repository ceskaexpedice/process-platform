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


import org.ceskaexpedice.processplatform.common.dto.ProcessTask;
import org.ceskaexpedice.processplatform.common.dto.ScheduledProcessDto;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginJvmLauncher;

import java.util.Optional;

class WorkerLoop {

    private final ManagerClient_toDelete managerClient;
    private volatile boolean running = true;

    WorkerLoop(WorkerConfiguration workerConfiguration) {
        this.managerClient = new ManagerClient(workerConfiguration);
    }

    void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received. Stopping worker...");
            stop();
        }));

        Thread pollingThread = new Thread(() -> {
            while (running) {
                try {
                    Optional<ProcessTask> taskOpt = pollManagerForTask();

                    if (taskOpt.isPresent()) {
                        ProcessTask processTask = taskOpt.get();
                        PluginJvmLauncher.launchJvm(processTask);
                       // int exitCode = process.waitFor();
                       // reportProcessResult(processTask, exitCode);
                    } else {
                        System.out.println("No task available. Sleeping...");
                        Thread.sleep(10_000); // sleep before polling again
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Worker error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Worker loop exited.");
        }, "WorkerPollingThread");

        pollingThread.setDaemon(true); // optional
        pollingThread.start();
    }

    public void stop() {
        running = false;
    }

    private Optional<ProcessTask> pollManagerForTask() {
        try {
            // ProcessTask processTask = managerClient.nextProcessTask();
            ScheduledProcessDto processTask = managerClient.getNextProcess();

            if(processTask != null){
                return Optional.of(processTask);
            }else{
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Polling error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
