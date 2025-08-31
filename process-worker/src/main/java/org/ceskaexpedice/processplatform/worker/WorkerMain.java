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
package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.NodeType;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.client.ManagerClientFactory;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * WorkerMain
 * @author ppodsednik
 */
public class WorkerMain {
    public static final Logger LOGGER = Logger.getLogger(WorkerMain.class.getName());

    private WorkerLoop workerLoop;
    private WorkerConfiguration workerConfiguration;
    private ManagerClient managerClient;

    public WorkerMain() {
    }

    public void initialize(WorkerConfiguration workerConfiguration) {
        LOGGER.info("Initializing...");
        this.workerConfiguration = workerConfiguration;
        managerClient = ManagerClientFactory.createManagerClient(workerConfiguration);
        List<PluginInfo> pluginsList = scanPlugins();
        if (pluginsList.isEmpty()) {
            throw new ApplicationException("No plugins found");
        }
        registerNode(pluginsList);
        registerPlugins(pluginsList);
        this.workerLoop = new WorkerLoop(workerConfiguration, managerClient);
        workerLoop.start();
        LOGGER.info("Initialized");
    }

    private List<PluginInfo> scanPlugins() {
        File pluginsDir = workerConfiguration.getPluginDirectory();
        LOGGER.info(String.format("Plugin folder is [%s]", pluginsDir));
        List<PluginInfo> pluginsList = PluginsLoader.load(pluginsDir);
        return pluginsList;
    }

    private void registerNode(List<PluginInfo> pluginsList) {
        LOGGER.info(String.format("Register node [%s]", workerConfiguration.getWorkerId()));
        Set<String> tags = new HashSet<>();
        for (PluginInfo pluginInfo : pluginsList) {
            for (PluginProfile pluginProfile: pluginInfo.getProfiles()){
                tags.add(pluginProfile.getProfileId());
            }
        }
        Node node = new Node();
        node.setNodeId(workerConfiguration.getWorkerId());
        node.setDescription(String.format("Worker [%s]", workerConfiguration.getWorkerId()));
        node.setType(NodeType.WORKER);
        node.setUrl(workerConfiguration.getWorkerBaseUrl());
        node.setTags(tags);

        managerClient.registerNode(node);
    }

    private void registerPlugins(List<PluginInfo> pluginsList) {
        for (PluginInfo pluginInfo : pluginsList) {
            LOGGER.info("Register plugin: " + pluginInfo.getPluginId());
            managerClient.registerPlugin(pluginInfo);
        }
    }

    public void shutdown() {
        workerLoop.stop();
    }


}