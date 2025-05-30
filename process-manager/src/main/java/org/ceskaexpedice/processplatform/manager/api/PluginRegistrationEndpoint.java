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

import org.ceskaexpedice.processplatform.common.dto.PluginInfoDto;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api/plugins")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PluginRegistrationEndpoint {

    private final PluginService pluginService;

    public PluginRegistrationEndpoint() {
        // Default constructor for Jersey (but you'd typically inject this with CDI/Spring/whatever)
        this.pluginService = new PluginService();
    }

    @POST
    @Path("/register")
    public Response registerPlugin(PluginInfoDto pluginInfoDto) {
        try {
            // Validate input
            if (pluginInfoDto.getPluginId() == null || pluginInfoDto.getPluginId().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("pluginId is required")
                        .build();
            }

            // Save plugin and profiles
            pluginService.registerPlugin(pluginInfoDto);

            return Response.ok().build();
        } catch (Exception e) {
            // Log error here (log.error...)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to register plugin: " + e.getMessage())
                    .build();
        }
    }
}
