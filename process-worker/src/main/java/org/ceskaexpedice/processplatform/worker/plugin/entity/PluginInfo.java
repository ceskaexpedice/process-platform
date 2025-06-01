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
package org.ceskaexpedice.processplatform.worker.plugin.entity;

import java.util.List;

/**
 * PluginInfo
 * @author ppodsednik
 */
public class PluginInfo {
    private final String pluginId;
    private final String description;
    private final String mainClass;
    private final List<PluginProfile> profiles;

    public PluginInfo(String pluginId, String description, String mainClass, List<PluginProfile> profiles) {
        this.pluginId = pluginId;
        this.description = description;
        this.mainClass = mainClass;
        this.profiles = profiles;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getDescription() {
        return description;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<PluginProfile> getProfiles() {
        return profiles;
    }
}