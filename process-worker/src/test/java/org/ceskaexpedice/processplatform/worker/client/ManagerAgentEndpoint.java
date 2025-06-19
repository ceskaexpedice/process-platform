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


import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * ManagerAgentEndpoint
 *
 * @author ppodsednik
 */
@Path("/agent")
public class ManagerAgentEndpoint {
  //private final AgentService agentService;

  /*
  public ManagerAgentEndpoint(AgentService agentService) {
    //this.agentService = agentService;
  }*/

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/register-plugin")
  public void registerPlugin(PluginInfoTO pluginInfo) {
    System.out.println(pluginInfo);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/next-process")
  public ScheduledProcessTO getNextProcess(@QueryParam("tags") List<String> tags) {
    //List<String> tagList = uriInfo.getQueryParameters().get("tags");

    List<String> jvmArgs = new ArrayList<>();
    jvmArgs.add("-Xmx1024m");
    jvmArgs.add("-Xms256m");
    jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

    Map<String,String> payload = new HashMap<>();
    payload.put("name","Petr");
    payload.put("surname","Harasil");

    ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
            UUID.randomUUID() + "",
            "testPlugin1",
            "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1",
            payload,
            jvmArgs);
    return scheduledProcessTO;
    //return agentService.getNextScheduledProcess();
        /*
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

         */
  }

  @PUT
  @Path("/pid/{uuid}")
  public Response updateProcessPID(@PathParam("uuid") String uuid, @QueryParam("pid") String pid) {
    // Store OS process ID of the spawned JVM
    return Response.ok().build();
  }

}
