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
package org.ceskaexpedice.processplatform.worker.runprocess;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader {
    public static URLClassLoader loadPlugin(String processName) throws Exception {
        File jar = new File("plugins/" + processName + ".jar");
        if (!jar.exists()) {
            throw new IllegalArgumentException("Missing JAR for process: " + processName);
        }
        return new URLClassLoader(new URL[] { jar.toURI().toURL() }, PluginLoader.class.getClassLoader());
    }
}
