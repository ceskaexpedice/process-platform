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
import org.ceskaexpedice.processplatform.common.ErrorCode;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.common.utils.StringUtils;
import org.ceskaexpedice.processplatform.manager.api.service.mapper.ProcessServiceMapper;
import org.ceskaexpedice.processplatform.manager.client.WorkerClient;
import org.ceskaexpedice.processplatform.manager.client.WorkerClientFactory;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.dao.PluginDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProcessDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProfileDao;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.PluginProfileEntity;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.common.utils.DateUtils.parseLocalDateTime;

/**
 * ProcessService
 * @author ppodsednik
 */
public class ProcessService {
    private static final Logger LOGGER = Logger.getLogger(ProcessService.class.getName());
    private static final Integer GET_BATCHES_DEFAULT_OFFSET = 0;
    private static final Integer GET_BATCHES_DEFAULT_LIMIT = 10;

    private final ManagerConfiguration managerConfiguration;
    private final ProcessDao processDao;
    private final NodeDao nodeDao;
    private final ProfileDao profileDao;
    private final PluginDao pluginDao;
    private final PluginService pluginService;
    private final WorkerClient workerClient;

    public ProcessService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider, PluginService pluginService, NodeService nodeService) {
        this.managerConfiguration = managerConfiguration;
        this.processDao = new ProcessDao(dbConnectionProvider, managerConfiguration);
        this.nodeDao = new NodeDao(dbConnectionProvider, managerConfiguration);
        this.profileDao = new ProfileDao(dbConnectionProvider, managerConfiguration);
        this.pluginDao = new PluginDao(dbConnectionProvider, managerConfiguration);
        this.pluginService = pluginService;
        this.workerClient = WorkerClientFactory.createWorkerClient(this, nodeService);
    }

    public String scheduleMainProcess(ScheduleMainProcess scheduleMainProcess) {
        LOGGER.info(String.format("schedule main process for the profile [%s]", scheduleMainProcess.getProfileId()));
        validatePayload(scheduleMainProcess);
        String newProcessId = UUID.randomUUID().toString();
        String newBatchId = newProcessId;

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleMainProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setDescription("Main process for the profile " + scheduleMainProcess.getProfileId());
        processEntity.setStatus(ProcessState.PLANNED.getVal());
        processEntity.setPlanned(LocalDateTime.now());
        processEntity.setBatchId(newBatchId);

        processDao.createProcess(processEntity);
        return newProcessId;
    }

    public String scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        LOGGER.info(String.format("schedule sub process for the profile [%s]", scheduleSubProcess.getProfileId()));
        validatePayload(scheduleSubProcess);
        if (scheduleSubProcess.getBatchId() == null) {
            throw new BusinessLogicException("Batch id cannot be null for subprocess", ErrorCode.INVALID_INPUT);
        }
        ProcessEntity processMain = processDao.getProcess(scheduleSubProcess.getBatchId());
        if (processMain == null) {
            throw new BusinessLogicException("Not found based on sub process batch id. Process [" + scheduleSubProcess.getBatchId() + "] does not exist", ErrorCode.NOT_FOUND);
        }
        String newProcessId = UUID.randomUUID().toString();
        String ownerId = processMain.getOwner();

        ProcessEntity processEntity = ProcessServiceMapper.mapProcess(scheduleSubProcess);
        processEntity.setProcessId(newProcessId);
        processEntity.setDescription("Sub process for the profile " + scheduleSubProcess.getProfileId());
        processEntity.setStatus(ProcessState.PLANNED.getVal());
        processEntity.setPlanned(LocalDateTime.now());
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
            if (tags.contains(processEntity.getProfileId())) {
                scheduledProcess = ProcessServiceMapper.mapProcess(processEntity);
                break;
            }
        }
        if (scheduledProcess == null) {
            return null;
        }
        PluginProfileEntity profile = profileDao.getProfile(scheduledProcess.getProfileId());
        PluginEntity plugin = pluginDao.getPlugin(profile.getPluginId());
        scheduledProcess.setJvmArgs(profile.getJvmArgs());
        scheduledProcess.setPluginId(profile.getPluginId());
        scheduledProcess.setMainClass(plugin.getMainClass());
        processDao.updateWorkerId(scheduledProcess.getProcessId(), workerId);
        processDao.updateState(scheduledProcess.getProcessId(), ProcessState.NOT_RUNNING);
        return scheduledProcess;
    }

    public List<Batch> getBatches(BatchFilter batchFilter, int offset, int limit) {
        List<Batch> batches = new ArrayList<>();
        List<ProcessEntity> batchHeaders = processDao.getBatchHeaders(batchFilter, offset, limit);
        for (ProcessEntity processHeaderEntity : batchHeaders) {
            List<ProcessState> processStates = new ArrayList<>();
            Batch batch = ProcessServiceMapper.mapFirstProcessToBatch(processHeaderEntity);
            List<ProcessEntity> processes = processDao.getBatch(processHeaderEntity.getBatchId());
            for (ProcessEntity processEntity : processes) {
                ProcessInfo processInfo = ProcessServiceMapper.mapProcessBasic(processEntity);
                batch.getProcesses().add(processInfo);
                processStates.add(processInfo.getStatus());
            }
            ProcessState batchState = ProcessState.calculateBatchState(processStates);
            batch.setStatus(batchState);
            batches.add(batch);
        }
        return batches;
    }

    public Batch getBatch(String mainProcessId) {
        ProcessEntity mainProcessEntity = processDao.getProcess(mainProcessId);
        if (mainProcessEntity == null) {
            throw new BusinessLogicException(String.format("Main process does not exist [%s]", mainProcessId), ErrorCode.NOT_FOUND);
        }
        Batch batch = ProcessServiceMapper.mapFirstProcessToBatch(mainProcessEntity);
        List<ProcessState> processStates = new ArrayList<>();
        List<ProcessEntity> processes = processDao.getBatch(mainProcessId);
        for (ProcessEntity processEntity : processes) {
            ProcessInfo processInfo = ProcessServiceMapper.mapProcessBasic(processEntity);
            batch.getProcesses().add(processInfo);
            processStates.add(processInfo.getStatus());
        }
        ProcessState batchState = ProcessState.calculateBatchState(processStates);
        batch.setStatus(batchState);
        return batch;
    }

    public int deleteBatch(String mainProcessId) {
        Batch batch = getBatch(mainProcessId);
        if(!isDeletableState(batch.getStatus())) {
            throw new BusinessLogicException(String.format("Cannot delete batch [%s] with state [%s]", mainProcessId, batch.getStatus().name()),
                    ErrorCode.INVALID_STATE);
        }
        deleteBatchWorkerDirs(batch);
        int deleted = deleteBatchInDb(mainProcessId);
        return deleted;
    }

    public int deleteBatchInDb(String mainProcessId) {
        int deleted = processDao.deleteBatch(mainProcessId);
        return deleted;
    }

    public void deleteBatchWorkerDirs(Batch batch) {
        for(ProcessInfo processInfo: batch.getProcesses()) {
            workerClient.deleteProcessWorkerDir(processInfo.getProcessId());
        }
    }

    public List<String> getOwners() {
        List<String> owners = processDao.getOwners();
        return owners;
    }

    public int countBatchHeaders(BatchFilter batchFilter) {
        int counter = processDao.countBatchHeaders(batchFilter);
        return counter;
    }

    public void updatePid(String processId, int pid) {
        LOGGER.info(String.format("update pid: processId, pid [%s, %s]", processId, pid));
        boolean updated = processDao.updatePid(processId, pid);
        if (!updated) {
            throw new BusinessLogicException("Process with id [" + processId + "] not found", ErrorCode.NOT_FOUND);
        }
    }

    public void updateState(String processId, ProcessState processState) {
        LOGGER.info(String.format("update state: processId, process state [%s, %s]", processId, processState));
        boolean updated = processDao.updateState(processId, processState);
        if (!updated) {
            throw new BusinessLogicException("Process with id [" + processId + "] not found", ErrorCode.NOT_FOUND);
        }
    }

    public void updateDescription(String processId, String description) {
        LOGGER.info(String.format("update description: processId, description [%s, %s]", processId, description));
        boolean updated = processDao.updateDescription(processId, description);
        if (!updated) {
            throw new BusinessLogicException("Process with id [" + processId + "] not found", ErrorCode.NOT_FOUND);
        }
    }

    public InputStream getProcessLog(String processId, boolean err) {
        InputStream logStream = workerClient.getProcessLog(processId, err);
        return logStream;
    }

    public int getBatchOffset(String offsetStr) {
        int offset = GET_BATCHES_DEFAULT_OFFSET;
        if (StringUtils.isAnyString(offsetStr)) {
            try {
                offset = Integer.valueOf(offsetStr);
                if (offset < 0) {
                    throw new BusinessLogicException(String.format("offset must be zero or a positive number, '%s' is not", offsetStr), ErrorCode.INVALID_INPUT);
                }
            } catch (NumberFormatException e) {
                throw new BusinessLogicException(String.format("offset must be a number, '%s' is not", offsetStr), ErrorCode.INVALID_INPUT);
            }
        }
        return offset;
    }

    public static int getBatchLimit(String limitStr) {
        int limit = GET_BATCHES_DEFAULT_LIMIT;
        if (StringUtils.isAnyString(limitStr)) {
            try {
                limit = Integer.valueOf(limitStr);
                if (limit < 1) {
                    throw new BusinessLogicException(String.format("limit must be a positive number, '%s' is not", limitStr), ErrorCode.INVALID_INPUT);
                }
            } catch (NumberFormatException e) {
                throw new BusinessLogicException(String.format("limit must be a number, '%s' is not", limitStr), ErrorCode.INVALID_INPUT);
            }
        }
        return limit;
    }

    public BatchFilter createBatchFilter(String owner, String processState, String from, String to) {
        BatchFilter filter = new BatchFilter();
        if (StringUtils.isAnyString(owner)) {
            filter.setOwner(owner);
        }
        if (StringUtils.isAnyString(from)) {
            filter.setFrom(parseLocalDateTime(from));
        }
        if (StringUtils.isAnyString(to)) {
            filter.setTo(parseLocalDateTime(to));
        }
        if (StringUtils.isAnyString(processState)) {
            try {
                filter.setProcessState(ProcessState.valueOf(processState));
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException(e.toString(), ErrorCode.INVALID_INPUT);
            }
        }
        if (filter.isEmpty()) {
            return null;
        } else {
            return filter;
        }
    }

    private void validatePayload(ScheduleProcess scheduleProcess) {
        PluginProfileEntity profile = profileDao.getProfile(scheduleProcess.getProfileId());
        PluginEntity plugin = pluginDao.getPlugin(profile.getPluginId());
        pluginService.validatePayload(plugin.getPluginId(), scheduleProcess.getPayload());
    }

    private boolean isDeletableState(ProcessState batchState) {
        ProcessState[] deletableStates = new ProcessState[]{ProcessState.FINISHED, ProcessState.FAILED, ProcessState.KILLED};
        return batchState != null && Arrays.asList(deletableStates).contains(batchState);
    }
}
