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
import org.ceskaexpedice.processplatform.manager.db.entity.NodeEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class NodeDao {

    private static final Logger LOGGER = Logger.getLogger(NodeDao.class.getName());
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;

    public NodeDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        this.dbConnectionProvider = dbConnectionProvider;
        this.managerConfiguration = managerConfiguration;
    }

    public NodeEntity getNode(String nodeId) {
        try (Connection connection = getConnection()) {
            // TODO
           /*
            List<PluginProfileEntity> profiles = new JDBCQueryTemplate<PluginProfileEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfileEntity> returnsList) throws SQLException {
                    PluginProfileEntity pluginProfile = ProfileMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PROFILE_ID = ?", profileId);
            return profiles.size() == 1 ? profiles.get(0) : null;

            */
            return null;
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
