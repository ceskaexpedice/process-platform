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
package org.ceskaexpedice.processplatform.worker.api.service.os.unix;

import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.api.service.os.PidList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * UnixPidList
 */
public class UnixPidList extends PidList {

    private static final Logger LOGGER = Logger.getLogger(UnixPidList.class.getName());

    @Override
    public List<String> getProcessesPIDS() {
        try {
            List<String> data = new ArrayList<String>();
            List<String> command = new ArrayList<String>();
            command.add("ps");
            command.add("-A");
            command.add("-o");
            command.add("pid");
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process psProcess = processBuilder.start();
            InputStream inputStream = psProcess.getInputStream();
            int exitValue = psProcess.waitFor();
            if (exitValue != 0) {
                LOGGER.warning("ps exiting with value '" + exitValue + "'");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, bos);
            BufferedReader reader = new BufferedReader(new StringReader(new String(
                    bos.toByteArray())));
            String line = null;
            boolean firstLine = false;
            while ((line = reader.readLine()) != null) {
                if (!firstLine)
                    firstLine = true;
                else {
                    data.add(line.trim());
                }
            }

            IOUtils.closeQuietly(inputStream);
            return data;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
