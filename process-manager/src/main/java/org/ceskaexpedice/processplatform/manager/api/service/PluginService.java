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

import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;

import java.util.List;

/**
 * PluginService
 * @author ppodsednik
 */
public class PluginService {

    private final ManagerConfiguration managerConfiguration;
    private final PluginDao pluginDao;

    public PluginService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.pluginDao = new PluginDao(dbConnectionProvider, managerConfiguration);
    }

    public PluginProfile getProfile(String profileId) {
        List<PluginProfile> profiles = pluginDao.getProfiles(profileId);
        return profiles.get(0);
    }

    public List<PluginProfile> getProfiles() {
        return null;
    }

    public void createProfile(PluginProfile profile) {
    }

    public void updateProfile(String profileId, PluginProfile profile) {
    }

    public void deleteProfile(String profileId) {
    }

    public void registerPluginInfo(PluginInfo pluginInfoDTO) {
        for (PluginProfile profile : pluginInfoDTO.getProfiles()) {
            upsertProfile(profile); // insert or update logic
        }
    }

    public void upsertProfile(PluginProfile profileDTO) {
        // checks: does this profile already exist?
        // yes → update; no → insert.
    }

}
