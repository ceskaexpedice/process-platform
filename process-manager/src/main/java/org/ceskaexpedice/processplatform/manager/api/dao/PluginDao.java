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
import org.ceskaexpedice.processplatform.common.entity.ProcessState;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginDao {

    private static final Logger LOGGER = Logger.getLogger(PluginDao.class.getName());
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;

    public PluginDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
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
                    return false;
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
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PLUGIN_ID = ?", pluginId);
            return profiles;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public PluginInfo getPlugin(String pluginId, List<PluginProfile> pluginProfiles) {
        try (Connection connection = getConnection()) {
            List<PluginInfo> processes = new JDBCQueryTemplate<PluginInfo>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginInfo> returnsList) throws SQLException {
                    PluginInfo pluginProfile = PluginMapper.mapPluginInfo(rs, pluginProfiles);
                    returnsList.add(pluginProfile);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PLUGIN p  where PLUGIN_ID = ?", pluginId);
            return processes.size() == 1 ? processes.get(0) : null;
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
