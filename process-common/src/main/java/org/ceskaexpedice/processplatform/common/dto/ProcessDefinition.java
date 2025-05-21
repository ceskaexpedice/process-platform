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
package org.ceskaexpedice.processplatform.common.dto;

import java.util.List;

public class ProcessDefinition {

    // From process_definitions table
    private String defId;
    private String description;
    private String mainClass;
    private String javaParameters;
    private String standardOs;
    private String errOs;
    private String securedAction;

    // From processes table
    private String uuid;
    private String name;
    private String paramsJson; // raw JSON or encoded key-value pairs
    private long plannedTimestamp;
    private long startedTimestamp;
    private int status;

    // Convenience (optional): parsed params
    private List<String> argumentList;

    public ProcessDefinition() {}

    // Getters and Setters for all fields

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJavaParameters() {
        return javaParameters;
    }

    public void setJavaParameters(String javaParameters) {
        this.javaParameters = javaParameters;
    }

    public String getStandardOs() {
        return standardOs;
    }

    public void setStandardOs(String standardOs) {
        this.standardOs = standardOs;
    }

    public String getErrOs() {
        return errOs;
    }

    public void setErrOs(String errOs) {
        this.errOs = errOs;
    }

    public String getSecuredAction() {
        return securedAction;
    }

    public void setSecuredAction(String securedAction) {
        this.securedAction = securedAction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }

    public long getPlannedTimestamp() {
        return plannedTimestamp;
    }

    public void setPlannedTimestamp(long plannedTimestamp) {
        this.plannedTimestamp = plannedTimestamp;
    }

    public long getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(long startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<String> argumentList) {
        this.argumentList = argumentList;
    }
}
