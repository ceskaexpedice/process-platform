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
package org.ceskaexpedice.processplatform.manager.api.service.mapper;

import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.NodeType;
import org.ceskaexpedice.processplatform.manager.db.entity.NodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * NodeServiceMapper
 * @author ppodsednik
 */
public final class NodeServiceMapper {
    private NodeServiceMapper(){}

    public static Node mapNode(NodeEntity nodeEntity) {
        if(nodeEntity == null) return null;
        Node node = new Node();
        node.setNodeId(nodeEntity.getNodeId());
        node.setDescription(nodeEntity.getDescription());
        node.setType(NodeType.valueOf(nodeEntity.getType()));
        node.setUrl(nodeEntity.getUrl());
        node.setTags(nodeEntity.getTags());
        return node;
    }

    public static NodeEntity mapNode(Node node) {
        if(node == null) return null;
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setNodeId(node.getNodeId());
        nodeEntity.setDescription(node.getDescription());
        nodeEntity.setType(node.getType().name());
        nodeEntity.setUrl(node.getUrl());
        nodeEntity.setTags(node.getTags());
        return nodeEntity;
    }

    public static List<Node> mapNode(List<NodeEntity> nodeEntities) {
        if(nodeEntities == null) return null;
        List<Node> nodes = new ArrayList<>();
        for (NodeEntity nodeEntity : nodeEntities) {
            Node node = mapNode(nodeEntity);
            nodes.add(node);
        }
        return nodes;
    }

}
