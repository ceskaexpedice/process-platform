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

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public final class ProcessDirUtils {

    private ProcessDirUtils() {}

    public static File prepareProcessWorkingDirectory(String processId) {
        String value = WorkerConfiguration.DEFAULT_WORKER_WORKDIR + File.separator + processId;
        File processWorkingDir = new File(value);
        if (!processWorkingDir.exists()) {
            boolean mkdirs = processWorkingDir.mkdirs();
            if (!mkdirs){
                throw new ApplicationException("cannot create directory '" + processWorkingDir.getAbsolutePath() + "'");
            }
        }
        return processWorkingDir;
    }

    public static File errorOutFile(File processWorkingDir) {
        return new File(createFolderIfNotExists(processWorkingDir + File.separator + "plgErr"),"sterr.err");
    }

    public static File standardOutFile(File processWorkingDir) {
        return new File(createFolderIfNotExists(processWorkingDir + File.separator + "plgOut"),"stout.out");
    }

    public static PrintStream createPrintStream(String file) throws FileNotFoundException {
        return new PrintStream(new FileOutputStream(file));
    }

    private static File createFolderIfNotExists(String folder) {
        File fldr = new File(folder);
        if (!fldr.exists()) {
            boolean mkdirs = fldr.mkdirs();
            if (!mkdirs){
                throw new ApplicationException("cannot create directory '" + fldr.getAbsolutePath() + "'");
            }
        }
        return fldr;
    }


}
