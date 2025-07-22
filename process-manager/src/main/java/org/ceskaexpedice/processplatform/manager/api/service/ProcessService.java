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

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProcessServiceMapper;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.dao.PluginDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProcessDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProfileDao;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.util.Date;
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
    private final ProfileDao profileDao;
    private final PluginDao pluginDao;
    private final PluginService pluginService;

    public ProcessService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider, PluginService pluginService) {
        this.managerConfiguration = managerConfiguration;
        this.processDao = new ProcessDao(dbConnectionProvider, managerConfiguration);
        this.nodeDao = new NodeDao(dbConnectionProvider, managerConfiguration);
        this.profileDao = new ProfileDao(dbConnectionProvider, managerConfiguration);
        this.pluginDao = new PluginDao(dbConnectionProvider, managerConfiguration);
        this.pluginService = pluginService;
    }

    public String scheduleMainProcess(ScheduleMainProcess scheduleMainProcess) {
        validatePayload(scheduleMainProcess);
        String newProcessId = UUID.randomUUID().toString();
        String newBatchId = newProcessId;

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleMainProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setDescription("Main process for the profile " + scheduleMainProcess.getProfileId());
        processEntity.setStatus(ProcessState.PLANNED.getVal());
        processEntity.setPlanned(new Date());
        processEntity.setBatchId(newBatchId);

        processDao.createProcess(processEntity);
        return newProcessId;
    }

    public String scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        validatePayload(scheduleSubProcess);
        if(scheduleSubProcess.getBatchId() == null){
            throw new BusinessLogicException("Batch id cannot be null for subprocess");
        }
        ProcessEntity processMain = processDao.getProcess(scheduleSubProcess.getBatchId());
        if(processMain == null){
            throw new BusinessLogicException("Process with id " + scheduleSubProcess.getBatchId() + " (sub process batch id) does not exist");
        }
        String newProcessId = UUID.randomUUID().toString();
        String ownerId = processMain.getOwner();

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleSubProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setDescription("Sub process for the profile " + scheduleSubProcess.getProfileId());
        processEntity.setStatus(ProcessState.PLANNED.getVal());
        processEntity.setPlanned(new Date());
        processEntity.setOwner(ownerId);

        processDao.createProcess(processEntity);
        return newProcessId;
    }

    public ProcessInfo getProcess(String processId) {
        ProcessInfo processInfo = ProcessServiceMapper.mapProcessBasic(processDao.getProcess(processId));
        return processInfo;
    }

    public ScheduledProcess getNextScheduledProcess(String workerId) {
        List<ProcessEntity> processes = processDao.getProcesses(ProcessState.PLANNED.getVal());
        ScheduledProcess scheduledProcess = null;
        for (ProcessEntity processEntity : processes) {
            Set<String> tags = nodeDao.getNode(workerId).getTags();
            if(tags.contains(processEntity.getProfileId())){
                scheduledProcess = ProcessServiceMapper.mapProcess(processEntity);
                break;
            }
        }
        if(scheduledProcess == null){
            return null;
        }
        PluginProfileEntity profile = profileDao.getProfile(scheduledProcess.getProfileId());
        PluginEntity plugin = pluginDao.getPlugin(profile.getPluginId());
        scheduledProcess.setJvmArgs(profile.getJvmArgs());
        scheduledProcess.setPluginId(profile.getPluginId());
        scheduledProcess.setMainClass(plugin.getMainClass());
        processDao.update(scheduledProcess.getProcessId(), workerId, ProcessState.NOT_RUNNING);
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

    private void validatePayload(ScheduleProcess scheduleProcess) {
        PluginProfileEntity profile = profileDao.getProfile(scheduleProcess.getProfileId());
        PluginEntity plugin = pluginDao.getPlugin(profile.getPluginId());
        pluginService.validatePayload(plugin.getPluginId(), scheduleProcess.getPayload());
    }

}
