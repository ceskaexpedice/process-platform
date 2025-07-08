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
package org.ceskaexpedice.processplatform.manager.api.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.entity.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;

import java.sql.*;
import java.util.*;

class PluginMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static PluginInfo mapPluginInfo(ResultSet rsPlugin, List<PluginProfile> profiles) {
        try {
            PluginInfo plugin = new PluginInfo();
            plugin.setPluginId(rsPlugin.getString("plugin_id"));
            plugin.setDescription(rsPlugin.getString("description"));
            plugin.setMainClass(rsPlugin.getString("main_class"));

            String json = rsPlugin.getString("payload_field_spec_map");
            if(json != null){
                Map<String, PayloadFieldSpec> specMap = mapper.readValue(json, new TypeReference<>() {});
                plugin.setPayloadFieldSpecMap(specMap);
            }

            Array array = rsPlugin.getArray("scheduled_profiles");
            Set<String> scheduledProfiles = new HashSet<>();
            if (array != null) {
                String[] arr = (String[]) array.getArray();
                scheduledProfiles = new HashSet<>(Arrays.asList(arr));
            }
            plugin.setScheduledProfiles(scheduledProfiles);

            plugin.setProfiles(profiles);
            return plugin;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

    static PluginProfile mapPluginProfile(ResultSet rsProfile) throws SQLException {
        PluginProfile profile = new PluginProfile();
        profile.setProfileId(rsProfile.getString("profile_id"));
        profile.setPluginId(rsProfile.getString("plugin_id"));

        Array array = rsProfile.getArray("jvm_args");
        profile.setJvmArgs(array != null ? Arrays.asList((String[]) array.getArray()) : new ArrayList<>());

        return profile;
    }
}
