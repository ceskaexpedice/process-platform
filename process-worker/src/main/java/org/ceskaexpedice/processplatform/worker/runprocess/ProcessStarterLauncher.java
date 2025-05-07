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
package org.ceskaexpedice.processplatform.worker.runprocess;

public class ProcessStarterLauncher {

    public void launchProcess(String processName, String taskPayload) {
        try {
            // Ensure plugin is available (optional load check)
            PluginLoader.loadPlugin(processName); // This throws if plugin JAR missing

            // Start new JVM with ProcessStarter
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp", "worker-service.jar:plugins/" + processName + ".jar",
                    "org.ceskaexpedice.processplatform.worker.executor.ProcessStarter",
                    processName,
                    taskPayload
            );
            pb.inheritIO(); // Or redirect output if needed
            pb.start();

        } catch (Exception e) {
            System.err.println("Failed to launch process: " + e.getMessage());
        }
    }
}
