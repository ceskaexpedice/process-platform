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

import java.util.Map;

/**
 * ProcessEntity
 * @author ppodsednik
 */
public class ProcessEntity {
    private String processId;
    private String profileId;
    private Map<String, String> payload;
    private String batchId;
    private String ownerId;

    public ProcessEntity() {
    }

    public String getProcessId() {
        return processId;
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

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
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
