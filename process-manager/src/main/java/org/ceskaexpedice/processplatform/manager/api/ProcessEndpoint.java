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
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.net.URI;
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
    public Response scheduleProcess(ScheduleMainProcess scheduleProcess) {
        try {
            String ownerId = scheduleProcess.getOwnerId(); // ownerId must be sent from Kramerius ProcessResource
            // batch id is created here because this is main process
            String batchId = UUID.randomUUID().toString();
            // ownerId + batchId will be persisted to pcp-processes

            return Response.ok().entity("{}").build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("processId") String processId) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("{processId}/logs/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                                   @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    // TODO this just an example here
    @GET
    @Path("/log/{processId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLog(@PathParam("processId") String processId) {
        URI workerUri = findResponsibleWorkerUri(processId); // implement your lookup logic
        Client client = ClientBuilder.newClient();
        Response workerResponse = client.target(workerUri)
                .path("/agent/log/" + processId)
                .request()
                .get();

        if (workerResponse.getStatus() != 200) {
            return Response.status(workerResponse.getStatus()).build();
        }

        InputStream workerStream = workerResponse.readEntity(InputStream.class);

        return Response.ok((StreamingOutput) output -> {
                    try (workerStream) {
                        workerStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + processId + ".log\"")
                .build();
    }

    // TODO this just an example here
    private URI findResponsibleWorkerUri(String processId) {
        // Use registry, DB, etc.
        return URI.create("http://worker-1:8080"); // placeholder
    }

    @GET
    @Path("{processId}/logs/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsErr(@PathParam("processId") String processId,
                                                   @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("{processId}/logs/out/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsOutLines(@PathParam("processId") String processId,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("{processId}/logs/err/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsErrLines(@PathParam("processId") String processId,
                                                        @QueryParam("offset") String offsetStr,
                                                        @QueryParam("limit") String limitStr) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
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
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @DELETE
    @Path("batches/by_first_process_id/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatch(@PathParam("processId") String processId) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @DELETE
    @Path("batches/by_first_process_id/{processId}/execution")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killBatch(@PathParam("processId") String processId) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwners() {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

}
