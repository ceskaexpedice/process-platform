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
package org.ceskaexpedice.processplatform.worker.plugin;

import org.ceskaexpedice.processplatform.api.ProcessPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PluginDiscovery {

    public static PluginInfo discoverPlugin(ProcessPlugin pluginInstance, File pluginJar, File profilesDir, ClassLoader cl) throws IOException {
        String pluginId = pluginInstance.getPluginId();
        String description = pluginInstance.getDescription();
        String mainClass = pluginInstance.getMainClass();

        List<PluginProfile> profiles = PluginProfileLoader.loadProfiles(pluginJar, profilesDir, pluginId);

        return new PluginInfo(pluginId, description, mainClass, profiles, cl);
    }
}