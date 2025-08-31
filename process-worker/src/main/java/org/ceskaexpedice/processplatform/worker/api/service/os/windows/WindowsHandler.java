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

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.api.service.os.OSHandler;
import org.ceskaexpedice.processplatform.worker.api.service.os.PidListFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * WindowsHandler
 * @author ppodsednik
 */
public class WindowsHandler implements OSHandler {
    private static final Logger LOGGER = Logger.getLogger(WindowsHandler.class.getName());

    private String pid;

    public WindowsHandler(String pid) {
        this.pid = pid;
    }

    @Override
    public void killProcess() {
        try {
            LOGGER.info("Killing process " + pid);
            // taskkill /PID  <pid>
            List<String> command = new ArrayList<String>();
            command.add("taskkill");
            command.add("/f");
            command.add("/PID");
            command.add(pid);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.start();
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isProcessAlive() {
        try {
            List<String> pids = PidListFactory.createPIDList().getProcessesPIDS();
            return pids.contains(pid);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
