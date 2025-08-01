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

import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * PluginEndpoint
 * @author petrp
 */
@Path("/plugin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PluginEndpoint {

    private final PluginService pluginService;

    public PluginEndpoint(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @GET
    @Path("/{pluginId}")
    public Response getPlugin(@PathParam("pluginId") String pluginId) {
        PluginInfo pluginInfo = pluginService.getPlugin(pluginId, true, true);
        if (pluginInfo == null) {
            return APIRestUtilities.notFound("Plugin not found: [%s]", pluginId);
        }
        return Response.ok(pluginInfo).build();
    }

    @GET
    public Response getPlugins() {
        List<PluginInfo> plugins = pluginService.getPlugins();
        return Response.ok(plugins).build();
    }

}
