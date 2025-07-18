/*
 * Copyright © 2021 Accenture and/or its affiliates. All Rights Reserved.
 * Permission to any use, copy, modify, and distribute this software and
 * its documentation for any purpose is subject to a licensing agreement
 * duly entered into with the copyright owner or its affiliate.
 * All information contained herein is, and remains the property of Accenture
 * and/or its affiliates and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Accenture and/or
 * its affiliates and its suppliers and may be covered by one or more patents
 * or pending patent applications in one or more jurisdictions worldwide,
 * and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Accenture and/or its affiliates.
 */
package org.ceskaexpedice.processplatform.worker.client;


import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.Constants;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * WorkerTestEndpoint
 *
 * @author ppodsednik
 */
@Path("/worker")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkerTestEndpoint {
    private static int counter;

    @POST
    @Path("/register_node")
    public Response registerNode(Node node) {
        System.out.println("ManagerAgentEndpoint: registerNode: " + node.getNodeId());
        return Response.ok().build();
    }

    @POST
    @Path("/register_plugin")
    public Response registerPlugin(PluginInfo pluginInfo) {
        System.out.println("ManagerAgentEndpoint: registerPlugin: " + pluginInfo.getPluginId() + ",# of profiles-" + pluginInfo.getProfiles().size());
        return Response.ok().build();
    }

    @GET
    @Path("/next_process/{workerId}")
    public Response getNextScheduledProcess(@PathParam("workerId") String workerId) {
        System.out.print("ManagerAgentEndpoint: getNextProcess:");
        counter++;
        if (counter == 1) {
            List<String> jvmArgs = new ArrayList<>();
            jvmArgs.add("-Xmx1024m");
            jvmArgs.add("-Xms256m");
            //jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

            Map<String, String> payload = new HashMap<>();
            payload.put("name", "Petr");
            payload.put("surname", "Harasil");

            ScheduledProcess scheduledProcess = new ScheduledProcess(
                    Constants.PLUGIN1_PROCESS_ID,
                    Constants.PLUGIN1_PROFILE_SMALL,
                    Constants.PLUGIN1_ID,
                    Constants.PLUGIN1_MAIN_CLASS,
                    payload,
                    jvmArgs,
                    "batchId",
                    "ownerId");
            System.out.println(scheduledProcess.getProcessId());
            return Response.ok(scheduledProcess).build();
        } else if (counter == 2) {
            ScheduledProcess scheduledProcess = new ScheduledProcess(
                    Constants.PLUGIN2_PROCESS_ID,
                    Constants.PLUGIN2_ID,
                    Constants.PLUGIN2_ID,
                    Constants.PLUGIN2_MAIN_CLASS,
                    new HashMap<>(),
                    new ArrayList<>(),
                    "batchId",
                    "ownerId");
            System.out.println(scheduledProcess.getProcessId());
            return Response.ok(scheduledProcess).build();
        } else {
            System.out.println("null");
            return null;
        }
    }

    @POST
    @Path("/schedule_sub_process")
    public Response scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        System.out.println("ManagerAgentEndpoint: scheduleSubProcess: " + scheduleSubProcess.getProfileId() +
                ",batchId-" + scheduleSubProcess.getBatchId());
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
