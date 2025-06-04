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

import java.util.List;

public class PluginProfileService {
    
    public List<PluginProfileTO> getAllProfiles() {
        return null;
    }

    public PluginProfileTO getProfile(String profileId) {
        return null;
    }

    public void createProfile(PluginProfileTO profile) {
    }

    public void updateProfile(String profileId, PluginProfileTO profile) {
    }

    public void deleteProfile(String profileId) {
    }

    public void registerPluginInfo(PluginInfoTO pluginInfoDTO) {
        for (PluginProfileTO profile : pluginInfoDTO.getProfiles()) {
            upsertProfile(profile); // insert or update logic
        }
    }

    public void upsertProfile(PluginProfileTO profileDTO) {
        // checks: does this profile already exist?
        // yes → update; no → insert.
    }

    // other CRUD methods
}
