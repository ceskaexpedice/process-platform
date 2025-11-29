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
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * ForWorkerEndpoint
 * @author ppodsednik
 */
@Path("/worker")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForWorkerEndpoint {

    public static final Logger  LOGGER = Logger.getLogger(ForWorkerEndpoint.class.getName());

    private final PluginService pluginService;
    private final ProcessService processService;
    private final NodeService nodeService;


    public ForWorkerEndpoint(PluginService pluginService, ProcessService processService, NodeService nodeService) {
        this.pluginService = pluginService;
        this.processService = processService;
        this.nodeService = nodeService;
    }

    @POST
    @Path("/register_node")
    public Response registerNode(Node node) {
        nodeService.registerNode(node);
        return APIRestUtilities.ok("Node [%s] registered", node.getNodeId());
    }

    @POST
    @Path("/register_plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        LOGGER.info(String.format("Register payload %s", pluginInfo.toString()));
        pluginService.registerPlugin(pluginInfo);
        return APIRestUtilities.ok("Plugin [%s] registered", pluginInfo.getPluginId());
    }

    @GET
    @Path("/next_process/{workerId}")
    public Response getNextScheduledProcess(@PathParam("workerId") String workerId) {
        ScheduledProcess scheduledProcess = processService.getNextScheduledProcess(workerId);
        if (scheduledProcess != null) {
            return Response.ok(scheduledProcess).build();
        } else {
            return APIRestUtilities.notFound("No scheduled process found for [%s]", workerId);
        }
    }

    @POST
    @Path("/schedule_sub_process")
    public Response scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        String processId = processService.scheduleSubProcess(scheduleSubProcess);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processId", processId);
        return APIRestUtilities.jsonPayload(jsonObject.toString());
    }

    @PUT
    @Path("/pid/{processId}")
    public Response updateProcessPid(@PathParam("processId") String processId, @QueryParam("pid") int pid) {
        processService.updatePid(processId, pid);
        return APIRestUtilities.ok("Process pid [%s] used for the process [%s]", pid, processId);
    }

    @PUT
    @Path("/state/{processId}")
    public Response updateProcessState(@PathParam("processId") String processId, @QueryParam("state") ProcessState state) {
        processService.updateState(processId, state);
        return APIRestUtilities.ok("Process state [%s] used for the process [%s]", state, processId);
    }

    @PUT
    @Path("/description/{processId}")
    public Response updateProcessDescription(@PathParam("processId") String processId, @QueryParam("description") String description) {
        processService.updateDescription(processId, description);
        return APIRestUtilities.ok("Process description [%s] used for the process [%s]", description, processId);
    }

}
