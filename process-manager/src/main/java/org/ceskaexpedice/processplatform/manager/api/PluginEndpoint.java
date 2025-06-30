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

    @GET
    @Path("/{pluginId}")
    public Response getPlugin(@PathParam("pluginId") String pluginId) {
        try {
            PluginInfo pluginInfo = null;
            return Response.ok(pluginInfo).build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    public Response getPlugins() {
        try {
            List<PluginInfo> plugins = null;
            return Response.ok(plugins).build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @POST
    @Path("validatePayload/{pluginId}")
    public Response validatePayload(@PathParam("pluginId") String pluginId, Map<String, String> payload) {
        try {
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("profile/{profileId}")
    public Response getProfile(@PathParam("profileId") String profileId) {
        try {
            PluginProfile profile = pluginService.getProfile(profileId);
            return Response.ok(profile).build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @GET
    @Path("/profiles")
    public Response getProfiles() {
        try {
            List<PluginProfile> allProfiles = pluginService.getProfiles();
            return Response.ok(allProfiles).build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @POST
    @Path("/profile")
    public Response createProfile(PluginProfile profile) {
        try {
            pluginService.createProfile(profile);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @PUT
    @Path("profile/{profileId}")
    public Response updateProfile(@PathParam("profileId") String profileId, PluginProfile profile) {
        try {
            pluginService.updateProfile(profileId, profile);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

    @DELETE
    @Path("profile/{profileId}")
    public Response deleteProfile(@PathParam("profileId") String profileId) {
        try {
            pluginService.deleteProfile(profileId);
            return Response.ok().build();
        } catch (Exception e) {
            return APIRestUtilities.exceptionToErrorResponse(e);
        }
    }

}
