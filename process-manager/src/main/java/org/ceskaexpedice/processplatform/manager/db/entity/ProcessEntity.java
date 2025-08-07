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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * ProcessEntity
 * @author ppodsednik
 */
public class ProcessEntity {
    private String processId;
    private String description;
    private String profileId;
    private String workerId;
    private int pid;
    private LocalDateTime planned;
    private LocalDateTime started;
    private LocalDateTime finished;
    private int status;
    private Map<String, String> payload;
    private String batchId;
    private String owner;

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

    public String getOwner() {
        return owner;
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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public LocalDateTime getPlanned() {
        return planned;
    }

    public void setPlanned(LocalDateTime planned) {
        this.planned = planned;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public void setFinished(LocalDateTime finished) {
        this.finished = finished;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
