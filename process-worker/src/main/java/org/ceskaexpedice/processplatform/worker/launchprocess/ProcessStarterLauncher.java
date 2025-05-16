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
package org.ceskaexpedice.processplatform.worker.launchprocess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProcessStarterLauncher {

    public void launchProcess(String processName, String payload) throws IOException {
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
                    "org.ceskaexpedice.processplatform.worker.runprocess.ProcessStarter",
                    processName,
                    payload
            );
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.inheritIO(); // pipe stdout/stderr to current console
            Process process = pb.start();
            // pokracuje dal.. rozhoduje se, jestli pocka na vysledek procesu
            if (wait) {
                int val = process.waitFor();
                if (val != 0) {
                    InputStream errorStream = process.getErrorStream();
                    String s = IOUtils.toString(errorStream, "UTF-8");
                    LOGGER.info(s);
                }
                LOGGER.info("return value exiting process '" + val + "'");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getOwnJarPath() {
        return ProcessStarterLauncher.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
    }
}
