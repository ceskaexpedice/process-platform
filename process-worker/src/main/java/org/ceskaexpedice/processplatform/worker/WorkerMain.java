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

import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.entity.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.entity.PluginInfoMapper;
import org.ceskaexpedice.processplatform.worker.plugin.utils.PluginsLoader;

import java.io.File;
import java.util.List;

/**
 * WorkerMain
 * @author ppodsednik
 */
public class WorkerMain {

    private WorkerConfiguration workerConfiguration;
    private WorkerLoop workerLoop;
    private ManagerClient managerClient;

    public WorkerMain() {
    }

    public void initialize(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
        List<PluginInfo> pluginsList = scanPlugins();
        if(pluginsList.isEmpty()){
            throw new IllegalStateException("No plugins found");
        }
        registerPlugins(pluginsList);
        this.managerClient = new ManagerClient(workerConfiguration);
        this.workerLoop = new WorkerLoop(managerClient);
        workerLoop.start();
    }

    private List<PluginInfo> scanPlugins() {
        File pluginsDir = new File("plugins"); // TODO from config
        List<PluginInfo> pluginsList = PluginsLoader.load(pluginsDir);
        return pluginsList;
    }

    private void registerPlugins(List<PluginInfo> pluginsList) {
        for (PluginInfo pluginInfo : pluginsList) {
            System.out.println("Discovered plugin: " + pluginInfo);
            PluginInfoTO pluginInfoTO = PluginInfoMapper.toTO(pluginInfo);
            managerClient.registerPlugin(pluginInfoTO);
        }
    }

    public void shutdown() {
        workerLoop.stop();
    }
}