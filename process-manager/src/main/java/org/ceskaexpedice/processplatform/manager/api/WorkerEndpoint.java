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

import org.ceskaexpedice.processplatform.common.dto.ProcessState;
import org.ceskaexpedice.processplatform.manager.api.service.WorkerService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/worker")
public class WorkerEndpoint {

    public WorkerEndpoint(WorkerService workerService) {
    }

    //@Inject
//    private TaskQueue taskQueue;
    // === Get Next Available Process ===
    @POST
    @Path("/next")
    public Response getNextProcess(List<String> supportedProcessIds) {
        // Return the next suitable process based on supported plugins
        // Merges data from process_definition + processes
        return Response.ok(/* Process */).build();
    }

    // === Update Process State (e.g., running, completed, failed) ===
    @PUT
    @Path("/state/{uuid}")
    public Response updateProcessState(@PathParam("uuid") String uuid, @QueryParam("state") String newState) {
        // Update status in the processes table
        return Response.ok().build();
    }

    // === Update Worker PID ===
    @PUT
    @Path("/pid/{uuid}")
    public Response updateProcessPID(@PathParam("uuid") String uuid, @QueryParam("pid") int pid) {
        // Store OS process ID of the spawned JVM
        return Response.ok().build();
    }

    // === Update Human-Readable Name ===
    @PUT
    @Path("/name/{uuid}")
    public Response updateProcessName(@PathParam("uuid") String uuid, @QueryParam("name") String name) {
        // Update name in the processes table for display
        return Response.ok().build();
    }

    @GET
    @Path("next")
    @Produces(MediaType.APPLICATION_JSON)
    public Response nextProcess(@Context UriInfo uriInfo) {
        List<String> tagList = uriInfo.getQueryParameters().get("tags");

        uriInfo.getQueryParameters().forEach((key, value) -> {});
        return Response.ok().entity("result Owners".toString()).build();
    }

    /*
    @GET
    @Path("/get-process/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("uuid") String uuid) {
        try (Connection conn = dataSource.getConnection()) {
            ProcessDefinition proc = buildProcessFromDatabase(conn, uuid);
            return Response.ok(proc).build(); // Jackson will serialize automatically
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

     */

    @PUT
    @Path("/{id}/state")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProcessState(@PathParam("id") String taskId, @Context UriInfo uriInfo) {

        String taskStateS = uriInfo.getQueryParameters().getFirst("taskState");
        ProcessState processState = ProcessState.valueOf(taskStateS);
        /*
        if (StringUtilities.isEmpty(type)) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        try {
            return QueueSAO.QueueEntryType.valueOf(type);
        }*/

        return Response.ok().entity("result Owners".toString()).build();
    }

    @PUT
    @Path("/{id}/pid")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProcessPid(@PathParam("id") String taskId, @Context UriInfo uriInfo) {

        String taskStateS = uriInfo.getQueryParameters().getFirst("taskState");
        ProcessState processState = ProcessState.valueOf(taskStateS);
        /*
        if (StringUtilities.isEmpty(type)) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        try {
            return QueueSAO.QueueEntryType.valueOf(type);
        }*/

        return Response.ok().entity("result Owners".toString()).build();
    }

    @PUT
    @Path("/{id}/name")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProcessName(@PathParam("id") String taskId, @Context UriInfo uriInfo) {

        String taskStateS = uriInfo.getQueryParameters().getFirst("taskState");
        ProcessState processState = ProcessState.valueOf(taskStateS);
        /*
        if (StringUtilities.isEmpty(type)) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        try {
            return QueueSAO.QueueEntryType.valueOf(type);
        }*/

        return Response.ok().entity("result Owners".toString()).build();
    }
/*
    public ProcessDefinition buildProcessFromDatabase(Connection conn, String uuid) throws SQLException {
        ProcessDefinition processDefinition = new ProcessDefinition();

        // 1. Load runtime process instance
        String processSql = "SELECT * FROM processes WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(processSql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    processDefinition.setUuid(rs.getString("UUID"));
                    processDefinition.setDefId(rs.getString("DEFID"));
                    processDefinition.setName(rs.getString("NAME"));
                    processDefinition.setParamsJson(rs.getString("PARAMS"));
                    processDefinition.setPlannedTimestamp(rs.getTimestamp("PLANNED").getTime());
                    Timestamp started = rs.getTimestamp("STARTED");
                    if (started != null) {
                        processDefinition.setStartedTimestamp(started.getTime());
                    }
                    processDefinition.setStatus(rs.getInt("STATUS"));
                } else {
                    throw new IllegalArgumentException("No process found with UUID: " + uuid);
                }
            }
        }

        // 2. Load static process definition
        String defSql = "SELECT * FROM process_definitions WHERE defid = ?";
        try (PreparedStatement ps = conn.prepareStatement(defSql)) {
            ps.setString(1, processDefinition.getDefId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    processDefinition.setDescription(rs.getString("DESCRIPTION"));
                    processDefinition.setMainClass(rs.getString("MAINCLASS"));
                    processDefinition.setJavaParameters(rs.getString("JAVAPROCESSPARAMETERS"));
                    processDefinition.setStandardOs(rs.getString("STANDARDOS"));
                    processDefinition.setErrOs(rs.getString("ERR_OS"));
                    processDefinition.setSecuredAction(rs.getString("SECUREDACTION"));
                } else {
                    throw new IllegalArgumentException("No process definition found for defid: " + processDefinition.getDefId());
                }
            }
        }

        // Optional: parse arguments from paramsJson
        // You can convert this to argument list if needed, or leave this to the Worker

        return processDefinition;
    }

 */
}
