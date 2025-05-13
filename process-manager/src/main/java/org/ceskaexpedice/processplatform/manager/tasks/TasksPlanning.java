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
package org.ceskaexpedice.processplatform.manager.tasks;


import org.ceskaexpedice.processplatform.common.Task;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

public class TasksPlanning {

    private final TaskQueue taskQueue;
    private final TaskRepository taskRepository;

    @Inject
    public TasksPlanning(TaskQueue taskQueue, TaskRepository taskRepository) {
        this.taskQueue = taskQueue;
        this.taskRepository = taskRepository;
    }

    public void start() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                List<Task> tasks = taskRepository.findPendingTasks();
                for (Task task : tasks) {
                    taskQueue.add(task);
                }
            }
        }, 0, 10_000);
    }
}
