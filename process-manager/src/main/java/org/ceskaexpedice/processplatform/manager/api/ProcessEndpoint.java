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

import org.ceskaexpedice.processplatform.common.model.Batch;
import org.ceskaexpedice.processplatform.common.model.BatchFilter;
import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ScheduleMainProcess;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.common.utils.StringUtils;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.client.WorkerClient;
import org.ceskaexpedice.processplatform.manager.client.WorkerClientFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * ProcessEndpoint
 * @author ppodsednik
 */
@Path("/process")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessEndpoint {

    private final ProcessService processService;

    public ProcessEndpoint(ProcessService processService, NodeService nodeService) {
        this.processService = processService;
    }

    @POST
    public Response scheduleMainProcess(ScheduleMainProcess scheduleMainProcess) {
        String processId = processService.scheduleMainProcess(scheduleMainProcess);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processId", processId);
        return APIRestUtilities.jsonPayload(jsonObject.toString());
    }

    @GET
    @Path("{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("processId") String processId) {
        ProcessInfo process = processService.getProcess(processId);
        if (process == null) {
            return APIRestUtilities.notFound("Process not found: [%s]", processId);
        }
        return Response.ok(process).build();
    }

    // TODO batch
    @GET
    @Path("batches")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatches(
            @QueryParam("offset") String offsetStr,
            @QueryParam("limit") String limitStr,
            @QueryParam("owner") String owner,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("state") String state
    ) {
        int offset = processService.getBatchOffset(offsetStr);
        int limit = processService.getBatchLimit(limitStr);
        BatchFilter batchFilter = processService.createBatchFilter(owner, from, to, state);
        // TODO implement batches
        // TODO int totalSize = this.processManager.getBatchesCount(filter);
        int totalSize = 0;
        JSONObject result = new JSONObject();
        // TODO result.put("offset", offset);
        // TODO result.put("limit", limit);
        result.put("total_size", totalSize);

        //batch & process data
        List<Batch> batches = processService.getBatches(null, 0, 50);
        JSONArray batchesJson = new JSONArray();
        for (Batch batch : batches) {
            JSONObject batchJson = batchToJson(batch);
            batchesJson.put(batchJson);
        }
        result.put("batches", batchesJson);
        return APIRestUtilities.jsonPayload(result.toString());
    }

    // TODO batch
    private JSONObject batchToJson(Batch batchWithProcesses) {
        JSONObject json = new JSONObject();
        //batch
        JSONObject batchJson = new JSONObject();
        batchJson.put("token", batchWithProcesses.getBatchId());
        batchJson.put("id", batchWithProcesses.getFirstProcessId());
        batchJson.put("state", toBatchStateName(batchWithProcesses.getStatus().getVal()));

        batchJson.put("planned", toFormattedStringOrNull(batchWithProcesses.getPlanned()));
        batchJson.put("started", toFormattedStringOrNull(batchWithProcesses.getStarted()));
        batchJson.put("finished", toFormattedStringOrNull(batchWithProcesses.getFinished()));
        batchJson.put("owner_id", batchWithProcesses.getOwner());


        json.put("batch", batchJson);
        //processes
        JSONArray processArray = new JSONArray();
        for (ProcessInfo process : batchWithProcesses.getProcesses()) {
            JSONObject processJson = new JSONObject();
            processJson.put("id", process.getProcessId());
            processJson.put("uuid", process.getProcessId());
            processJson.put("defid", process.getProfileId());
            processJson.put("name", process.getDescription());
            processJson.put("state", toProcessStateName(process.getStatus().getVal()));
            processJson.put("planned", toFormattedStringOrNull(process.getPlanned()));
            processJson.put("started", toFormattedStringOrNull(process.getStarted()));
            processJson.put("finished", toFormattedStringOrNull(process.getFinished()));
            processArray.put(processJson);
        }
        json.put("processes", processArray);
        return json;
    }

    // TODO batch
    private String toProcessStateName(Integer stateCode) {
        switch (stateCode) {
            case 0:
                return "NOT_RUNNING";
            case 1:
                return "RUNNING";
            case 2:
                return "FINISHED";
            case 3:
                return "FAILED";
            case 4:
                return "KILLED";
            case 5:
                return "PLANNED";
            case 9:
                return "WARNING";
            default:
                return "UNKNOWN";
        }
    }

    // TODO batch
    private String toBatchStateName(Integer batchStateCode) {
        switch (batchStateCode) {
            case 0:
                return "PLANNED";
            case 1:
                return "RUNNING";
            case 2:
                return "FINISHED";
            case 3:
                return "FAILED";
            case 4:
                return "KILLED";
            case 5:
                return "WARNING";
            default:
                return "UNKNOWN";
        }
    }

    // TODO batch
    public static String toFormattedStringOrNull(Date dateTime) {
        LocalDateTime localDateTime = dateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        if (dateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime);
        }
    }

    @DELETE
    @Path("batches/by_first_process_id/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatch(@PathParam("processId") String processId) {
        // TODO implement batches
        return Response.ok().build();
    }

    @DELETE
    @Path("batches/by_first_process_id/{processId}/execution")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killBatch(@PathParam("processId") String processId) {
        // TODO batch
        return Response.ok().build();
    }

    @GET
    @Path("owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwners() {
        // TODO implement owners
        return Response.ok().build();
    }


    @GET
    @Path("{processId}/log/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogOut(@PathParam("processId") String processId,
                                      @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = processService.getProcessLog(processId, false);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

    @GET
    @Path("{processId}/log/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogErr(@PathParam("processId") String processId,
                                      @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = processService.getProcessLog(processId, true);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

}
