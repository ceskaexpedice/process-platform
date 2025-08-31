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
import org.ceskaexpedice.processplatform.worker.api.service.os.OSHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * UnixHandler
 */
public class UnixHandler implements OSHandler {

    private static final Logger LOGGER = Logger.getLogger(UnixHandler.class.getName());
    private String pid;

    public UnixHandler(String pid) {
        this.pid = pid;
    }

    @Override
    public void killProcess() {
        try {
            LOGGER.fine("Killing process " + pid);
            // kill -9 <pid>
            List<String> command = new ArrayList<String>();
            command.add("kill");
            command.add("-9");
            command.add(pid);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process startedProcess = processBuilder.start();
            LOGGER.fine("killing command '" + command + "' and exit command "/*+startedProcess.exitValue()*/);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public boolean isProcessAlive() {
        try {
            LOGGER.info("is alive");
            if (pid != null) {
                List<String> command = new ArrayList<String>();
                command.add("ps");
                command.add("-p");
                command.add(pid);
                command.add("-o");
                command.add("pid,time,cmd");
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process psProcess = processBuilder.start();
                InputStream inputStream = psProcess.getInputStream();
                // pockam az bude konec
                int exitValue = psProcess.waitFor();
                if (exitValue != 0) {
                    LOGGER.warning("ps exiting with value '" + exitValue + "'");
                }
                List<String[]> data = new ArrayList<String[]>();
                // pak ctu vypis procesu
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                IOUtils.copy(inputStream, bos);
                BufferedReader reader = new BufferedReader(new StringReader(new String(bos.toByteArray())));
                String line = null;
                boolean firstLine = false;
                while ((line = reader.readLine()) != null) {
                    if (!firstLine) firstLine = true;
                    else {
                        String[] array = line.split(" ");
                        LOGGER.fine("ps data == " + Arrays.asList(array));
                        data.add(array);
                    }
                }

                IOUtils.closeQuietly(inputStream);

                return data.size() == 1;
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
        return false;
    }


}
