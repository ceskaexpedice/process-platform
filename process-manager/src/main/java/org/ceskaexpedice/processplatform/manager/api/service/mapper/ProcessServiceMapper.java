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
package org.ceskaexpedice.processplatform.manager.api.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.common.utils.DateUtils;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;
import org.json.JSONObject;

/**
 * ProcessServiceMapper
 * @author ppodsednik
 */
public final class ProcessServiceMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ProcessServiceMapper(){}

    public static ProcessEntity mapProcess(ScheduleMainProcess scheduleMainProcess) {
        if(scheduleMainProcess == null) return null;
        ProcessEntity processEntity = new ProcessEntity();
        processEntity.setProfileId(scheduleMainProcess.getProfileId());
        processEntity.setPayload(scheduleMainProcess.getPayload());
        processEntity.setOwner(scheduleMainProcess.getOwnerId());;
        return processEntity;
    }

    public static ProcessEntity mapProcess(ScheduleSubProcess scheduleSubProcess) {
        if(scheduleSubProcess == null) return null;
        ProcessEntity processEntity = new ProcessEntity();
        processEntity.setProfileId(scheduleSubProcess.getProfileId());
        processEntity.setPayload(scheduleSubProcess.getPayload());
        processEntity.setBatchId(scheduleSubProcess.getBatchId());;
        return processEntity;
    }

    public static ScheduledProcess mapProcess(ProcessEntity processEntity) {
        if(processEntity == null) return null;
        ScheduledProcess scheduledProcess = new ScheduledProcess();
        scheduledProcess.setProcessId(processEntity.getProcessId());
        scheduledProcess.setProfileId(processEntity.getProfileId());
        scheduledProcess.setOwnerId(processEntity.getOwner());
        scheduledProcess.setBatchId(processEntity.getBatchId());
        scheduledProcess.setPayload(processEntity.getPayload());
        return scheduledProcess;
    }

    public static Batch mapFirstProcessToBatch(ProcessEntity processEntity) {
        if(processEntity == null) return null;
        Batch batch = new Batch();
        batch.setMainProcessId(processEntity.getProcessId());
        batch.setStatus(ProcessState.load(processEntity.getStatus()));
        batch.setPlanned(DateUtils.convert(processEntity.getPlanned()));
        batch.setStarted(DateUtils.convert(processEntity.getStarted()));
        batch.setFinished(DateUtils.convert(processEntity.getFinished()));
        batch.setOwner(processEntity.getOwner());
        return batch;
    }

    public static ProcessInfo mapProcessBasic(ProcessEntity processEntity) {
        if(processEntity == null) return null;
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcessId(processEntity.getProcessId());
        processInfo.setDescription(processEntity.getDescription());
        processInfo.setProfileId(processEntity.getProfileId());
        processInfo.setWorkerId(processEntity.getWorkerId());
        processInfo.setPid((long) processEntity.getPid());
        processInfo.setPlanned(DateUtils.convert(processEntity.getPlanned()));
        processInfo.setStarted(DateUtils.convert(processEntity.getStarted()));
        processInfo.setFinished(DateUtils.convert(processEntity.getFinished()));
        processInfo.setStatus(ProcessState.load(processEntity.getStatus()));
        processInfo.setPayload(processEntity.getPayload());
        processInfo.setBatchId(processEntity.getBatchId());
        processInfo.setOwner(processEntity.getOwner());
        return processInfo;
    }

    public static JSONObject mapBatchToJson(Batch batch) {
        try {
            String jsonString = mapper.writeValueAsString(batch);
            JSONObject json = new JSONObject(jsonString);
            return json;
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
