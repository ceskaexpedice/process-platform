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
package org.ceskaexpedice.processplatform.worker.plugin.loader;

import org.ceskaexpedice.processplatform.api.PluginSpi;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;

import java.io.File;
import java.net.*;
import java.security.CodeSource;
import java.util.*;

/**
 * PluginsLoader
 * @author ppodsednik
 */
public final class PluginsLoader {

    private PluginsLoader() {}

    public static URLClassLoader createPluginClassLoader(File pluginDir) {
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            throw new IllegalStateException("No JAR files found in: " + pluginDir.getAbsolutePath());
        }
        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            try {
                urls[i] = jars[i].toURI().toURL();
            } catch (MalformedURLException e) {
                throw new ApplicationException("Error loading JAR file: " + jars[i].getAbsolutePath(), e);
            }
        }
        return new URLClassLoader(urls, PluginsLoader.class.getClassLoader());
    }

    public static ClassLoader createPluginClassLoader(File pluginsDir, String pluginId) {
        File pluginDir = new File(pluginsDir, pluginId);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            throw new ApplicationException("Plugin directory not found: " + pluginDir.getAbsolutePath());
        }
        return createPluginClassLoader(pluginDir);
    }

    public static List<PluginInfo> load(File pluginsDir) {
        List<PluginInfo> result = new ArrayList<>();
        File[] pluginsDirDirs = pluginsDir.listFiles(File::isDirectory);
        if (pluginsDirDirs == null){
            return result;
        }
        for (File pluginDir : pluginsDirDirs) {
            URLClassLoader pluginClassLoader = createPluginClassLoader(pluginDir);
            ServiceLoader<PluginSpi> loader = ServiceLoader.load(PluginSpi.class, pluginClassLoader);
            for (PluginSpi plugin : loader) {
                File pluginJar = getPluginJar(plugin);
                PluginInfo pluginInfo = resolvePlugin(plugin, pluginJar);
                result.add(pluginInfo);
            }
        }
        return result;
    }

    private static File getPluginJar(PluginSpi plugin) {
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        File pluginJar;
        if (codeSource != null && codeSource.getLocation() != null) {
            URI uri;
            try {
                uri = codeSource.getLocation().toURI();
            } catch (URISyntaxException e) {
                throw new ApplicationException(e.toString(), e);
            }
            pluginJar = new File(uri);
        } else {
            throw new ApplicationException("Cannot determine JAR file for plugin: " + plugin.getClass().getName());
        }
        return pluginJar;
    }

    private static PluginInfo resolvePlugin(PluginSpi plugin, File pluginJar) {
        String pluginId = plugin.getPluginId();
        String description = plugin.getDescription();
        String mainClass = plugin.getMainClass();
        Set<String> scheduledProfiles = plugin.getScheduledProfiles();
        Map<String, PayloadFieldSpec> payloadSpec = plugin.getPayloadSpec();

        List<PluginProfile> profiles = PluginProfilesLoader.loadProfiles(pluginJar);
        if(profiles.isEmpty()){
            PluginProfile defaultProfile = new PluginProfile(pluginId, pluginId, pluginId, new ArrayList<>());
            profiles.add(defaultProfile);
        }

        return new PluginInfo(pluginId, description, mainClass, payloadSpec, scheduledProfiles, profiles);
    }

}
