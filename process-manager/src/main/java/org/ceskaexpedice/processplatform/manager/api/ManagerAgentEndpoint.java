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

import org.ceskaexpedice.processplatform.common.entity.*;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ManagerAgentEndpoint
 * @author ppodsednik
 */
@Path("/agent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManagerAgentEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ManagerAgentEndpoint.class.getName());

    private final PluginService pluginService;
    private final ProcessService processService;

    public ManagerAgentEndpoint(PluginService pluginService, ProcessService processService) {
        this.pluginService = pluginService;
        this.processService = processService;
    }

    @GET
    @Path("/next-process")
    public Response getNextProcess(@QueryParam("workerId") String workerId, @QueryParam("workerTags") List<String> tags) {
        try {
            ScheduledProcess scheduledProcess = new ScheduledProcess();
            // batchId
            if (scheduledProcess != null) {
                return Response.ok(scheduledProcess).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @POST
    @Path("/register-plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        try {
            System.out.println("ManagerAgentEndpoint: registerPlugin: " + pluginInfo.getPluginId() + ",# of profiles-" + pluginInfo.getProfiles().size());
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @POST
    @Path("/schedule-sub-process")
    public Response scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        try {
            System.out.println("ManagerAgentEndpoint: scheduleSubProcess: " + scheduleSubProcess.getProfileId()
                    + ",batchId-" + scheduleSubProcess.getBatchId());
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @PUT
    @Path("/pid/{processId}")
    public Response updateProcessPid(@PathParam("processId") String processId, @QueryParam("pid") String pid) {
        try {
            // Store OS process ID of the spawned JVM
            System.out.println("ManagerAgentEndpoint: updateProcessPid:processId-" + processId + ";pid-" + pid);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @PUT
    @Path("/state/{processId}")
    public Response updateProcessState(@PathParam("processId") String processId, @QueryParam("state") ProcessState state) {
        try {
            // Store OS process ID of the spawned JVM
            System.out.println("ManagerAgentEndpoint: updateProcessState:processId-" + processId + ";state-" + state);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @PUT
    @Path("/name/{processId}")
    public Response updateProcessName(@PathParam("processId") String processId, @QueryParam("name") String name) {
        try {
            // Store OS process ID of the spawned JVM
            System.out.println("ManagerAgentEndpoint: updateProcessName:processId-" + processId + ";name-" + name);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }


        /*

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
