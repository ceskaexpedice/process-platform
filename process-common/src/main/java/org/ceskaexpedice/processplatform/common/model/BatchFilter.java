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
import java.util.List;

/**
 * BatchFilter
 * @author ppodsednik
 */
public class BatchFilter {
    private String owner;
    private LocalDateTime from;
    private LocalDateTime to;
    private ProcessState processState;
    private List<String> workers;
    public BatchFilter() {
    }

    public boolean isEmpty() {

        return owner == null &&
                from == null &&
                to == null &&
                processState == null &&
                (workers == null || workers.isEmpty());
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }
}
