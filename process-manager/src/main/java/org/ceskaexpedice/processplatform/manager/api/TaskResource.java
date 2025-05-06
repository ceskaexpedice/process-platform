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
package org.ceskaexpedice.processplatform.manager.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ceskaexpedice.processplatform.manager.service.TaskQueueService;
import org.ceskaexpedice.processplatform.model.Task;

import java.util.Optional;

@Path("/api/tasks")
public class TaskResource {

    @Inject
    TaskQueueService queueService;

    @GET
    @Path("/next")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNextTask() {
        Optional<Task> task = queueService.getNextTask();
        return task.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NO_CONTENT))
                .build();
    }
}
