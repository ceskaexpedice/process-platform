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
package org.ceskaexpedice.processplatform.common.dto;

import java.util.List;

public class PluginInfoDto {
    private String pluginId;
    private String description;
    private String mainClass;
    private List<PluginProfileDto> profiles;

    public PluginInfoDto() {
    }

    public PluginInfoDto(String pluginId, String description, String mainClass, List<PluginProfileDto> profiles) {
        this.pluginId = pluginId;
        this.description = description;
        this.mainClass = mainClass;
        this.profiles = profiles;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<PluginProfileDto> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<PluginProfileDto> profiles) {
        this.profiles = profiles;
    }
}