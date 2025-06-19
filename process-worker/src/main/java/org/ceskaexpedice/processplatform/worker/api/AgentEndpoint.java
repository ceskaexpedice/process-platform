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
package org.ceskaexpedice.processplatform.worker.api;

import org.ceskaexpedice.processplatform.worker.api.service.AgentService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ManagerEndpoint
 * @author ppodsednik
 */
@Path("/agent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgentEndpoint {

    public AgentEndpoint(AgentService agentService) {
    }

    // === Get Process Logs (stdout or stderr) ===
    @GET
    @Path("/logs/{uuid}")
    public Response getLogs(
            @PathParam("uuid") String uuid,
            @QueryParam("stream") @DefaultValue("stdout") String streamType) {
        // streamType can be "stdout" or "stderr"
        // You can return logs as plain text or application/octet-stream if binary
        return Response.ok(/* logs as text or file */).entity("{\"uuid\":\"" + uuid + "\" }").build();
    }

    // === Check Process Status (alive, exit code) ===
    @GET
    @Path("/status/{uuid}")
    public Response getStatus(@PathParam("uuid") String uuid) {
        // Return current status of the process (RUNNING, COMPLETED, EXIT_CODE, etc.)
        return Response.ok(/* ProcessStatus */).build();
    }

    // === Kill Process ===
    @POST
    @Path("/kill-task")
    public Response killTask() {
        /*
        if (currentProcess != null) {
            currentProcess.destroyForcibly();
            return Response.ok("Killed").build();
        }*/
        return Response.status(404).entity("No running task").build();
    }

    @POST
    @Path("/kill")
    public Response killPlugin() {
        //service.killRunningProcess();
        return Response.ok().build();
    }
}
