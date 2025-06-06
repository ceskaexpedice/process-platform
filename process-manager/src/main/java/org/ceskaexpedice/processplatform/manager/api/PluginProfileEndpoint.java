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

import org.ceskaexpedice.processplatform.common.to.PluginProfileTO;
import org.ceskaexpedice.processplatform.manager.api.service.PluginProfileService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/admin/plugin-profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PluginProfileEndpoint {
    /*
    /pluginProfiles/{pluginId}/{profileType}/payloadSpec → returns JSON spec
    /pluginProfiles/{pluginId}/{profileType}/validatePayload → accepts payload, returns 200/400
     */

    private final PluginProfileService pluginProfileService;

    public PluginProfileEndpoint(PluginProfileService pluginProfileService) {
        this.pluginProfileService = pluginProfileService;
    }

    @GET
    public List<PluginProfileTO> getAllProfiles() {
        return pluginProfileService.getAllProfiles();
    }

    @GET
    @Path("/{profileId}")
    public PluginProfileTO getProfile(@PathParam("profileId") String profileId) {
        return pluginProfileService.getProfile(profileId);
    }

    @POST
    public void createProfile(PluginProfileTO profile) {
        pluginProfileService.createProfile(profile);
    }

    @PUT
    @Path("/{profileId}")
    public void updateProfile(@PathParam("profileId") String profileId, PluginProfileTO profile) {
        pluginProfileService.updateProfile(profileId, profile);
    }

    @DELETE
    @Path("/{profileId}")
    public void deleteProfile(@PathParam("profileId") String profileId) {
        pluginProfileService.deleteProfile(profileId);
    }
}
