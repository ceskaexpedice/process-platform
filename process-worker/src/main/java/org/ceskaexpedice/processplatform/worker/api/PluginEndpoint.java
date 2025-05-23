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

import org.ceskaexpedice.processplatform.worker.api.service.ManagerEndpointService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/plugin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PluginEndpoint {

    public PluginEndpoint(ManagerEndpointService managerEndpointService) {
    }

    // === Get Process Logs (stdout or stderr) ===
    @GET
    @Path("/logs/{uuid}")
    public Response getLogs(
            @PathParam("uuid") String uuid,
            @QueryParam("stream") @DefaultValue("stdout") String streamType) {
        // streamType can be "stdout" or "stderr"
        // You can return logs as plain text or application/octet-stream if binary
        return Response.ok(/* logs as text or file */).entity("{\"uuid\":\"" + uuid + "\" }").build();
    }

}
