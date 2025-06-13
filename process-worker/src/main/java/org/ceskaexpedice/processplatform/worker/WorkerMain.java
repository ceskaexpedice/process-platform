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
import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;
import org.ceskaexpedice.processplatform.worker.utils.ManagerClient;
import org.ceskaexpedice.processplatform.worker.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * WorkerMain
 * @author ppodsednik
 */
public class WorkerMain {

    private WorkerLoop workerLoop;
    private WorkerConfiguration workerConfiguration;

    public WorkerMain() {
    }

    public void initialize(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
        registerPlugins();
        this.workerLoop = new WorkerLoop(workerConfiguration);
        workerLoop.start();
    }

    private List<PluginInfo> scanPlugins() {
        File pluginsDir = new File(workerConfiguration.get("pluginPath"));
        List<PluginInfo> pluginsList = PluginsLoader.load(pluginsDir);
        return pluginsList;
    }

    private void registerPlugins() {
        List<PluginInfo> pluginsList = scanPlugins();
        if(pluginsList.isEmpty()){
            throw new IllegalStateException("No plugins found");
        }
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        for (PluginInfo pluginInfo : pluginsList) {
            System.out.println("Discovered plugin: " + pluginInfo);
            PluginInfoTO pluginInfoTO = toTO(pluginInfo);
            managerClient.registerPlugin(pluginInfoTO);
        }
    }

    public void shutdown() {
        workerLoop.stop();
    }

    private static PluginInfoTO toTO(PluginInfo pluginInfo) {
        /* TODO
        List<PluginProfileTO> profiles = pluginInfo.getProfiles().stream()
                .map(profile -> new PluginProfileTO(
                        profile.getProfileId(),
                        pluginInfo.getPluginId(),
                        profile.getStaticParams(),
                        profile.getJvmArgs()
                ))
                .collect(Collectors.toList());

        return new PluginInfoTO(
                pluginInfo.getPluginId(),
                pluginInfo.getDescription(),
                pluginInfo.getMainClass(),
                profiles
        );

         */ return null;
    }


}