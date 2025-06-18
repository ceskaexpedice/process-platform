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
package org.ceskaexpedice.processplatform.worker.plugin.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.worker.plugin.executor.PluginStarter.*;
import static org.ceskaexpedice.processplatform.worker.utils.Utils.*;

/**
 * PluginJvmLauncher
 * @author ppodsednik
 */
public final class PluginJvmLauncher {
    public static final Logger LOGGER = java.util.logging.Logger.getLogger(PluginJvmLauncher.class.getName());

    private PluginJvmLauncher() {
    }

    public static void launchJvm(ScheduledProcessTO scheduledProcessTO, WorkerConfiguration workerConfiguration) {
        try {
            List<String> command = createCommand(scheduledProcessTO, workerConfiguration);
            ProcessBuilder pb = new ProcessBuilder(command);
            Process processR = pb.start();
            int val = processR.waitFor();
            if (val != 0) {
                InputStream errorStream = processR.getErrorStream();
                String s = IOUtils.toString(errorStream, "UTF-8");
                LOGGER.info(s);
            }
            LOGGER.info("return value exiting process '" + val + "'");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static List<String> createCommand(ScheduledProcessTO scheduledProcessTO, WorkerConfiguration workerConfiguration) throws JsonProcessingException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-Duser.home=" + System.getProperty("user.home"));
        command.add("-Dfile.encoding=UTF-8" );

        convert(scheduledProcessTO, command);

        String workerConfigJson = new ObjectMapper().writeValueAsString(workerConfiguration.getAll());
        String encodedConfig = Base64.getEncoder().encodeToString(workerConfigJson.getBytes(StandardCharsets.UTF_8));
        command.add("-D" + WORKER_CONFIG_BASE64_KEY + "=" + encodedConfig);

        File processWorkingDir = prepareProcessWorkingDirectory(scheduledProcessTO.getProcessId() + "");
        File standardStreamFile = standardOutFile(processWorkingDir);
        File errStreamFile = errorOutFile(processWorkingDir);
        command.add("-D" + SOUT_FILE_KEY + "=" + standardStreamFile.getAbsolutePath());
        command.add("-D" + SERR_FILE_KEY + "=" + errStreamFile.getAbsolutePath());

        command.add("-cp");
        String starterClasspath = workerConfiguration.get(WorkerConfiguration.STARTER_CLASSPATH_KEY);
        command.add(starterClasspath);

        command.add(PluginStarter.class.getName());
        return command;
    }

    private static void convert(ScheduledProcessTO scheduledProcessTO, List<String> command) throws JsonProcessingException {
        List<String> javaProcessParameters = scheduledProcessTO.getJvmArgs();
        for (String jpParam : javaProcessParameters) {
            command.add(jpParam);
        }
        command.add("-D" + MAIN_CLASS_KEY + "="  + scheduledProcessTO.getMainClass());
        command.add("-D" + PLUGIN_ID_KEY + "="  + scheduledProcessTO.getPluginId());
        command.add("-D" + PROCESS_ID_KEY + "="  + scheduledProcessTO.getProcessId());
        String payloadJson = new ObjectMapper().writeValueAsString(scheduledProcessTO.getPayload());
        String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        command.add("-D" + PLUGIN_PAYLOAD_BASE64_KEY + "=" + encodedPayload);
    }

}
