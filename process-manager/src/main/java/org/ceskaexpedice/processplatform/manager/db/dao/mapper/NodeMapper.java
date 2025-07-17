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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NodeMapper {

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
