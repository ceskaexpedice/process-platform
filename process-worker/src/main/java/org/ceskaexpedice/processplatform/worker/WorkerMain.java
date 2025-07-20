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
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.client.ManagerClientFactory;
import org.ceskaexpedice.processplatform.worker.config.EffectiveWorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        registerNode();
        registerPlugins();
        this.workerLoop = new WorkerLoop(workerConfiguration, managerClient);
        workerLoop.start();
        LOGGER.info("Initialized");
    }

    private List<PluginInfo> scanPlugins() {
        File pluginsDir = new EffectiveWorkerConfiguration(workerConfiguration).getPluginDirectory();
        LOGGER.info(String.format("Plugin folder is %s", pluginsDir));
        List<PluginInfo> pluginsList = PluginsLoader.load(pluginsDir);
        return pluginsList;
    }

    private void registerNode() {
        // TODO
        Node node = new Node();
        node.setNodeId(workerConfiguration.get(WorkerConfiguration.WORKER_ID_KEY));
        node.setDescription("????");
        node.setType(NodeType.worker);
        node.setUrl(workerConfiguration.get(WorkerConfiguration.WORKER_BASE_URL_KEY));
        Set<String> tags = Arrays.stream(workerConfiguration.get(WorkerConfiguration.WORKER_PROFILES_KEY).split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        node.setTags(tags);
        managerClient.registerNode(node);
    }

    private void registerPlugins() {
        List<PluginInfo> pluginsList = scanPlugins();
        if (pluginsList.isEmpty()) {
            throw new ApplicationException("No plugins found");
        }
        for (PluginInfo pluginInfo : pluginsList) {
            LOGGER.info("Discovered plugin: " + pluginInfo.getPluginId());
            managerClient.registerPlugin(pluginInfo);
        }
    }

    public void shutdown() {
        workerLoop.stop();
    }


}