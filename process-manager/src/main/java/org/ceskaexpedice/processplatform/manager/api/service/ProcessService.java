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

import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProcessServiceMapper;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProfileServiceMapper;
import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProcessDao;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ProcessService
 * @author ppodsednik
 */
public class ProcessService {
    private final ManagerConfiguration managerConfiguration;
    private final ProcessDao processDao;
    private final NodeDao nodeDao;

    public ProcessService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.processDao = new ProcessDao(dbConnectionProvider, managerConfiguration);
        this.nodeDao = new NodeDao(dbConnectionProvider, managerConfiguration);
    }

    public void scheduleProcess(ScheduleMainProcess scheduleMainProcess) {
        // TODO validate payload
        String newProcessId = UUID.randomUUID().toString();
        String newBatchId = UUID.randomUUID().toString();

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleMainProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setBatchId(newBatchId);

        processDao.createPlannedProcess(processEntity);
    }

    public void scheduleProcess(ScheduleSubProcess scheduleSubProcess) {
        // TODO validate payload
        String newProcessId = UUID.randomUUID().toString();
        String ownerId = null; // TODO fetch it from owner process

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleSubProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setOwnerId(ownerId);

        processDao.createPlannedProcess(processEntity);
    }

    public ScheduledProcess getNextScheduledProcess(String workerId) {
        List<ProcessEntity> processes = processDao.getPlannedProcesses();
        ScheduledProcess scheduledProcess = null;
        for (ProcessEntity processEntity : processes) {
            Set<String> tags = nodeDao.getNode(workerId).getTags();
            if(tags.contains(processEntity.getProfileId())){
                scheduledProcess = ProcessServiceMapper.mapProcess(processEntity);
                // TODO populate pluginId, mainClass, jvmArgs from plugin
                scheduledProcess.setJvmArgs(null); // TODO
                scheduledProcess.setPluginId(null); // TODO
                scheduledProcess.setMainClass(null); // TODO
                break;
            }
        }
        // TODO set process state to NOT_RUNNING
        // TODO set workerId on the process

        return scheduledProcess;
    }

    public void updateProcessPid(String processId, String pid) {
        // TODO processDao.
    }

    public void updateProcessState(String processId, ProcessState processState) {
        // TODO processDao.
    }

    public void updateProcessName(String processId, String name) {
        // TODO processDao.
    }

}
