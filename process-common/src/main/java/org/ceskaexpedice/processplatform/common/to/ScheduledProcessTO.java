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
    private String mainClass;
    private Map<String, String> payload;
    private List<String> jvmArgs;

    public ScheduledProcessTO() {
    }

    public ScheduledProcessTO(UUID processId, String pluginId, String mainClass,
                              Map<String, String> payload, List<String> jvmArgs) {
        this.processId = processId;
        this.pluginId = pluginId;
        this.mainClass = mainClass;
        this.payload = payload;
        this.jvmArgs = jvmArgs;
    }

    public UUID getProcessId() {
        return processId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

}
