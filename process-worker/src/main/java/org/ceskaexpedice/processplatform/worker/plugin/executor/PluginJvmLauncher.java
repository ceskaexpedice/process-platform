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
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration.*;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.WORKER_CONFIG_BASE_64;
import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.*;

/**
 * PluginJvmLauncher
 * @author ppodsednik
 */
public final class PluginJvmLauncher {
    private static final Logger LOGGER = Logger.getLogger(PluginJvmLauncher.class.getName());

    private PluginJvmLauncher() {
    }

    public static int launchJvm(ScheduledProcess scheduledProcess, WorkerConfiguration workerConfiguration) {
        try {
            LOGGER.info(String.format("Launching JVM for the process [%s]", scheduledProcess.getProcessId()));
            List<String> command = createCommandJVMArgs(scheduledProcess, workerConfiguration);
            ProcessBuilder pb = new ProcessBuilder(command);
            LOGGER.info(String.format("Starting process %s for plugin %s'", scheduledProcess.getProcessId(), scheduledProcess.getPluginId()));
            Process processR = pb.start();
            int exitVal = processR.waitFor();
            if (exitVal != 0) {
                InputStream errorStream = processR.getErrorStream();
                String s = IOUtils.toString(errorStream, "UTF-8");
                LOGGER.info(s);
            }
            LOGGER.info(String.format("Return value of the exiting process [%s]:%d'", scheduledProcess.getProcessId(), exitVal));
            return exitVal;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return -1;
        }
    }

    private static List<String> createCommandJVMArgs(ScheduledProcess scheduledProcess, WorkerConfiguration workerConfiguration) throws IOException {
        Path argFile = generateJvmArgsFile(scheduledProcess, workerConfiguration);
        return List.of("java", "@" + argFile.toAbsolutePath());
    }

    private static Path generateJvmArgsFile(ScheduledProcess scheduledProcess, WorkerConfiguration workerConfiguration) throws IOException {
        List<String> args = new ArrayList<>();

        args.add("-Duser.home=" + System.getProperty("user.home"));
        args.add("-Dfile.encoding=UTF-8");

        // JVM args ze ScheduledProcess
        List<String> javaProcessParameters = scheduledProcess.getJvmArgs();
        if (javaProcessParameters != null) {
            args.addAll(javaProcessParameters);
        }

        // Base64 configs
        ObjectMapper mapper = new ObjectMapper();

        String workerConfigJson = mapper.writeValueAsString(workerConfiguration.getAll());
        String encodedWorkerConfig = Base64.getEncoder().encodeToString(workerConfigJson.getBytes(StandardCharsets.UTF_8));
        args.add("-D" + WORKER_CONFIG_BASE_64 + "=" + encodedWorkerConfig);

        ProcessConfiguration processConfiguration = new ProcessConfiguration();
        convert(scheduledProcess, processConfiguration);
        File processWorkingDir = prepareProcessWorkingDirectory(workerConfiguration.getWorkerId(),
                scheduledProcess.getProcessId() + "");
        File standardStreamFile = standardOutFile(processWorkingDir);
        File errStreamFile = errorOutFile(processWorkingDir);
        processConfiguration.set(SOUT_FILE_KEY, standardStreamFile.getAbsolutePath());
        processConfiguration.set(SERR_FILE_KEY, errStreamFile.getAbsolutePath());

        String processConfigJson = mapper.writeValueAsString(processConfiguration.getAll());
        String encodedProcessConfig = Base64.getEncoder().encodeToString(processConfigJson.getBytes(StandardCharsets.UTF_8));
        args.add("-D" + PROCESS_CONFIG_BASE64_KEY + "=" + encodedProcessConfig);

        // Classpath
        args.add("-cp");
        args.add(workerConfiguration.getStarterClasspath());

        // Main class
        args.add(PluginStarter.class.getName());

        // write to the file
        Path argsFile = Files.createTempFile("jvm", ".args");
        Files.write(argsFile, args, StandardCharsets.UTF_8);
        return argsFile;
    }


    private static void convert(ScheduledProcess scheduledProcess, ProcessConfiguration processConfiguration) throws JsonProcessingException {
        processConfiguration.set(MAIN_CLASS_KEY, scheduledProcess.getMainClass());
        processConfiguration.set(PLUGIN_ID_KEY, scheduledProcess.getPluginId());
        processConfiguration.set(PROCESS_ID_KEY, scheduledProcess.getProcessId());
        processConfiguration.set(BATCH_ID_KEY, scheduledProcess.getBatchId());
        String payloadJson = new ObjectMapper().writeValueAsString(scheduledProcess.getPayload());
        String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        processConfiguration.set(PLUGIN_PAYLOAD_BASE64_KEY, encodedPayload);
    }

}
