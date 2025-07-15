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

import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfileEndpoint {

    private final ProfileService profileService;

    public ProfileEndpoint(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GET
    @Path("/{profileId}")
    public Response getProfile(@PathParam("profileId") String profileId) {
        PluginProfile profile = profileService.getProfile(profileId);
        return Response.ok(profile).build();
    }

    @GET
    @Path("/profiles")
    public Response getProfiles() {
        List<PluginProfile> allProfiles = profileService.getProfiles();
        return Response.ok(allProfiles).build();
    }

    @GET
    @Path("/plugin_profiles")
    public Response getPluginProfiles(@QueryParam("pluginId") String pluginId) {
        List<PluginProfile> allProfiles = profileService.getProfiles(pluginId);
        return Response.ok(allProfiles).build();
    }

    @PUT
    @Path("profile/{profileId}")
    public Response updateProfile(@PathParam("profileId") String profileId, PluginProfile profile) {
        profileService.updateProfile(profile);
        return Response.ok().build();
    }

}
