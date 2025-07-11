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
package org.ceskaexpedice.processplatform.manager.service;

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.entity.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.manager.db.dao.PluginDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProfileDao;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * PluginService
 * @author ppodsednik
 */
public class PluginService {
    private static final Logger LOGGER = Logger.getLogger(PluginService.class.getName());

    private final ManagerConfiguration managerConfiguration;
    private final PluginDao pluginDao;
    private final ProfileDao profileDao;

    public PluginService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.pluginDao = new PluginDao(dbConnectionProvider, managerConfiguration);
        this.profileDao = new ProfileDao(dbConnectionProvider, managerConfiguration);
    }

    // ------ plugins ----------

    public PluginInfo getPlugin(String pluginId) {
        // TODO get all scheduled processes hierarchically
        PluginEntity pluginEntity = pluginDao.getPlugin(pluginId);
        PluginInfo pluginInfo = PluginServiceMapper.mapPlugin(pluginEntity);
        if(pluginInfo == null) return null;

        List<PluginProfile> profiles = PluginServiceMapper.mapProfiles(profileDao.getProfiles(pluginId));
        pluginInfo.setProfiles(profiles);
        return pluginInfo;
    }

    public List<PluginInfo> getPlugins() {
        List<PluginInfo> plugins = PluginServiceMapper.mapPlugins(pluginDao.getPlugins());
        return plugins;
    }

    public void validatePayload(String pluginId, Map<String, String> payload) {
        // TODO validate input
        PluginInfo plugin = getPlugin(pluginId);
        for (String name : plugin.getPayloadFieldSpecMap().keySet()) {
            PayloadFieldSpec payloadFieldSpec = plugin.getPayloadFieldSpecMap().get(name);
            if (payloadFieldSpec.isRequired()) {
                if (!payload.containsKey(name)) {
                    throw new BusinessLogicException("Payload field " + name + " is missing");
                }
            }
        }
    }

    public void registerPlugin(PluginInfo pluginInfo) {
        // TODO log messages
        PluginInfo pluginExisting = getPlugin(pluginInfo.getPluginId());
        if (pluginExisting != null) {
            // already registered
            for (PluginProfile profile : pluginInfo.getProfiles()) {
                PluginProfileEntity profileExisting = profileDao.getProfile(profile.getProfileId());
                if (profileExisting == null) {
                    // let's register new profile to the existing plugin
                    PluginProfileEntity newProfile = PluginServiceMapper.mapProfile(profile);
                    profileDao.createProfile(newProfile);
                }
            }
        } else {
            pluginDao.createPlugin(PluginServiceMapper.mapPlugin(pluginInfo));
            for (PluginProfile profile : pluginInfo.getProfiles()) {
                profileDao.createProfile(PluginServiceMapper.mapProfile(profile));
            }
        }
    }

    // ------ profiles ----------

    public PluginProfile getProfile(String profileId) {
        PluginProfile profile = PluginServiceMapper.mapProfile(profileDao.getProfile(profileId));
        return profile;
    }

    public List<PluginProfile> getProfiles() {
        List<PluginProfile> profiles = PluginServiceMapper.mapProfiles(profileDao.getProfiles());
        return profiles;
    }

    public List<PluginProfile> getProfiles(String pluginId) {
        List<PluginProfile> profiles = PluginServiceMapper.mapProfiles(profileDao.getProfiles(pluginId));
        return profiles;
    }

    public void createProfile(PluginProfile profile) {
        profileDao.createProfile(PluginServiceMapper.mapProfile(profile));
    }

    public void updateProfile(PluginProfile profile) {
        profileDao.updateProfile(PluginServiceMapper.mapProfile(profile));
    }

    public void deleteProfile(String profileId) {
        profileDao.deleteProfile(profileId);
    }


}
