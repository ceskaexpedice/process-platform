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
package org.ceskaexpedice.processplatform.worker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.ProcessRegistry;
import org.json.JSONObject;

/**
 * WorkerInfoMapper {
 * @author ppodsednik
 */
public final class WorkerInfoMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private WorkerInfoMapper(){}

    public static WorkerInfo mapProcessRegistry(ProcessRegistry processRegistry) {
        if(processRegistry == null) return null;
        WorkerInfo workerInfo = new WorkerInfo();
        workerInfo.setCurrentPid(processRegistry.getCurrentPid());
        workerInfo.setCurrentProcess(processRegistry.getCurrentProcess());
        workerInfo.setJvmAlive(processRegistry.isCurrentAlive());
        workerInfo.setLastProcess(processRegistry.getLastProcess());
        workerInfo.setState(processRegistry.getState());
        workerInfo.setLastExitCode(processRegistry.getLastExitCode());
        return workerInfo;
    }

    public static JSONObject mapToJson(WorkerInfo workerInfo) {
        try {
            String jsonString = mapper.writeValueAsString(workerInfo);
            JSONObject json = new JSONObject(jsonString);
            return json;
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
