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

@Singleton
public class WorkerLoop {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final TaskFetcher fetcher;
    private final TaskExecutor executorService;

    @Inject
    public WorkerLoop(TaskFetcher fetcher, TaskExecutor executorService) {
        this.fetcher = fetcher;
        this.executorService = executorService;
    }

    public void start() {
        executor.scheduleAtFixedRate(this::pollManager, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    private void pollManager() {
        Optional<TaskDto> task = fetcher.fetchTask();
        task.ifPresent(executorService::executeTask);
    }
}
