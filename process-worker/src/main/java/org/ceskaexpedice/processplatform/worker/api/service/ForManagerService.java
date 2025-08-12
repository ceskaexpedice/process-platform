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
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.*;

/**
 * ForManagerService
 * @author ppodsednik
 */
public class ForManagerService {
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


}
