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
package org.ceskaexpedice.processplatform.manager;

package org.ceskaexpedice.processplatform.manager.polling;

import org.ceskaexpedice.processplatform.manager.db.TaskRepository;
import org.ceskaexpedice.processplatform.manager.service.TaskQueueService;
import org.ceskaexpedice.processplatform.model.Task;

import java.util.List;

public class TaskPollingLoop implements Runnable {

    private final TaskRepository repository;
    private final TaskQueueService queueService;
    private volatile boolean running = true;

    public TaskPollingLoop(TaskRepository repository, TaskQueueService queueService) {
        this.repository = repository;
        this.queueService = queueService;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<Task> newTasks = repository.findPendingTasks();
                for (Task task : newTasks) {
                    repository.markAsQueued(task.getId()); // optional state change
                    queueService.addTask(task);
                }
                Thread.sleep(5000); // configurable polling interval
            } catch (Exception e) {
                e.printStackTrace(); // or use logger
            }
        }
    }
}
