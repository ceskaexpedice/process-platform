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

import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/plugin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PluginEndpoint {

    private final PluginService pluginService;

    public PluginEndpoint(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    // ------------- plugins ---------------------------

    @GET
    @Path("/{pluginId}")
    public Response getPlugin(@PathParam("pluginId") String pluginId) {
        PluginInfo pluginInfo = pluginService.getPlugin(pluginId);
        if (pluginInfo == null) {
            return APIRestUtilities.notFound("Plugin not found: %s", pluginId);
        }
        return Response.ok(pluginInfo).build();
    }

    @GET
    public Response getPlugins(@QueryParam("withProfiles") boolean withProfiles) {
        List<PluginInfo> plugins = pluginService.getPlugins(withProfiles);
        return Response.ok(plugins).build();
    }

    @POST
    @Path("validate_payload/{pluginId}")
    public Response validatePayload(@PathParam("pluginId") String pluginId, Map<String, String> payload) {
        pluginService.validatePayload(pluginId, payload);
        return Response.ok().build();
    }

    // ------------- profiles ---------------------------

    @GET
    @Path("profile/{profileId}")
    public Response getProfile(@PathParam("profileId") String profileId) {
        PluginProfile profile = pluginService.getProfile(profileId);
        return Response.ok(profile).build();
    }

    @GET
    @Path("/profiles")
    public Response getProfiles() {
        List<PluginProfile> allProfiles = pluginService.getProfiles();
        return Response.ok(allProfiles).build();
    }

    @GET
    @Path("/plugin_profiles")
    public Response getPluginProfiles(@QueryParam("pluginId") String pluginId) {
        List<PluginProfile> allProfiles = pluginService.getProfiles(pluginId);
        return Response.ok(allProfiles).build();
    }

    @POST
    @Path("/profile")
    public Response createProfile(PluginProfile profile) {
        pluginService.createProfile(profile);
        return Response.ok().build();
    }

    @PUT
    @Path("profile/{profileId}")
    public Response updateProfile(@PathParam("profileId") String profileId, PluginProfile profile) {
        pluginService.updateProfile(profileId, profile);
        return Response.ok().build();
    }

    @DELETE
    @Path("profile/{profileId}")
    public Response deleteProfile(@PathParam("profileId") String profileId) {
        pluginService.deleteProfile(profileId);
        return Response.ok().build();
    }


}
