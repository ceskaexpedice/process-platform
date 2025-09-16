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
package org.ceskaexpedice.processplatform.worker.api.service;

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.ErrorCode;
import org.ceskaexpedice.processplatform.common.utils.StringUtils;
import org.ceskaexpedice.processplatform.worker.api.service.os.OSHandler;
import org.ceskaexpedice.processplatform.worker.api.service.os.OSHandlerFactory;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.*;

/**
 * ForManagerService
 * @author ppodsednik
 */
public class ForManagerService {
    private static final Integer GET_LOGS_DEFAULT_OFFSET = 0;
    private static final Integer GET_LOGS_DEFAULT_LIMIT = 10;
    private final WorkerConfiguration workerConfiguration;

    public ForManagerService(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
    }

    public InputStream getProcessLog(String processId, boolean err) {
        try {
            File processWorkingDir = prepareProcessWorkingDirectory(workerConfiguration.getWorkerId(), processId);
            File standardStreamFile = err ? errorOutFile(processWorkingDir) : standardOutFile(processWorkingDir);
            return new FileInputStream(standardStreamFile);
        } catch (FileNotFoundException e) {
            throw new ApplicationException("Could not find process log file for process with id " + processId, e);
        }
    }

    public long getProcessLogSize(String processId, boolean err) {
        File processWorkingDir = prepareProcessWorkingDirectory(workerConfiguration.getWorkerId(), processId);
        File standardStreamFile = err ? errorOutFile(processWorkingDir) : standardOutFile(processWorkingDir);
        ProcessLogsHelper processLogsHelper = new ProcessLogsHelper(standardStreamFile);
        long logFileSize = processLogsHelper.getLogFileSize();
        return logFileSize;
    }

    public List<String> getProcessLogLines(String processId, boolean err, long offset, long limit) {
        File processWorkingDir = prepareProcessWorkingDirectory(workerConfiguration.getWorkerId(), processId);
        File standardStreamFile = err ? errorOutFile(processWorkingDir) : standardOutFile(processWorkingDir);
        ProcessLogsHelper processLogsHelper = new ProcessLogsHelper(standardStreamFile);
        List<String> lines = processLogsHelper.getLogFileData(offset, limit);
        return lines;
    }

    public long getLogLimit(String limitStr) {
        int limit = GET_LOGS_DEFAULT_LIMIT;
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

    public long getLogOffset(String offsetStr) {
        int offset = GET_LOGS_DEFAULT_OFFSET;
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

    public void deleteWorkingDir(String processId) {
        File processWorkingDir = prepareProcessWorkingDirectory(workerConfiguration.getWorkerId(), processId);
        ProcessDirUtils.deleteRecursively(processWorkingDir);
    }

    public boolean killProcessJvm(String pid) {
        OSHandler osHandler = OSHandlerFactory.createOSHandler(pid);
        boolean processAlive = osHandler.isProcessAlive();
        if (!processAlive) {
            return false;
        }
        osHandler.killProcess();
        try {
            Thread.sleep(500); // give it a moment
        } catch (InterruptedException e) {
            return false;
        }
        processAlive = osHandler.isProcessAlive();
        return !processAlive;
    }

}
