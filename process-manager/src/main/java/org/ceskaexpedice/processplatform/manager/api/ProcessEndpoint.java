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

import org.ceskaexpedice.processplatform.common.entity.ScheduleProcess;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * AdminEndpoint
 * @author ppodsednik
 */
@Path("/process")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessEndpoint {

    public ProcessEndpoint(ProcessService processService) {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response scheduleProcess(ScheduleProcess scheduleProcess) {
        return Response.ok().entity("{}").build();
    }

    @PUT
    @Path("/{processId}/approve")
    public Response approveProcess(@PathParam("processId") String processId) {
        return Response.ok().build();
    }

    @GET
    @Path("owners")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getOwners() {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_id/{process_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getProcessByProcessId(@PathParam("process_id") String processId) {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_uuid/{process_uuid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getProcessByProcessUuid(@PathParam("process_uuid") String processUuid) {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_uuid/{process_uuid}/logs/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOutByProcessUuid(@PathParam("process_uuid") String processUuid,
                                                   @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_uuid/{process_uuid}/logs/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsErrByProcessUuid(@PathParam("process_uuid") String processUuid,
                                                   @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_uuid/{process_uuid}/logs/out/lines")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getProcessLogsOutLinesByProcessUuid(@PathParam("process_uuid") String processUuid,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @GET
    @Path("by_process_uuid/{process_uuid}/logs/err/lines")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getProcessLogsErrLinesByProcessUuid(@PathParam("process_uuid") String processUuid,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @DELETE
    @Path("batches/by_first_process_id/{process_id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteBatch(@PathParam("process_id") String processId) {
        return Response.ok().build();
    }

    @DELETE
    @Path("batches/by_first_process_id/{process_id}/execution")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response killBatch(@PathParam("process_id") String processId) {
        return Response.ok().build();
    }

    @GET
    @Path("batches")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getBatches(
            @QueryParam("offset") String offsetStr,
            @QueryParam("limit") String limitStr,
            @QueryParam("owner") String filterOwner,
            @QueryParam("from") String filterFrom,
            @QueryParam("until") String filterUntil,
            @QueryParam("state") String filterState
    ) {
        return Response.ok().build();
    }


}
