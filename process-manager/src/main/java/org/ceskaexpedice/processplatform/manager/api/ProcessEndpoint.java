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

import org.ceskaexpedice.processplatform.common.entity.ScheduleMainProcess;
import org.ceskaexpedice.processplatform.common.entity.ScheduleProcess;
import org.ceskaexpedice.processplatform.common.entity.ScheduledProcess;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * ProcessEndpoint
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
    public Response scheduleProcess(ScheduleMainProcess scheduleProcess) {
        String ownerId = scheduleProcess.getOwnerId(); // ownerId must be sent from Kramerius ProcessResource
        // batch id is created here because this is main process
        String batchId = UUID.randomUUID().toString();
        // ownerId + batchId will be persisted to pcp-processes

        return Response.ok().entity("{}").build();
    }

    @GET
    @Path("{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("processId") String processId) {
        return Response.ok().build();
    }

    @GET
    @Path("owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwners() {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                                   @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsErr(@PathParam("processId") String processId,
                                                   @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/out/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsOutLines(@PathParam("processId") String processId,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/err/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsErrLines(@PathParam("processId") String processId,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @GET
    @Path("batches")
    @Produces(MediaType.APPLICATION_JSON)
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

    @DELETE
    @Path("batches/by_first_process_id/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatch(@PathParam("processId") String processId) {
        return Response.ok().build();
    }

    @DELETE
    @Path("batches/by_first_process_id/{processId}/execution")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killBatch(@PathParam("processId") String processId) {
        return Response.ok().build();
    }



}
