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
package org.ceskaexpedice.processplatform.manager.api.service;

import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.NodeServiceMapper;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.PluginServiceMapper;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProfileServiceMapper;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.entity.NodeEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;

import java.util.List;
import java.util.logging.Logger;

/**
 * PluginService
 * @author ppodsednik
 */
public class NodeService {
    private static final Logger LOGGER = Logger.getLogger(NodeService.class.getName());

    private final ManagerConfiguration managerConfiguration;
    private final NodeDao nodeDao;

    public NodeService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.nodeDao = new NodeDao(dbConnectionProvider, managerConfiguration);
    }

    public void registerNode(Node node) {
        Node nodeExisting = getNode(node.getNodeId());
        if (nodeExisting != null) {
            LOGGER.info("Node with id " + node.getNodeId() + " already registered");
            return;
        }
        nodeDao.createNode(NodeServiceMapper.mapNode(node));
    }

    public Node getNode(String nodeId) {
        NodeEntity nodeEntity = nodeDao.getNode(nodeId);
        Node node = NodeServiceMapper.mapNode(nodeEntity);
        return node;
    }

}
