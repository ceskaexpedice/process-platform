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
package org.ceskaexpedice.processplatform.manager.db.dao.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PluginMapper
 * @author ppodsednik
 */
public final class PluginMapper {
    private PluginMapper(){}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void mapPlugin(PreparedStatement stmt, PluginEntity plugin, Connection conn) throws SQLException, JsonProcessingException {
        stmt.setString(1, plugin.getPluginId());
        stmt.setString(2, plugin.getDescription());
        stmt.setString(3, plugin.getMainClass());

        String json = mapper.writeValueAsString(plugin.getPayloadFieldSpecMap());
        stmt.setString(4, json);
        if (plugin.getScheduledProfiles() != null) {
            Array scheduledProfilesArray = conn.createArrayOf("text", plugin.getScheduledProfiles().toArray());
            stmt.setArray(5, scheduledProfilesArray);
        } else {
            Array scheduledProfilesArray = conn.createArrayOf("text", new String[0]);
            stmt.setArray(5, scheduledProfilesArray);

        }
    }

    public static PluginEntity mapPlugin(ResultSet rsPlugin) {
        try {
            PluginEntity plugin = new PluginEntity();
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

            return plugin;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
