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
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessAdminEndpoint {

    // === Process Definitions ===

    @POST
    @Path("/definitions")
    public Response createOrUpdateProcessDefinition(JSONObject definition) {
        // Insert or update process_definition table
        return Response.ok().build();
    }

    @GET
    @Path("/definitions")
    public Response getAllProcessDefinitions() {
        // Return list of all process definitions
        return Response.ok(/* List<ProcessDefinition> */).build();
    }

    @GET
    @Path("/definitions/{id}")
    public Response getProcessDefinition(@PathParam("id") String id) {
        // Return specific process definition by ID
        return Response.ok(/* ProcessDefinition */).build();
    }

    @DELETE
    @Path("/definitions/{id}")
    public Response deleteProcessDefinition(@PathParam("id") String id) {
        // Remove definition from DB if needed
        return Response.noContent().build();
    }


    // === Scheduled Processes ===

    @POST
    @Path("/schedule")
    public Response scheduleProcess(JSONObject processInstance) {
        // Insert into PROCESSES table, link to definition by ID
        return Response.ok(/* UUID or scheduled info */).build();
    }

    @GET
    @Path("/processes")
    public Response getAllScheduledProcesses() {
        // Optional: return all scheduled processes
        return Response.ok(/* List<ScheduledProcess> */).build();
    }

    @GET
    @Path("/processes/{uuid}")
    public Response getScheduledProcess(@PathParam("uuid") String uuid) {
        // Return full JSON for worker (merged process_definition + scheduled params)
        return Response.ok(/* Process */).build();
    }

    @DELETE
    @Path("/processes/{uuid}")
    public Response cancelScheduledProcess(@PathParam("uuid") String uuid) {
        // Optional: mark a process as cancelled
        return Response.noContent().build();
    }
}
