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

import org.ceskaexpedice.processplatform.api.WarningException;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.api.ProcessState;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.utils.logging.LoggingLoader;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.PLUGIN_PATH_KEY;
import static org.ceskaexpedice.processplatform.worker.plugin.executor.ReflectionUtils.annotatedMethodType;
import static org.ceskaexpedice.processplatform.worker.utils.Utils.parseSimpleJson;

/**
 * PluginStarter
 * @author ppodsednik
 */
public class PluginStarter implements PluginContext {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(PluginStarter.class.getName());
    private static final String LOGGING_FILE_PROPERTY = "java.util.logging.config.file";
    private static final String LOGGING_CLASS_PROPERTY = "java.util.logging.config.class";

    public static final String MAIN_CLASS_KEY = "mainClass";
    public static final String PLUGIN_ID_KEY = "pluginId";
    public static final String UUID_KEY = "uuid";
    public static final String WORKER_CONFIG_BASE64_KEY = "workerConfigBase64";
    public static final String PLUGIN_PAYLOAD_BASE64_KEY = "pluginPayloadBase64";
    public static final String SOUT_FILE_KEY = "SOUT";
    public static final String SERR_FILE_KEY = "SERR";

    private final WorkerConfiguration workerConfig;

    public PluginStarter(WorkerConfiguration workerConfiguration) {
        this.workerConfig = workerConfiguration;
    }

    public static void main(String[] args) {
        String mainClass = System.getProperty(MAIN_CLASS_KEY);
        String pluginId = System.getProperty(PLUGIN_ID_KEY);

        String workerConfigBase64 = System.getProperty(WORKER_CONFIG_BASE64_KEY);
        String workerConfigJson = new String(Base64.getDecoder().decode(workerConfigBase64), StandardCharsets.UTF_8);
        Map<String, String> workerProps = parseSimpleJson(workerConfigJson);
        WorkerConfiguration workerConfig = new WorkerConfiguration(workerProps);

        String payloadBase64 = System.getProperty(PLUGIN_PAYLOAD_BASE64_KEY);
        String payloadJson = new String(Base64.getDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
        Map<String, String> pluginPayload = parseSimpleJson(payloadJson);

        PrintStream outStream = null;
        PrintStream errStream = null;
        try {
            outStream = createPrintStream(System.getProperty(SOUT_FILE_KEY));
            errStream = createPrintStream(System.getProperty(SERR_FILE_KEY));
            System.setErr(errStream);
            System.setOut(outStream);
            setDefaultLoggingIfNecessary();
            LOGGER.info("STARTING PROCESS WITH USER HOME:"+System.getProperty("user.home"));
            LOGGER.info("STARTING PROCESS WITH FILE ENCODING:"+System.getProperty("file.encoding"));

            String pid = getPID();
            updatePID(pid);

            PluginContextHolder.setContext(new PluginStarter(workerConfig));
            runPlugin(pluginId, mainClass, pluginPayload, workerConfig);
            checkErrorFile();
            PluginContextHolder.getContext().updateProcessState(ProcessState.FINISHED);
        } catch (WarningException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                PluginContextHolder.getContext().updateProcessState(ProcessState.WARNING);
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
                PluginContextHolder.getContext().updateProcessState(ProcessState.FAILED);
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
            // TODO is it necessary to shutdown AkubraRepo here?
            /*
            String uuid = System.getProperty(ProcessStarter.UUID_KEY);
            String closeTokenFlag = System.getProperty(AUTOMATIC_CLOSE_TOKEN, "true");
            if (closeTokenFlag != null && closeTokenFlag.trim().toLowerCase().equals("true")) {
                ProcessUtils.closeToken(uuid);
            }*/
        }
    }

    private static void runPlugin(String pluginId, String mainClass, Map<String, String> pluginPayload, WorkerConfiguration workerConfig)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        ClassLoader loader = PluginsLoader.createPluginClassLoader(new File(workerConfig.get(PLUGIN_PATH_KEY)), pluginId);
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> clz = loader.loadClass(mainClass);
            ReflectionUtils.MethodType processMethod = annotatedMethodType(clz);
            if (processMethod == null) {
                throw new IllegalStateException("Could not find process method for class: " + mainClass);
            }
            String[] pluginArgs = {}; // TODO
            if (processMethod.getType() == ReflectionUtils.MethodType.Type.ANNOTATED) {
                Object[] params = ReflectionUtils.map(processMethod.getMethod(), pluginArgs, pluginPayload);
                processMethod.getMethod().invoke(null, params);
            } else {
                // TODO processMethod.getMethod().invoke(null, (Object) pluginArgs);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    @Override
    public void updateProcessState(ProcessState processState) {
        // TODO: Implement REST call to worker's PluginEndpoint
    }

    @Override
    public void updateProcessName(String name) {

    }

    @Override
    public void scheduleProcess(String processDefinition) {
        System.out.println("TestPlugin1.scheduleProcess:" + processDefinition);
    }

    private static PrintStream createPrintStream(String file) throws FileNotFoundException {
        return new PrintStream(new FileOutputStream(file));
    }

    private static void setDefaultLoggingIfNecessary() {
        String classProperty = System.getProperty(LOGGING_CLASS_PROPERTY);
        String fileProperty = System.getProperty(LOGGING_FILE_PROPERTY);
        if ((classProperty == null) && (fileProperty == null)) {
            // loads default logging
            new LoggingLoader();
        }
    }

    /**
     * Returns PID of process
     *
     * @return
     */
    private static String getPID() {
        String pid = null;
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] split = name.split("@");
        if ((split != null) && (split.length > 1)) {
            pid = split[0];
        }
        return pid;
    }

    private static void updatePID(String pid) {
        // TODO call worker PluginAgent to pass the pid to the manager

        /*
        try {
            PID_UPDATED_BY_ME = ProcessUpdatingChannel.getChannel().updatePID(pid);
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } catch (InstantiationException e) {
            throw new IOException(e);
        }*/
    }

    private static void checkErrorFile() {
        /* TODO
        if (Boolean.getBoolean(ProcessStarter.SHOULD_CHECK_ERROR_STREAM)) {
            String serrFileName = System.getProperty(SERR_FILE);
            File serrFile = new File(serrFileName);
            if (serrFile.length() > 0) throw new WarningException("system error file contains errors");
        }

         */
    }

}
