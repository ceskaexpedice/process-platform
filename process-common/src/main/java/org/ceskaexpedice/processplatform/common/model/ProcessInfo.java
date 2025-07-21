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

import java.util.Date;
import java.util.Map;

/**
 * ProcessInfo
 * @author ppodsednik
 */
public class ProcessInfo {
    private String processId;
    private String description;
    private String profileId;
    private String workerId;
    private int pid;
    private Date planned;
    private Date started;
    private Date finished;
    private ProcessState status;
    private Map<String, String> payload;
    private String batchId;
    private String owner;

    public ProcessInfo() {
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

    public Date getPlanned() {
        return planned;
    }

    public void setPlanned(Date planned) {
        this.planned = planned;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public ProcessState getStatus() {
        return status;
    }

    public void setStatus(ProcessState status) {
        this.status = status;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
