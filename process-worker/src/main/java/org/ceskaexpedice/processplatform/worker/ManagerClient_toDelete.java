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
package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.common.dto.PluginInfoDto;
import org.ceskaexpedice.processplatform.common.dto.ScheduledProcessDto;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ManagerClient_toDelete {
    private final String managerBaseUrl;
    private final Client client;

    public ManagerClient_toDelete(String managerBaseUrl) {
        this.managerBaseUrl = managerBaseUrl;
        this.client = ClientBuilder.newClient();
    }

    public void registerPlugin(PluginInfoDto pluginInfoDto) {
        Response response = client
                .target(managerBaseUrl)
                .path("/plugins/register")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pluginInfoDto));

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to register plugin: " + response.getStatus());
        }
    }

    public ScheduledProcessDto getNextProcess() {
        Response response = client
                .target(managerBaseUrl)
                .path("/processes/next")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(ScheduledProcessDto.class);
        } else if (response.getStatus() == 204) {
            return null;
        } else {
            throw new RuntimeException("Failed to get next process: " + response.getStatus());
        }
    }
}
