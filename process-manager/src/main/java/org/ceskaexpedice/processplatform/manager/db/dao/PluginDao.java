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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.*;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class PluginDao {

    private static final Logger LOGGER = Logger.getLogger(PluginDao.class.getName());
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;
    private static final ObjectMapper mapper = new ObjectMapper();

    public PluginDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        this.dbConnectionProvider = dbConnectionProvider;
        this.managerConfiguration = managerConfiguration;
    }

    public PluginEntity getPlugin(String pluginId) {
        try (Connection connection = getConnection()) {
            List<PluginEntity> plugins = new JDBCQueryTemplate<PluginEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginEntity> returnsList) {
                    PluginEntity pluginEntity = PluginMapper.mapPlugin(rs);
                    returnsList.add(pluginEntity);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PLUGIN p  where PLUGIN_ID = ?", pluginId);
            return plugins.size() == 1 ? plugins.get(0) : null;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<PluginEntity> getPlugins() {
        try (Connection connection = getConnection()) {
            List<PluginEntity> plugins = new JDBCQueryTemplate<PluginEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginEntity> returnsList) {
                    PluginEntity pluginEntity = PluginMapper.mapPlugin(rs);
                    returnsList.add(pluginEntity);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PLUGIN p");
            return plugins;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void createPlugin(PluginEntity plugin) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_plugin (plugin_id, description, main_class, payload_field_spec_map, scheduled_profiles) VALUES (?, ?, ?, ?::jsonb, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, plugin.getPluginId());
                stmt.setString(2, plugin.getDescription());
                stmt.setString(3, plugin.getMainClass());

                String json = mapper.writeValueAsString(plugin.getPayloadFieldSpecMap());
                stmt.setString(4, json);

                Array scheduledProfilesArray = connection.createArrayOf("text", plugin.getScheduledProfiles().toArray());
                stmt.setArray(5, scheduledProfilesArray);

                stmt.executeUpdate();
            } catch (JsonProcessingException e) {
                throw new ApplicationException(e.toString(), e);
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
