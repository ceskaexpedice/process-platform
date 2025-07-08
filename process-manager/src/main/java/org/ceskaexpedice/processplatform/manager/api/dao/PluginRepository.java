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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PluginRepository {

    private static final ObjectMapper mapper = new ObjectMapper();

    public void insertPluginInfo(Connection conn, PluginInfo plugin) throws Exception {
        String sql = "INSERT INTO pcp_plugin (plugin_id, description, main_class, payload_field_spec_map) VALUES (?, ?, ?, ?::jsonb)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plugin.getPluginId());
            stmt.setString(2, plugin.getDescription());
            stmt.setString(3, plugin.getMainClass());

            String json = mapper.writeValueAsString(plugin.getPayloadFieldSpecMap());
            stmt.setString(4, json);

            Array scheduledProfilesArray = conn.createArrayOf("text", plugin.getScheduledProfiles().toArray());
            stmt.setArray(5, scheduledProfilesArray);

            stmt.executeUpdate();
        }
    }

    /*
    public void insertPluginProfile(Connection conn, PluginProfile profile) throws SQLException {
        String sql = "INSERT INTO pcp_profile (profile_id, plugin_id, jvm_args) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getProfileId());
            stmt.setString(2, profile.getPluginId());

            Array jvmArgsArray = conn.createArrayOf("text", profile.getJvmArgs().toArray());
            stmt.setArray(3, jvmArgsArray);

            stmt.executeUpdate();
        }
    }

     */
}
