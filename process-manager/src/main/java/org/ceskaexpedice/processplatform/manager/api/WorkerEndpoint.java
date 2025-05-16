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

import org.ceskaexpedice.processplatform.common.TaskState;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/manager")
public class WorkerEndpoint {

    //@Inject
//    private TaskQueue taskQueue;

    @GET
    @Path("next")
    @Produces(MediaType.APPLICATION_JSON)
    public Response nextProcess(@Context UriInfo uriInfo) {
        List<String> tagList = uriInfo.getQueryParameters().get("tags");

        uriInfo.getQueryParameters().forEach((key, value) -> {});
        return Response.ok().entity("result Owners".toString()).build();
    }

    @GET
    @Path("/get-process/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcess(@PathParam("uuid") String uuid) {
        try (Connection conn = dataSource.getConnection()) {
            org.ceskaexpedice.processplatform.common.Process proc = buildProcessFromDatabase(conn, uuid);
            return Response.ok(proc).build(); // Jackson will serialize automatically
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/state")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProcessState(@PathParam("id") String taskId, @Context UriInfo uriInfo) {

        String taskStateS = uriInfo.getQueryParameters().getFirst("taskState");
        TaskState taskState = TaskState.valueOf(taskStateS);
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
        TaskState taskState = TaskState.valueOf(taskStateS);
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
        TaskState taskState = TaskState.valueOf(taskStateS);
        /*
        if (StringUtilities.isEmpty(type)) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        try {
            return QueueSAO.QueueEntryType.valueOf(type);
        }*/

        return Response.ok().entity("result Owners".toString()).build();
    }

    public org.ceskaexpedice.processplatform.common.Process buildProcessFromDatabase(Connection conn, String uuid) throws SQLException {
        org.ceskaexpedice.processplatform.common.Process process = new org.ceskaexpedice.processplatform.common.Process();

        // 1. Load runtime process instance
        String processSql = "SELECT * FROM processes WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(processSql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    process.setUuid(rs.getString("UUID"));
                    process.setDefId(rs.getString("DEFID"));
                    process.setName(rs.getString("NAME"));
                    process.setParamsJson(rs.getString("PARAMS"));
                    process.setPlannedTimestamp(rs.getTimestamp("PLANNED").getTime());
                    Timestamp started = rs.getTimestamp("STARTED");
                    if (started != null) {
                        process.setStartedTimestamp(started.getTime());
                    }
                    process.setStatus(rs.getInt("STATUS"));
                } else {
                    throw new IllegalArgumentException("No process found with UUID: " + uuid);
                }
            }
        }

        // 2. Load static process definition
        String defSql = "SELECT * FROM process_definitions WHERE defid = ?";
        try (PreparedStatement ps = conn.prepareStatement(defSql)) {
            ps.setString(1, process.getDefId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    process.setDescription(rs.getString("DESCRIPTION"));
                    process.setMainClass(rs.getString("MAINCLASS"));
                    process.setJavaParameters(rs.getString("JAVAPROCESSPARAMETERS"));
                    process.setStandardOs(rs.getString("STANDARDOS"));
                    process.setErrOs(rs.getString("ERR_OS"));
                    process.setSecuredAction(rs.getString("SECUREDACTION"));
                } else {
                    throw new IllegalArgumentException("No process definition found for defid: " + process.getDefId());
                }
            }
        }

        // Optional: parse arguments from paramsJson
        // You can convert this to argument list if needed, or leave this to the Worker

        return process;
    }
}
