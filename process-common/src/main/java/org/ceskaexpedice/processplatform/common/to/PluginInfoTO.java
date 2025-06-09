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
package org.ceskaexpedice.processplatform.common.to;

import org.ceskaexpedice.processplatform.api.PayloadFieldSpec;

import java.util.List;
import java.util.Map;

/**
 * PluginInfoTO
 * @author ppodsednik
 */
public class PluginInfoTO {
    private String pluginId;
    private String description;
    private String mainClass;
    private Map<String, PayloadFieldSpec> payloadFieldSpecMap;
    private List<PluginProfileTO> profiles;

    public PluginInfoTO(String pluginId, String description, String mainClass,
                        Map<String, PayloadFieldSpec> payloadFieldSpecMap, List<PluginProfileTO> profiles) {
        this.pluginId = pluginId;
        this.description = description;
        this.mainClass = mainClass;
        this.payloadFieldSpecMap = payloadFieldSpecMap;
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

    public List<PluginProfileTO> getProfiles() {
        return profiles;
    }

}