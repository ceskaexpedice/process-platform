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
package org.ceskaexpedice.processplatform.common.entity;

import java.util.List;
import java.util.Map;

/**
 * ScheduleProcess
 * @author ppodsednik
 */
public class ScheduleProcess {
    private String profileId;
    private String pluginId;
    private Map<String, String> payload;
    private boolean pending = false;

    public ScheduleProcess() {
    }

    public ScheduleProcess(String profileId, String pluginId, Map<String, String> payload, boolean pending) {
        this.profileId = profileId;
        this.pluginId = pluginId;
        this.payload = payload;
        this.pending = pending;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public boolean isPending() {
        return pending;
    }
}
