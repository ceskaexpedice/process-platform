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
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

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
        return getProcessLogHelper(processId, fileName, false);
    }

    @GET
    @Path("{processId}/log/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogErr(@PathParam("processId") String processId,
                                     @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        return getProcessLogHelper(processId, fileName, true);
    }

    @GET
    @Path("{processId}/log/out/lines")
    public Response getProcessLogOutLines(@PathParam("processId") String processId,
                                          @QueryParam("offset") String offsetStr,
                                          @QueryParam("limit") String limitStr) {
        return getProcessLogLinesHelper(processId, offsetStr, limitStr, false);
    }

    @GET
    @Path("{processId}/log/err/lines")
    public Response getProcessLogErrLines(@PathParam("processId") String processId,
                                          @QueryParam("offset") String offsetStr,
                                          @QueryParam("limit") String limitStr) {
        return getProcessLogLinesHelper(processId, offsetStr, limitStr, true);
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
        if (!killed) {
            return APIRestUtilities.notFound("Process JVM not found [%s]", pid);
        } else {
            return APIRestUtilities.ok("Process JVM Killed [%s]", pid);
        }
    }

    private Response getProcessLogHelper(String processId, String fileName, boolean err) {
        InputStream logStream = forManagerService.getProcessLog(processId, err);
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .build();
    }

    private Response getProcessLogLinesHelper(String processId, String offsetStr,String limitStr, boolean err) {
        JSONObject result = new JSONObject();
        result.put("totalSize", forManagerService.getProcessLogSize(processId, err));
        JSONArray linesJson = new JSONArray();
        List<String> lines = forManagerService.getProcessLogLines(processId, err,
                forManagerService.getLogOffset(offsetStr), forManagerService.getLogLimit(limitStr));
        for (String line : lines) {
            linesJson.put(line);
        }
        result.put("lines", linesJson);
        return APIRestUtilities.jsonPayload(result.toString());
    }


}
