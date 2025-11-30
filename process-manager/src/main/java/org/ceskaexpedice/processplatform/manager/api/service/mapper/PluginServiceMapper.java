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

import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * PluginServiceMapper
 * @author ppodsednik
 */
public final class PluginServiceMapper {
    private PluginServiceMapper(){}

    public static PluginInfo mapPlugin(PluginEntity pluginEntity) {
        if(pluginEntity == null) return null;
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(pluginEntity.getPluginId());
        if (pluginEntity.getScheduledProfiles() != null) {
            pluginInfo.setScheduledProfiles(pluginEntity.getScheduledProfiles());
        } else {
            pluginInfo.setScheduledProfiles(new HashSet<>());
        }
        pluginInfo.setDescription(pluginEntity.getDescription());
        pluginInfo.setMainClass(pluginEntity.getMainClass());
        pluginInfo.setPayloadFieldSpecMap(pluginEntity.getPayloadFieldSpecMap());
        return pluginInfo;
    }

    public static PluginEntity mapPlugin(PluginInfo pluginInfo) {
        if(pluginInfo == null) return null;
        PluginEntity pluginEntity = new PluginEntity();
        pluginEntity.setPluginId(pluginInfo.getPluginId());
        if (pluginInfo.getScheduledProfiles() != null) {
            pluginEntity.setScheduledProfiles(pluginInfo.getScheduledProfiles());
        } else {
            pluginInfo.setScheduledProfiles(new HashSet<>());
        }
        pluginEntity.setDescription(pluginInfo.getDescription());
        pluginEntity.setMainClass(pluginInfo.getMainClass());
        pluginEntity.setPayloadFieldSpecMap(pluginInfo.getPayloadFieldSpecMap());
        return pluginEntity;
    }

    public static List<PluginInfo> mapPlugins(List<PluginEntity> pluginEntities) {
        if(pluginEntities == null) return null;
        List<PluginInfo> plugins = new ArrayList<>();
        for (PluginEntity pluginEntity : pluginEntities) {
            PluginInfo pluginInfo = PluginServiceMapper.mapPlugin(pluginEntity);
            plugins.add(pluginInfo);
        }
        return plugins;
    }
}
