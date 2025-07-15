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
package org.ceskaexpedice.processplatform.common.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PluginInfo
 * @author ppodsednik
 */
public class PluginInfo {
    private String pluginId;
    private String description;
    private String mainClass;
    private Map<String, PayloadFieldSpec> payloadFieldSpecMap;
    private Set<String> scheduledProfiles;
    private List<PluginProfile> profiles;

    public PluginInfo() {
    }

    public PluginInfo(String pluginId, String description, String mainClass,
                      Map<String, PayloadFieldSpec> payloadFieldSpecMap,
                      Set<String> scheduledProfiles, List<PluginProfile> profiles) {
        this.pluginId = pluginId;
        this.description = description;
        this.mainClass = mainClass;
        this.payloadFieldSpecMap = payloadFieldSpecMap;
        this.scheduledProfiles = scheduledProfiles;
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

    public Map<String, PayloadFieldSpec> getPayloadFieldSpecMap() {
        return payloadFieldSpecMap;
    }

    public List<PluginProfile> getProfiles() {
        return profiles;
    }

    public Set<String> getScheduledProfiles() {
        return scheduledProfiles;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setPayloadFieldSpecMap(Map<String, PayloadFieldSpec> payloadFieldSpecMap) {
        this.payloadFieldSpecMap = payloadFieldSpecMap;
    }

    public void setScheduledProfiles(Set<String> scheduledProfiles) {
        this.scheduledProfiles = scheduledProfiles;
    }

    public void setProfiles(List<PluginProfile> profiles) {
        this.profiles = profiles;
    }
}