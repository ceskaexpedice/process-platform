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
    public Response getNextProcess(@QueryParam("workerTags") List<String> tags) {
        ScheduledProcess scheduledProcess = new ScheduledProcess();
        // batchId
        if (scheduledProcess != null) {
            return Response.ok(scheduledProcess).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @POST
    @Path("/register-plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        System.out.println("ManagerAgentEndpoint: registerPlugin: " + pluginInfo.getPluginId() + ",# of profiles-" + pluginInfo.getProfiles().size());
        return Response.ok().build();
    }

    @POST
    @Path("/schedule-sub-process")
    public Response scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        System.out.println("ManagerAgentEndpoint: scheduleSubProcess: " + scheduleSubProcess.getProfileId()
                + ",batchId-" + scheduleSubProcess.getBatchId());
        return Response.ok().build();
    }

    @PUT
    @Path("/worker/{processId}")
    public Response updateWorker(@PathParam("processId") String processId, @QueryParam("worker") String worker) {
        // Store OS process ID of the spawned JVM
        System.out.println("ManagerAgentEndpoint: updateWorker:processId-" + processId + ";worker-" + worker);
        return Response.ok().build();
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

}
