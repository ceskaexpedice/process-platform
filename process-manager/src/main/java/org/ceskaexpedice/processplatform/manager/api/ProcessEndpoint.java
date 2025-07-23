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

import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ScheduleMainProcess;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.client.WorkerClient;
import org.ceskaexpedice.processplatform.manager.client.WorkerClientFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.net.URI;

/**
 * ProcessEndpoint
 * @author ppodsednik
 */
@Path("/process")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessEndpoint {

    private final ProcessService processService;
    private final WorkerClient workerClient;

    public ProcessEndpoint(ProcessService processService, NodeService nodeService) {
        this.processService = processService;
        this.workerClient = WorkerClientFactory.createWorkerClient(processService, nodeService);
    }

    @POST
    public Response scheduleMainProcess(ScheduleMainProcess scheduleMainProcess) {
        processService.scheduleMainProcess(scheduleMainProcess);
        return APIRestUtilities.ok("Main process for profile %s scheduled", scheduleMainProcess.getProfileId());
    }

    @GET
    @Path("{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("processId") String processId) {
        ProcessInfo process = processService.getProcess(processId);
        if (process == null) {
            return APIRestUtilities.notFound("Process not found: %s", processId);
        }
        return Response.ok(process).build();
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

    @GET
    @Path("owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwners() {
        return Response.ok().build();
    }


    @GET
    @Path("{processId}/log/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                      @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = workerClient.getProcessLog(processId, false);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + ".log\"")
                .build();
    }

    @GET
    @Path("{processId}/log/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsErr(@PathParam("processId") String processId,
                                      @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/log/out/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsOutLines(@PathParam("processId") String processId,
                                           @QueryParam("offset") String offsetStr,
                                           @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/log/err/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsErrLines(@PathParam("processId") String processId,
                                           @QueryParam("offset") String offsetStr,
                                           @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

}
