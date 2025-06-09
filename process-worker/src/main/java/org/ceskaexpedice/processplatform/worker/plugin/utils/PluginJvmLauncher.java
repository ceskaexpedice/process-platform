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
package org.ceskaexpedice.processplatform.worker.plugin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginStarter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * PluginJvmLauncher
 * @author ppodsednik
 */
public final class PluginJvmLauncher {

    private PluginJvmLauncher() {}

    public static void launchJvm(ScheduledProcessTO scheduledProcessTO, WorkerConfiguration workerConfiguration) {
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
            List<String> command = new ArrayList<>();
            command.add("java");
            List<String> javaProcessParameters = scheduledProcessTO.getJvmArgs();
            for (String jpParam : javaProcessParameters) {
                command.add(jpParam);
            }
            command.add("-cp");

            String starterClasspath = workerConfiguration.get("starter.classpath");
            command.add(starterClasspath);
//            String classpath = getOwnJarPath() + ";" + workerConfiguration.get("processApiPath");
           // command.add(classpath);

            command.add(PluginStarter.class.getName());
            command.add(workerConfiguration.get("pluginPath").toString());
            command.add(scheduledProcessTO.getPluginId());
            command.add(scheduledProcessTO.getMainClass());

            String payloadJson = new ObjectMapper().writeValueAsString(scheduledProcessTO.getPayload());
            String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            command.add(encodedPayload);

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
        } catch (Throwable e) {
            System.out.println();
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
