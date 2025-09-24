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

import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ProfileMapper
 * @author ppodsednik
 */
public final class ProfileMapper {
    private ProfileMapper(){}

    public static void mapPluginProfile(PreparedStatement stmt, PluginProfileEntity profile, Connection conn) throws SQLException {
        stmt.setString(1, profile.getProfileId());
        stmt.setString(2, profile.getDescription());
        stmt.setString(3, profile.getPluginId());
        if (profile.getJvmArgs() != null) {
            Array jvmArgsArray = conn.createArrayOf("text", profile.getJvmArgs().toArray());
            stmt.setArray(4, jvmArgsArray);
        } else {
            stmt.setNull(4, Types.ARRAY);
        }
    }

    public static PluginProfileEntity mapPluginProfile(ResultSet rsProfile) throws SQLException {
        PluginProfileEntity profile = new PluginProfileEntity();
        profile.setProfileId(rsProfile.getString("profile_id"));
        profile.setDescription(rsProfile.getString("description"));
        profile.setPluginId(rsProfile.getString("plugin_id"));

        Array array = rsProfile.getArray("jvm_args");
        profile.setJvmArgs(array != null ? Arrays.asList((String[]) array.getArray()) : new ArrayList<>());

        return profile;
    }
}
