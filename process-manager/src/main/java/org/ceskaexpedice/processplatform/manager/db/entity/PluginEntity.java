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

import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;

import java.util.Map;
import java.util.Set;

/**
 * PluginEntity
 * @author ppodsednik
 */
public class PluginEntity {
    private String pluginId;
    private String description;
    private String mainClass;
    private Map<String, PayloadFieldSpec> payloadFieldSpecMap;
    private Set<String> scheduledProfiles;

    public PluginEntity() {
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

}