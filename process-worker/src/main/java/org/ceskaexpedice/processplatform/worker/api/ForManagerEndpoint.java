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
package org.ceskaexpedice.processplatform.worker.api;

import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * AgentEndpoint
 * @author ppodsednik
 */
@Path("/manager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForManagerEndpoint {

    public ForManagerEndpoint(ForManagerService agentService) {
    }

    @GET
    @Path("{processId}/logs/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                      @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    // TODO this just an example here
    @GET
    @Path("/log/{processId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response streamLog(@PathParam("processId") String processId) {
        //InputStream logStream = workerLogService.openLogStream(processId); // FileInputStream or similar
        InputStream logStream = null;

        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + processId + ".log\"")
                .build();
    }

    @GET
    @Path("{processId}/logs/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsErr(@PathParam("processId") String processId,
                                      @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/out/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsOutLines(@PathParam("processId") String processId,
                                           @QueryParam("offset") String offsetStr,
                                           @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @GET
    @Path("{processId}/logs/err/lines")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessLogsErrLines(@PathParam("processId") String processId,
                                           @QueryParam("offset") String offsetStr,
                                           @QueryParam("limit") String limitStr) {
        return Response.ok().build();
    }

    @POST
    @Path("/kill")
    public Response killJVM() {
        return Response.ok().build();
    }
}
