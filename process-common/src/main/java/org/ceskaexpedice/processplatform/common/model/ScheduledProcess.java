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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScheduledProcess
 * @author ppodsednik
 */
public class ScheduledProcess {
    private String processId;
    private String profileId;
    private String pluginId;
    private String mainClass;
    private Map<String, String> payload =  new HashMap<>();
    private List<String> jvmArgs  = new ArrayList<>();
    private String batchId;
    private String ownerId;

    public ScheduledProcess() {
    }

    public ScheduledProcess(String processId, String profileId, String pluginId, String mainClass,
                            Map<String, String> payload, List<String> jvmArgs, String batchId, String ownerId) {
        this.processId = processId;
        this.profileId = profileId;
        this.pluginId = pluginId;
        this.mainClass = mainClass;
        this.payload = payload;
        this.jvmArgs = jvmArgs;
        this.batchId = batchId;
        this.ownerId = ownerId;
    }

    public String getProcessId() {
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

    public String getBatchId() {
        return batchId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }

    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
