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

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.PluginServiceMapper;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProfileServiceMapper;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.dao.PluginDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProfileDao;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.util.*;
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

    public PluginInfo getPlugin(String pluginId, boolean includeProfiles, boolean scheduledProfilesRecursive) {
        PluginEntity pluginEntity = pluginDao.getPlugin(pluginId);
        PluginInfo pluginInfo = PluginServiceMapper.mapPlugin(pluginEntity);
        if (pluginInfo == null) {
            return null;
        }
        if (!includeProfiles) {
            return pluginInfo;
        }
        List<PluginProfile> profiles = ProfileServiceMapper.mapProfiles(profileDao.getProfiles(pluginId));
        pluginInfo.setProfiles(profiles);
        if (!scheduledProfilesRecursive) {
            return pluginInfo;
        }

        // get all scheduled profiles recursive
        Set<String> visitedPlugins = new HashSet<>();
        Set<String> resultProfiles = new LinkedHashSet<>();
        collectScheduledProfilesRecursive(pluginEntity, visitedPlugins, resultProfiles);
        pluginInfo.setScheduledProfiles(resultProfiles);
        return pluginInfo;
    }

    public List<PluginInfo> getPlugins() {
        List<PluginInfo> plugins = PluginServiceMapper.mapPlugins(pluginDao.getPlugins());
        return plugins;
    }

    public void validatePayload(String pluginId, Map<String, String> payload) {
        // TODO add validation on type
        PluginInfo plugin = getPlugin(pluginId, false, false);
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
        LOGGER.info(String.format("Register plugin [%s]", pluginInfo.getPluginId()));
        PluginInfo pluginExisting = getPlugin(pluginInfo.getPluginId(), false, false);
        if (pluginExisting != null) {
            LOGGER.info("Plugin [" + pluginInfo.getPluginId() + "] already registered");
            return;
        }
        pluginDao.createPlugin(PluginServiceMapper.mapPlugin(pluginInfo));
        for (PluginProfile profile : pluginInfo.getProfiles()) {
            profileDao.createProfile(ProfileServiceMapper.mapProfile(profile));
        }
    }

    private void collectScheduledProfilesRecursive(PluginEntity pluginEntity, Set<String> visitedPlugins, Set<String> resultProfiles) {
        if (pluginEntity == null) {
            return;
        }
        // avoid loops
        if (!visitedPlugins.add(pluginEntity.getPluginId())) {
            return;
        }
        Set<String> scheduledProfiles = pluginEntity.getScheduledProfiles();
        if (scheduledProfiles == null) {
            return;
        }
        for (String profileId : scheduledProfiles) {
            resultProfiles.add(profileId);
            PluginProfileEntity profileEntity = profileDao.getProfile(profileId);
            if (profileEntity != null) {
                PluginEntity nextPlugin = pluginDao.getPlugin(profileEntity.getPluginId());
                collectScheduledProfilesRecursive(nextPlugin, visitedPlugins, resultProfiles);
            }
        }
    }

}
