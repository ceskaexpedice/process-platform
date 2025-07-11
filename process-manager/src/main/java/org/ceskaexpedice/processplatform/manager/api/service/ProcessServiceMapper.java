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
package org.ceskaexpedice.processplatform.manager.api.service;

import org.ceskaexpedice.processplatform.common.entity.*;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.util.ArrayList;
import java.util.List;

class ProcessServiceMapper {

    static ProcessEntity mapProcess(ScheduleMainProcess scheduleMainProcess) {
        if(scheduleMainProcess == null) return null;
        ProcessEntity processEntity = new ProcessEntity();
        processEntity.setProfileId(scheduleMainProcess.getProfileId());
        processEntity.setPayload(scheduleMainProcess.getPayload());
        processEntity.setBatchId(scheduleMainProcess.getOwnerId());;
        return processEntity;
    }

    static ProcessEntity mapProcess(ScheduleSubProcess scheduleSubProcess) {
        if(scheduleSubProcess == null) return null;
        ProcessEntity processEntity = new ProcessEntity();
        processEntity.setProfileId(scheduleSubProcess.getProfileId());
        processEntity.setPayload(scheduleSubProcess.getPayload());
        processEntity.setBatchId(scheduleSubProcess.getBatchId());;
        return processEntity;
    }

    static ScheduledProcess mapProcess(ProcessEntity processEntity) {
        if(processEntity == null) return null;
        ScheduledProcess scheduledProcess = new ScheduledProcess();
        scheduledProcess = new ScheduledProcess();
        scheduledProcess.setProcessId(processEntity.getProcessId());
        scheduledProcess.setProfileId(processEntity.getProfileId());
        scheduledProcess.setOwnerId(processEntity.getOwnerId());
        scheduledProcess.setBatchId(processEntity.getBatchId());
        scheduledProcess.setPayload(processEntity.getPayload());
        return scheduledProcess;
    }
}
