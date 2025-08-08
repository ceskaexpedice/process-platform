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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Batch
 * @author ppodsednik
 */
public class Batch {
    private String mainProcessId;
    private ProcessState status;
    private LocalDateTime planned;
    private LocalDateTime started;
    private LocalDateTime finished;
    private String owner;
    private List<ProcessInfo> processes = new ArrayList<>();

    public String getMainProcessId() {
        return mainProcessId;
    }

    public void setMainProcessId(String mainProcessId) {
        this.mainProcessId = mainProcessId;
    }

    public ProcessState getStatus() {
        return status;
    }

    public void setStatus(ProcessState status) {
        this.status = status;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ProcessInfo> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessInfo> processes) {
        this.processes = processes;
    }
}
