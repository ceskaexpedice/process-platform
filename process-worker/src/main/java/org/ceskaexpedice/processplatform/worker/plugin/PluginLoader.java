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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader {

    public static ClassLoader loadProcessClassLoader(String processName) throws Exception {
        File pluginDir = new File("plugins/" + processName);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            throw new IllegalArgumentException("Plugin directory not found: " + pluginDir.getAbsolutePath());
        }

        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            throw new IllegalStateException("No JAR files found in: " + pluginDir.getAbsolutePath());
        }

        URL[] urls = new URL[jars.length];
        for (int i = 0; i < jars.length; i++) {
            urls[i] = jars[i].toURI().toURL();
        }

        return new URLClassLoader(urls, PluginLoader.class.getClassLoader());
    }
}
