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
package org.ceskaexpedice.processplatform.manager.api.dao;

import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.DbUtils;
import org.ceskaexpedice.processplatform.manager.db.JDBCQueryTemplate;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class ProfileDao {

    private static final Logger LOGGER = Logger.getLogger(ProfileDao.class.getName());
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;

    public ProfileDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        this.dbConnectionProvider = dbConnectionProvider;
        this.managerConfiguration = managerConfiguration;
    }

    public PluginProfile getProfile(String profileId) {
        try (Connection connection = getConnection()) {
            List<PluginProfile> profiles = new JDBCQueryTemplate<PluginProfile>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfile> returnsList) throws SQLException {
                    PluginProfile pluginProfile = PluginMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PROFILE_ID = ?", profileId);
            return profiles.size() == 1 ? profiles.get(0) : null;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<PluginProfile> getProfiles() {
        try (Connection connection = getConnection()) {
            List<PluginProfile> profiles = new JDBCQueryTemplate<PluginProfile>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfile> returnsList) throws SQLException {
                    PluginProfile pluginProfile = PluginMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p");
            return profiles;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<PluginProfile> getProfiles(String pluginId) {
        try (Connection connection = getConnection()) {
            List<PluginProfile> profiles = new JDBCQueryTemplate<PluginProfile>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfile> returnsList) throws SQLException {
                    PluginProfile pluginProfile = PluginMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PLUGIN_ID = ?", pluginId);
            return profiles;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void createProfile(PluginProfile profile) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_profile (profile_id, plugin_id, jvm_args) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, profile.getProfileId());
                stmt.setString(2, profile.getPluginId());
                if (profile.getJvmArgs() != null) {
                    Array jvmArgsArray = connection.createArrayOf("text", profile.getJvmArgs().toArray());
                    stmt.setArray(3, jvmArgsArray);
                } else {
                    stmt.setNull(3, Types.ARRAY);
                }
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }

    }

    public void updateProfile(PluginProfile profile) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_profile SET jvm_args = ? WHERE profile_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                if (profile.getJvmArgs() != null) {
                    Array jvmArgsArray = connection.createArrayOf("text", profile.getJvmArgs().toArray());
                    stmt.setArray(1, jvmArgsArray);
                } else {
                    stmt.setNull(1, Types.ARRAY);
                }

                stmt.setString(2, profile.getProfileId()); // WHERE clause

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No profile found with ID: " + profile.getProfileId());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void deleteProfile(String profileId) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM pcp_profile WHERE profile_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, profileId);

                int deleted = stmt.executeUpdate();
                if (deleted == 0) {
                    throw new DataAccessException("No profile found to delete with ID: " + profileId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    private Connection getConnection() {
        Connection connection = dbConnectionProvider.get();
        if (connection == null) {
            //throw new NotReadyException("connection not ready");
        }
        try {
//            connection.setTransactionIsolation(KConfiguration.getInstance().getConfiguration().getInt("jdbcProcessTransactionIsolationLevel", Connection.TRANSACTION_READ_COMMITTED));
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            //throw new NotReadyException("connection not ready - " + e);
        }
        return connection;
    }

}
