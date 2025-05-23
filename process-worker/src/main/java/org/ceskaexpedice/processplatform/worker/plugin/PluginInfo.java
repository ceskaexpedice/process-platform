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

public class PluginInfo {

    public final String id;
    public final String description;
    public final String mainClass;
    public final File pluginDir;
    public final ClassLoader classLoader;

    public PluginInfo(String id, String description, String mainClass, File pluginDir, ClassLoader classLoader) {
        this.id = id;
        this.description = description;
        this.mainClass = mainClass;
        this.pluginDir = pluginDir;
        this.classLoader = classLoader;
    }

    @Override
    public String toString() {
        return id + " (" + description + ")";
    }

}
