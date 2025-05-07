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
package org.ceskaexpedice.processplatform.worker.tasks;

import com.google.inject.Inject;
import org.ceskaexpedice.processplatform.worker.runprocess.ProcessStarterLauncher;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TasksLoader {

    private final ManagerClient managerClient;
    private final ProcessStarterLauncher launcher;

    @Inject
    public TasksLoader(ManagerClient managerClient, ProcessStarterLauncher launcher) {
        this.managerClient = managerClient;
        this.launcher = launcher;
    }

    public void start() {
        Timer timer = new Timer(true); // daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pollManagerForTasks();
            }
        }, 0, 10_000); // every 10 seconds
    }

    private void pollManagerForTasks() {
        try {
            List<Task> tasks = managerClient.fetchTasks();
            for (Task task : tasks) {
                launcher.launchProcess(task.getType(), task.getPayload());
            }
        } catch (Exception e) {
            System.err.println("Polling error: " + e.getMessage());
        }
    }
}
