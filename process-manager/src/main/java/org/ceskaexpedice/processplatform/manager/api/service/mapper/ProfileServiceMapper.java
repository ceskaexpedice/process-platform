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
package org.ceskaexpedice.processplatform.manager.api.service.mapper;

import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.util.ArrayList;
import java.util.List;

public class ProfileServiceMapper {

    public static PluginProfile mapProfile(PluginProfileEntity profileEntity) {
        if(profileEntity == null) return null;
        PluginProfile pluginProfile = new PluginProfile();
        pluginProfile.setPluginId(profileEntity.getPluginId());
        pluginProfile.setJvmArgs(profileEntity.getJvmArgs());
        pluginProfile.setProfileId(profileEntity.getProfileId());
        pluginProfile.setDescription(profileEntity.getDescription());
        return pluginProfile;
    }

    public static List<PluginProfile> mapProfiles(List<PluginProfileEntity> profileEntities) {
        if(profileEntities == null) return null;
        List<PluginProfile> profiles = new ArrayList<>();
        for (PluginProfileEntity profileEntity : profileEntities) {
            PluginProfile pluginProfile = ProfileServiceMapper.mapProfile(profileEntity);
            profiles.add(pluginProfile);
        }
        return profiles;
    }

    public static PluginProfileEntity mapProfile(PluginProfile profile) {
        if(profile == null) return null;
        PluginProfileEntity pluginProfileEntity = new PluginProfileEntity();
        pluginProfileEntity.setPluginId(profile.getPluginId());
        pluginProfileEntity.setJvmArgs(profile.getJvmArgs());
        pluginProfileEntity.setProfileId(profile.getProfileId());
        pluginProfileEntity.setDescription(profile.getDescription());
        return pluginProfileEntity;
    }

}
