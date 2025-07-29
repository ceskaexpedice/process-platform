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

import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProfileServiceMapper;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.dao.PluginDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProfileDao;

import java.util.List;
import java.util.logging.Logger;

/**
 * ProfileService
 * @author ppodsednik
 */
public class ProfileService {
    private static final Logger LOGGER = Logger.getLogger(ProfileService.class.getName());

    private final ManagerConfiguration managerConfiguration;
    private final PluginDao pluginDao;
    private final ProfileDao profileDao;

    public ProfileService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.pluginDao = new PluginDao(dbConnectionProvider, managerConfiguration);
        this.profileDao = new ProfileDao(dbConnectionProvider, managerConfiguration);
    }

    public PluginProfile getProfile(String profileId) {
        PluginProfile profile = ProfileServiceMapper.mapProfile(profileDao.getProfile(profileId));
        return profile;
    }

    public List<PluginProfile> getProfiles() {
        List<PluginProfile> profiles = ProfileServiceMapper.mapProfiles(profileDao.getProfiles());
        return profiles;
    }

    public void updateJvmArgs(String profileId, List<String> jvmArgs) {
        LOGGER.info(String.format("update jvm args: profileId,jvmArgs [%s, %s]", profileId, jvmArgs));
        profileDao.updateJvmArgs(profileId, jvmArgs);
    }

}
