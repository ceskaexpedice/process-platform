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

package org.ceskaexpedice.processplatform.manager.client;


import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * WorkerClientFactory
 * @author petrp
 */
public final class WorkerClientFactory {

    private static final Logger LOGGER = Logger.getLogger(WorkerClientFactory.class.getName());

    private static final AtomicReference<WorkerClient> INSTANCE = new AtomicReference<>();

    private WorkerClientFactory() {
    }

    public static WorkerClient createWorkerClient(ProcessService processService, NodeService nodeService) {
        return INSTANCE.updateAndGet(existingInstance -> {
            if (existingInstance == null) {
                LOGGER.info("Creating new WorkerClient");
                WorkerClient workerClient = new WorkerClient(processService, nodeService);
                return workerClient;
            }
            return existingInstance;
        });
    }

    public static WorkerClient foundCreated() {
        return INSTANCE.get();
    }
}
