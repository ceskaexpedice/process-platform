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

import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/process-tasks")
public class ProcessTaskResource {

    //@Inject
//    private TaskQueue taskQueue;

    @GET
    @Path("next")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response nextTask(String[] supportedTasks) {
        return Response.ok().entity("result Owners".toString()).build();
    }

    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@PathParam("id") String taskId, String status) {
        return Response.ok().entity("result Owners".toString()).build();
    }

}
