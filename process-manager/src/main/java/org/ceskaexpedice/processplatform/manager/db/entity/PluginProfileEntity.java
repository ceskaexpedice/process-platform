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
package org.ceskaexpedice.processplatform.manager.db.entity;

import java.util.List;

/**
 * PluginProfileEntity
 * @author ppodsednik
 */
public class PluginProfileEntity {
    private String profileId;
    private String description;
    private String pluginId;
    private List<String> jvmArgs;

    public PluginProfileEntity() {
    }

    public String getProfileId() {
        return profileId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
