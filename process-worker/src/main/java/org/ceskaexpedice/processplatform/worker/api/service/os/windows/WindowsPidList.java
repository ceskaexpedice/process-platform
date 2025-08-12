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
package org.ceskaexpedice.processplatform.worker.api.service.os.windows;

import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.api.service.os.PidList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * WindowsPidList
 */
public class WindowsPidList extends PidList {

    public static final Logger LOGGER = Logger.getLogger(WindowsPidList.class.getName());

    @Override
    public List<String> getProcessesPIDS() {
        try {
            List<String> command = new ArrayList<String>();
            command.add("tasklist");
            command.add("/FO");
            command.add("CSV");
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process psProcess = processBuilder.start();
            InputStream inputStream = psProcess.getInputStream();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, bos);
            int exitValue = psProcess.waitFor();
            if (exitValue != 0) {
                LOGGER.warning("ps exiting with value '" + exitValue + "'");
            }
            BufferedReader reader = new BufferedReader(new StringReader(new String(bos.toByteArray(), "Windows-1250")));
            return WindowsPIDListProcessOutput.pids(reader);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
