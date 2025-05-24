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
package org.ceskaexpedice.processplatform.worker.plugin;

import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.dto.ProcessTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class PluginJvmLauncher {

    private PluginJvmLauncher() {}

    public static void launchJvm(ProcessTask processTask) {
        // TODO payload must be pars to be passed to the plugin through main method args
        /*
        File pluginDir = new File("plugins/" + processName);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            throw new IllegalArgumentException("Plugin directory not found: " + pluginDir.getAbsolutePath());
        }*/
        /*
        <process>
		<id>import</id>
		<description>Import FOXML</description>
		<mainClass>org.kramerius.Import</mainClass>
		<standardOs>lrOut</standardOs>
		<javaProcessParameters>-Xmx1024m -Xms256m -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001</javaProcessParameters>
		<errOs>lrErr</errOs>
		<securedaction>a_import</securedaction>
	</process>

         */

        try {

            String classpath = getOwnJarPath() + ";c:\\Users\\petr\\.m2\\repository\\org\\ceskaexpedice\\process-api\\1.0-SNAPSHOT\\process-api-1.0-SNAPSHOT.jar";
            List<String> command = List.of(
                    "java",
                    "-Xmx1024m",
                    "-Xms256m",
                    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001",
                    "-cp", classpath,
                    "org.ceskaexpedice.processplatform.worker.plugin.PluginStarter",
                    "import",
                    "org.kramerius.Import",
                    "importPayload"
            );
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.inheritIO(); // pipe stdout/stderr to current console
            Process processR = pb.start();
            int val = processR.waitFor();
            if (val != 0) {
                InputStream errorStream = processR.getErrorStream();
                String s = IOUtils.toString(errorStream, "UTF-8");
                System.out.println(s);
                //LOGGER.info(s);
            }
            //LOGGER.info("return value exiting process '" + val + "'");
        } catch (IOException | InterruptedException e) {
            // LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    static String getOwnJarPath() {
        return PluginJvmLauncher.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
    }
}
