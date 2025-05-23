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

import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.PluginJvmLauncher;
import org.ceskaexpedice.processplatform.worker.plugin.PluginLoader;

import java.io.File;
import java.util.List;

public class WorkerMain {

    private WorkerLoop workerLoop;
    private WorkerConfiguration workerConfiguration;
    private List<PluginInfo> pluginsList;

    public WorkerMain() {
    }

    public void initialize(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
        pluginsList = scanPlugins();
        if(!pluginsList.isEmpty()){
            registerPlugins();
        }
        this.workerLoop = new WorkerLoop(new ManagerClient(), new PluginJvmLauncher());
        workerLoop.start();
    }

    private List<PluginInfo> scanPlugins() {
        File pluginsDir = new File("plugins"); // TODO from config
        List<PluginInfo> pluginsList = PluginLoader.scanPlugins(pluginsDir);
        return pluginsList;
    }

    private void registerPlugins() {
        File pluginsDir = new File("plugins");
        List<PluginInfo> plugins = PluginLoader.scanPlugins(pluginsDir);

        for (PluginInfo plugin : plugins) {
            System.out.println("Discovered plugin: " + plugin);

            // TODO: Register plugin with manager via REST
        }
    }
}