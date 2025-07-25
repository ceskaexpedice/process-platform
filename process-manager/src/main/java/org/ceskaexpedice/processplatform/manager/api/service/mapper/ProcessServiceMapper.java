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

import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

/**
 * ProcessServiceMapper
 * @author ppodsednik
 */
public class ProcessServiceMapper {

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

    public static ProcessInfo mapProcessBasic(ProcessEntity processEntity) {
        if(processEntity == null) return null;
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcessId(processEntity.getProcessId());
        processInfo.setDescription(processEntity.getDescription());
        processInfo.setProfileId(processEntity.getProfileId());
        processInfo.setWorkerId(processEntity.getWorkerId());
        processInfo.setPid(processEntity.getPid());
        processInfo.setPlanned(processEntity.getPlanned());
        processInfo.setStarted(processEntity.getStarted());
        processInfo.setFinished(processEntity.getFinished());
        processInfo.setStatus(ProcessState.load(processEntity.getStatus()));
        processInfo.setPayload(processEntity.getPayload());
        processInfo.setBatchId(processEntity.getBatchId());
        processInfo.setOwner(processEntity.getOwner());
        return processInfo;
    }
}
