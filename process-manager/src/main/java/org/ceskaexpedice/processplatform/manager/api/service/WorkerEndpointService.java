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
package org.ceskaexpedice.processplatform.manager.api.service;

import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.PluginProfileTO;

/**
 * WorkerService
 * @author ppodsednik
 */
public class WorkerEndpointService {

    public void registerPlugin(PluginInfoTO pluginInfoTO) {
        // Save plugin to 'plugins' table
        // For example:
        // pluginRepository.save(new PluginEntity(pluginInfoDto.getPluginId(), pluginInfoDto.getDescription(), pluginInfoDto.getMainClass()));

        // Save profiles to 'plugin_profiles' table
        for (PluginProfileTO profile : pluginInfoTO.getProfiles()) {
            // pluginProfilesRepository.save(new PluginProfileEntity(profile.getProfileId(), profile.getPluginId(), ...));
        }
    }

}
