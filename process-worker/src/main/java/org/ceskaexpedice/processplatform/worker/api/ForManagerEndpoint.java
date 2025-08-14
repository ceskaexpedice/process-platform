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

import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * ForManagerEndpoint
 * @author ppodsednik
 */
@Path("/manager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForManagerEndpoint {
    private final ForManagerService forManagerService;

    public ForManagerEndpoint(ForManagerService forManagerService) {
        this.forManagerService = forManagerService;
    }

    @GET
    @Path("{processId}/log/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                      @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = forManagerService.getProcessLog(processId, false);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

    @GET
    @Path("{processId}/log/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogErr(@PathParam("processId") String processId,
                                      @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = forManagerService.getProcessLog(processId, true);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

    @DELETE
    @Path("{processId}/directory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProcessWorkingDirectory(@PathParam("processId") String processId) {
        forManagerService.deleteWorkingDir(processId);
        return APIRestUtilities.ok("Process [%s] working directory deleted", processId);
    }

    @DELETE
    @Path("{pid}/kill")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killProcessJvm(@PathParam("pid") String pid) {
        boolean killed = forManagerService.killProcessJvm(pid);
        if(!killed){
            return APIRestUtilities.notFound("Process JVM not found [%s]", pid);
        }else{
            return APIRestUtilities.ok("Process JVM Killed [%s]", pid);
        }
    }

}
