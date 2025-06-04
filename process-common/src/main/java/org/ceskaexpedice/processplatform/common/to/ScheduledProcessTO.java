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

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ScheduledProcessTO
 * @author ppodsednik
 */
public class ScheduledProcessTO {
    private UUID processId;
    private String pluginId;
    private String profileId;
    private String mainClass;
    private List<String> jvmArgs;
    private Map<String, String> staticParams;
    private Map<String, Object> payload;  // JSON payload varies by plugin

    public ScheduledProcessTO() {
    }

    public ScheduledProcessTO(UUID processId, String pluginId, String profileId, String mainClass, List<String> jvmArgs,
                              Map<String, String> staticParams, Map<String, Object> payload) {
        this.processId = processId;
        this.pluginId = pluginId;
        this.profileId = profileId;
        this.mainClass = mainClass;
        this.jvmArgs = jvmArgs;
        this.staticParams = staticParams;
        this.payload = payload;
    }

    public UUID getProcessId() {
        return processId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    public Map<String, String> getStaticParams() {
        return staticParams;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

}
