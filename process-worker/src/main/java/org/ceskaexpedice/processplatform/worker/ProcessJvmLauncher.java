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
package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.common.dto.ProcessTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ProcessJvmLauncher {

    public void launchJvm(ProcessTask processTask) {
        // TODO payload must be pars to be passed to the plugin through main method args
        /*
        File pluginDir = new File("plugins/" + processName);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            throw new IllegalArgumentException("Plugin directory not found: " + pluginDir.getAbsolutePath());
        }*/
        try {

            String classpath = getOwnJarPath();
            List<String> command = List.of(
                    "java",
                    "-cp", classpath,
                    "org.ceskaexpedice.processplatform.worker.startprocess.ProcessStarter",
                    "processName",
                    "payload"
            );
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.inheritIO(); // pipe stdout/stderr to current console
            Process processR = pb.start();
            int val = processR.waitFor();
            if (val != 0) {
                InputStream errorStream = processR.getErrorStream();
                //String s = IOUtils.toString(errorStream, "UTF-8");
                //LOGGER.info(s);
            }
            //LOGGER.info("return value exiting process '" + val + "'");
        } catch (IOException | InterruptedException e) {
            // LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getOwnJarPath() {
        return ProcessJvmLauncher.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
    }
}
