/*
 * Copyright (C) 2012 Pavel Stastny
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
package org.ceskaexpedice.processplatform.manager.db.dao;

import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.JDBCQueryTemplate;
import org.ceskaexpedice.processplatform.manager.db.dao.mapper.ProfileMapper;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * ProfileDao
 * @author petrp
 */
public class ProfileDao extends AbstractDao{

    private static final Logger LOGGER = Logger.getLogger(ProfileDao.class.getName());

    public ProfileDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        super(dbConnectionProvider, managerConfiguration);
    }

    public PluginProfileEntity getProfile(String profileId) {
        try (Connection connection = getConnection()) {
            List<PluginProfileEntity> profiles = new JDBCQueryTemplate<PluginProfileEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfileEntity> returnsList) throws SQLException {
                    PluginProfileEntity pluginProfile = ProfileMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PROFILE_ID = ?", profileId);
            return profiles.size() == 1 ? profiles.get(0) : null;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<PluginProfileEntity> getProfiles() {
        try (Connection connection = getConnection()) {
            List<PluginProfileEntity> profiles = new JDBCQueryTemplate<PluginProfileEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfileEntity> returnsList) throws SQLException {
                    PluginProfileEntity pluginProfile = ProfileMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p");
            return profiles;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<PluginProfileEntity> getProfiles(String pluginId) {
        try (Connection connection = getConnection()) {
            List<PluginProfileEntity> profiles = new JDBCQueryTemplate<PluginProfileEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfileEntity> returnsList) throws SQLException {
                    PluginProfileEntity pluginProfile = ProfileMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PLUGIN_ID = ?", pluginId);
            return profiles;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void createProfile(PluginProfileEntity profile) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_profile (profile_id, description, plugin_id, jvm_args) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ProfileMapper.mapPluginProfile(stmt, profile, connection);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }

    }

    public void updateJvmArgs(String profileId, List<String> jvmArgs) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_profile SET jvm_args = ? WHERE profile_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                if (jvmArgs != null) {
                    Array jvmArgsArray = connection.createArrayOf("text", jvmArgs.toArray());
                    stmt.setArray(1, jvmArgsArray);
                } else {
                    stmt.setNull(1, Types.ARRAY);
                }

                stmt.setString(2, profileId);

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No profile found with ID: " + profileId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

}
