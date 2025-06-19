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
import org.ceskaexpedice.processplatform.common.entity.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration.*;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.WORKER_CONFIG_BASE64_KEY;
import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.*;

/**
 * PluginJvmLauncher
 * @author ppodsednik
 */
public final class PluginJvmLauncher {
    public static final Logger LOGGER = java.util.logging.Logger.getLogger(PluginJvmLauncher.class.getName());

    private PluginJvmLauncher() {
    }

    public static void launchJvm(ScheduledProcess scheduledProcess, WorkerConfiguration workerConfiguration) {
        try {
            List<String> command = createCommand(scheduledProcess, workerConfiguration);
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

    private static List<String> createCommand(ScheduledProcess scheduledProcess, WorkerConfiguration workerConfiguration) throws JsonProcessingException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-Duser.home=" + System.getProperty("user.home"));
        command.add("-Dfile.encoding=UTF-8" );
        List<String> javaProcessParameters = scheduledProcess.getJvmArgs();
        for (String jpParam : javaProcessParameters) {
            command.add(jpParam);
        }

        String workerConfigJson = new ObjectMapper().writeValueAsString(workerConfiguration.getAll());
        String encodedConfig = Base64.getEncoder().encodeToString(workerConfigJson.getBytes(StandardCharsets.UTF_8));
        command.add("-D" + WORKER_CONFIG_BASE64_KEY + "=" + encodedConfig);

        ProcessConfiguration processConfiguration = new ProcessConfiguration();
        convert(scheduledProcess, processConfiguration);
        File processWorkingDir = prepareProcessWorkingDirectory(scheduledProcess.getProcessId() + "");
        File standardStreamFile = standardOutFile(processWorkingDir);
        File errStreamFile = errorOutFile(processWorkingDir);
        processConfiguration.set(SOUT_FILE_KEY, standardStreamFile.getAbsolutePath());
        processConfiguration.set(SERR_FILE_KEY, errStreamFile.getAbsolutePath());
        String processConfigJson = new ObjectMapper().writeValueAsString(processConfiguration.getAll());
        String encodedProcessConfig = Base64.getEncoder().encodeToString(processConfigJson.getBytes(StandardCharsets.UTF_8));
        command.add("-D" + PROCESS_CONFIG_BASE64_KEY + "=" + encodedProcessConfig);

        command.add("-cp");
        String starterClasspath = workerConfiguration.get(WorkerConfiguration.STARTER_CLASSPATH_KEY);
        command.add(starterClasspath);

        command.add(PluginStarter.class.getName());
        return command;
    }

    private static void convert(ScheduledProcess scheduledProcess, ProcessConfiguration processConfiguration) throws JsonProcessingException {
        processConfiguration.set(MAIN_CLASS_KEY, scheduledProcess.getMainClass());
        processConfiguration.set(PLUGIN_ID_KEY, scheduledProcess.getPluginId());
        processConfiguration.set(PROCESS_ID_KEY, scheduledProcess.getProcessId());
        String payloadJson = new ObjectMapper().writeValueAsString(scheduledProcess.getPayload());
        String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        processConfiguration.set(PLUGIN_PAYLOAD_BASE64_KEY, encodedPayload);
    }

}
