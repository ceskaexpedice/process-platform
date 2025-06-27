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
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WorkerEndpoint
 * @author ppodsednik
 */
@Path("/agent")
public class AgentEndpoint {
    private static final Logger LOGGER = Logger.getLogger(AgentEndpoint.class.getName());

    private final ProfileService profileService;
    private final ProcessService processService;

    public AgentEndpoint(ProfileService profileService, ProcessService processService) {
        this.profileService = profileService;
        this.processService = processService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register-plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        System.out.println("ManagerAgentEndpoint: registerPlugin: " + pluginInfo.getPluginId() + ",# of profiles-" + pluginInfo.getProfiles().size());
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next-process")
    public ScheduledProcess getNextProcess(@QueryParam("tags") List<String> tags) {
        try {
            ScheduledProcess scheduledProcess = new ScheduledProcess();
            return scheduledProcess;
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PUT
    @Path("/pid/{processId}")
    public Response updateProcessPid(@PathParam("processId") String processId, @QueryParam("pid") String pid) {
        // Store OS process ID of the spawned JVM
        System.out.println("ManagerAgentEndpoint: updateProcessPid:processId-" + processId + ";pid-" + pid);
        return Response.ok().build();
    }

    @PUT
    @Path("/state/{processId}")
    public Response updateProcessState(@PathParam("processId") String processId, @QueryParam("state") ProcessState state) {
        // Store OS process ID of the spawned JVM
        System.out.println("ManagerAgentEndpoint: updateProcessState:processId-" + processId + ";state-" + state);
        return Response.ok().build();
    }

    @PUT
    @Path("/name/{processId}")
    public Response updateProcessName(@PathParam("processId") String processId, @QueryParam("name") String name) {
        // Store OS process ID of the spawned JVM
        System.out.println("ManagerAgentEndpoint: updateProcessName:processId-" + processId + ";name-" + name);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/schedule-sub-process")
    public void scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        System.out.println("ManagerAgentEndpoint: scheduleSubProcess: " + scheduleSubProcess.getProfileId() + ",plugin-" +
                scheduleSubProcess.getPluginId() + ",batchId-" + scheduleSubProcess.getBatchId());
    }

        /*
    @GET
    @Path("/next-process")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduledProcess getNextProcess(@QueryParam("tags") List<String> tags) {
        return agentService.getNextScheduledProcess();
                // Implementation: fetch next process from DB
        ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
                UUID.randomUUID(),
                "pluginInfo.getPluginId()",
                "profileId",
                "pluginInfo.getMainClass()",
                null,
                null,
                null);
        if (scheduledProcessTO != null) {
            return Response.ok(scheduledProcessTO).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }


    }*/

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
