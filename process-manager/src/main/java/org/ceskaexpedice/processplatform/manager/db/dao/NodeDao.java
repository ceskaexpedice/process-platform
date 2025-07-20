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
import org.ceskaexpedice.processplatform.manager.db.dao.mapper.NodeMapper;
import org.ceskaexpedice.processplatform.manager.db.entity.NodeEntity;

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

    public void createNode(NodeEntity nodeEntity) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_node (node_id, description, type, url, tags) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                NodeMapper.mapNode(stmt, nodeEntity, connection);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public NodeEntity getNode(String nodeId) {
        try (Connection connection = getConnection()) {
            List<NodeEntity> nodes = new JDBCQueryTemplate<NodeEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<NodeEntity> returnsList) throws SQLException {
                    NodeEntity nodeEntity = NodeMapper.mapNode(rs);
                    returnsList.add(nodeEntity);
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_NODE n  where NODE_ID = ?", nodeId);
            return nodes.size() == 1 ? nodes.get(0) : null;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<NodeEntity> getNodes() {
        try (Connection connection = getConnection()) {
            List<NodeEntity> nodes = new JDBCQueryTemplate<NodeEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<NodeEntity> returnsList) throws SQLException {
                    NodeEntity nodeEntity = NodeMapper.mapNode(rs);
                    returnsList.add(nodeEntity);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_NODE");
            return nodes;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    // TODO
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
