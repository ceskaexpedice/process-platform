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

import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.WarningException;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.ScheduleSubProcess;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.client.ManagerClientFactory;
import org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;
import org.ceskaexpedice.processplatform.worker.utils.ReflectionUtils;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration.*;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.decodeWorkerConfig;
import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.createPrintStream;
import static org.ceskaexpedice.processplatform.worker.utils.ReflectionUtils.annotatedMethodType;
import static org.ceskaexpedice.processplatform.worker.utils.Utils.getPid;
import static org.ceskaexpedice.processplatform.worker.utils.logging.LoggingUtils.setDefaultLoggingIfNecessary;

/**
 * PluginStarter
 * @author ppodsednik
 */
public class PluginStarter implements PluginContext {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(PluginStarter.class.getName());

    private final WorkerConfiguration workerConfiguration;
    private final ProcessConfiguration processConfiguration;
    private final ManagerClient managerClient;

    public PluginStarter(WorkerConfiguration workerConfiguration, ProcessConfiguration processConfiguration) {
        this.workerConfiguration = workerConfiguration;
        this.processConfiguration = processConfiguration;
        this.managerClient = ManagerClientFactory.createManagerClient(workerConfiguration);
    }

    public static void main(String[] args) {
        ProcessConfiguration processConfig = getProcessConfig();
        WorkerConfiguration workerConfig = decodeWorkerConfig();
        ManagerClient managerClient = null;

        PrintStream outStream = null;
        PrintStream errStream = null;
        try {
            outStream = createPrintStream(processConfig.get(SOUT_FILE_KEY));
            errStream = createPrintStream(processConfig.get(SERR_FILE_KEY));
            System.setErr(errStream);
            System.setOut(outStream);
            setDefaultLoggingIfNecessary();
            LOGGER.info("STARTING PROCESS WITH USER HOME:" + System.getProperty("user.home"));

            PluginContext pluginContext = PluginContextFactory.createPluginContext(workerConfig, processConfig);
            PluginContextHolder.setContext(pluginContext);
            managerClient = ManagerClientFactory.createManagerClient(workerConfig);
            updatePID(processConfig, managerClient);

            updateProcessState(ProcessState.RUNNING, managerClient, processConfig);
            runPlugin(processConfig, workerConfig);
            updateProcessState(ProcessState.FINISHED, managerClient, processConfig);
        } catch (WarningException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                updateProcessState(ProcessState.WARNING, managerClient, processConfig);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e1) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            if (errStream != null) {
                try {
                    errStream.close();
                } catch (Exception e1) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                updateProcessState(ProcessState.FAILED, managerClient, processConfig);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (Exception e1) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            if (errStream != null) {
                try {
                    errStream.close();
                } catch (Exception e1) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } finally {
            // nothing yet
        }
    }

    @Override
    public void updateProcessName(String name) {
        managerClient.updateProcessDescription(processConfiguration.get(PROCESS_ID_KEY), name);
    }

    @Override
    public void scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        scheduleSubProcess.setBatchId(processConfiguration.get(BATCH_ID_KEY));
        managerClient.scheduleSubProcess(scheduleSubProcess);
    }

    private static void updateProcessState(ProcessState processState, ManagerClient managerClient, ProcessConfiguration processConfig) {
        managerClient.updateProcessState(processConfig.get(PROCESS_ID_KEY), processState);
    }

    private static void updatePID(ProcessConfiguration processConfiguration, ManagerClient managerClient) {
        String pid = getPid();
        managerClient.updateProcessPid(processConfiguration.get(PROCESS_ID_KEY), pid);
    }

    private static void runPlugin(ProcessConfiguration processConfig, WorkerConfiguration workerConfig)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Map<String, String> pluginPayload = getPluginPayload(processConfig);
        String pluginId = processConfig.get(PLUGIN_ID_KEY);
        String mainClass = processConfig.get(MAIN_CLASS_KEY);
        ClassLoader loader = PluginsLoader.createPluginClassLoader(workerConfig.getPluginDirectory(), pluginId);
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> clz = loader.loadClass(mainClass);
            ReflectionUtils.MethodType processMethod = annotatedMethodType(clz);
            if (processMethod == null) {
                throw new ApplicationException("Could not find process method for class: " + mainClass);
            }
            Object[] params = ReflectionUtils.map(processMethod.getMethod(), new String[]{}, pluginPayload);
            processMethod.getMethod().invoke(null, params);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

}
