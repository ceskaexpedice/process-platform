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

import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * ManagerAgentEndpoint
 * @author ppodsednik
 */
@Path("/worker")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkerEndpoint {
    private static final Logger LOGGER = Logger.getLogger(WorkerEndpoint.class.getName());

    private final PluginService pluginService;
    private final ProcessService processService;
    private final NodeService nodeService;

    public WorkerEndpoint(PluginService pluginService, ProcessService processService, NodeService nodeService) {
        this.pluginService = pluginService;
        this.processService = processService;
        this.nodeService = nodeService;
    }

    @POST
    @Path("/register_node")
    public Response registerNode(Node node) {
        nodeService.registerNode(node);
        return APIRestUtilities.ok("Node %s registered", node.getNodeId());
    }

    @POST
    @Path("/register_plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        pluginService.registerPlugin(pluginInfo);
        return APIRestUtilities.ok("Plugin %s registered", pluginInfo.getPluginId());
    }

    @GET
    @Path("/next_process/{workerId}")
    public Response getNextProcess(@PathParam("workerId") String workerId) {
        ScheduledProcess scheduledProcess = processService.getNextScheduledProcess(workerId);
        // batchId
        if (scheduledProcess != null) {
            return Response.ok(scheduledProcess).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @POST
    @Path("/schedule_sub_process")
    public Response scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        System.out.println("ManagerAgentEndpoint: scheduleSubProcess: " + scheduleSubProcess.getProfileId()
                + ",batchId-" + scheduleSubProcess.getBatchId());
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
