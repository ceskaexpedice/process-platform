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

import org.ceskaexpedice.processplatform.manager.db.entity.NodeEntity;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * NodeMapper
 * @author ppodsednik
 */
public final class NodeMapper {
    private NodeMapper(){}

    public static void mapNodeForUpdate(PreparedStatement stmt, NodeEntity nodeEntity, Connection conn) throws SQLException {
        // 1. description
        if (nodeEntity.getDescription() != null) {
            stmt.setString(1, nodeEntity.getDescription());
        } else {
            stmt.setNull(1, java.sql.Types.VARCHAR);
        }

        // 2. type
        if (nodeEntity.getType() != null) {
            stmt.setString(2, nodeEntity.getType());
        } else {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        }

        // 3. url
        if (nodeEntity.getUrl() != null) {
            stmt.setString(3, nodeEntity.getUrl());
        } else {
            stmt.setNull(3, java.sql.Types.VARCHAR);
        }

        // 4. tags Array
        if (nodeEntity.getTags() != null && !nodeEntity.getTags().isEmpty()) {
            Array tagsArray = conn.createArrayOf("text", nodeEntity.getTags().toArray());
            stmt.setArray(4, tagsArray);
        } else {
            stmt.setNull(4, java.sql.Types.ARRAY);
        }

        // 5. WHERE node_id = ?
        stmt.setString(5, nodeEntity.getNodeId());
    }

    public static void mapNode(PreparedStatement stmt, NodeEntity nodeEntity, Connection conn) throws SQLException {
        stmt.setString(1, nodeEntity.getNodeId());
        stmt.setString(2, nodeEntity.getDescription());
        stmt.setString(3, nodeEntity.getType());
        stmt.setString(4, nodeEntity.getUrl());
        Array tagsArray = conn.createArrayOf("text", nodeEntity.getTags().toArray());
        stmt.setArray(5, tagsArray);
    }

    public static NodeEntity mapNode(ResultSet rsNode) throws SQLException {
        NodeEntity node = new NodeEntity();
        node.setNodeId(rsNode.getString("node_id"));
        node.setDescription(rsNode.getString("description"));
        node.setType(rsNode.getString("type"));
        node.setUrl(rsNode.getString("url"));

        Array array = rsNode.getArray("tags");
        Set<String> tags = new HashSet<>();
        if (array != null) {
            String[] arr = (String[]) array.getArray();
            tags = new HashSet<>(Arrays.asList(arr));
        }
        node.setTags(tags);

        return node;
    }
}
