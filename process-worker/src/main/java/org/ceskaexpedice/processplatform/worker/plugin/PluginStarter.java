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

import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.logging.LoggingLoader;
import org.ceskaexpedice.processplatform.worker.plugin.utils.PluginsLoader;
import org.ceskaexpedice.processplatform.worker.plugin.utils.ReflectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.ceskaexpedice.processplatform.worker.plugin.utils.ReflectionUtils.annotatedMethodType;
import static org.ceskaexpedice.processplatform.worker.plugin.utils.ReflectionUtils.mainMethodType;
import static org.ceskaexpedice.processplatform.worker.utils.Utils.parseSimpleJson;

/**
 * PluginStarter
 * @author ppodsednik
 */
public class PluginStarter implements PluginContext {
    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(PluginStarter.class.getName());
    public static final String LOGGING_FILE_PROPERTY = "java.util.logging.config.file";
    public static final String LOGGING_CLASS_PROPERTY = "java.util.logging.config.class";

    public static final String SOUT_FILE = "SOUT";
    public static final String SERR_FILE = "SERR";

    private final WorkerConfiguration workerConfig;

    public PluginStarter(Map<String, String> workerProps) {
        this.workerConfig = new WorkerConfiguration(workerProps);
    }

    public static void main(String[] args) {
        /* TODO
        if (args.length < 4) {
            System.err.println("Usage: PluginStarter <pluginPath> <pluginName> <mainClassName> <payloadBase64> [pluginArgs...]");
            System.exit(1);
        }*/

        String workerConfigBase64 = args[0];
        String pluginId = args[1];
        String profileId = args[2];
        String mainClassName = args[3];
        String payloadBase64 = args[4];

        PrintStream outStream = null;
        PrintStream errStream = null;
        try {
            outStream = createPrintStream(System.getProperty(SOUT_FILE));
            errStream = createPrintStream(System.getProperty(SERR_FILE));
            System.setErr(errStream);
            System.setOut(outStream);

            setDefaultLoggingIfNecessary();

            LOGGER.info("STARTING PROCESS WITH USER HOME:"+System.getProperty("user.home"));
            LOGGER.info("STARTING PROCESS WITH FILE ENCODING:"+System.getProperty("file.encoding"));


            String payloadJson = new String(Base64.getDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            Map<String, String> payload = parseSimpleJson(payloadJson);
            String workerConfigJson = new String(Base64.getDecoder().decode(workerConfigBase64), StandardCharsets.UTF_8);
            Map<String, String> workerProps = parseSimpleJson(workerConfigJson);
            // Set plugin context
            PluginContextHolder.setContext(new PluginStarter(workerProps));

            // Load plugin class
            ClassLoader loader = PluginsLoader.createPluginClassLoader(new File(workerProps.get("pluginPath")), pluginId);
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> clz = loader.loadClass(mainClassName);
                ReflectionUtils.MethodType processMethod = annotatedMethodType(clz);
                if (processMethod == null) {
                    processMethod = mainMethodType(clz);
                }

                // TODO Everything after index 4 is plugin args
                String[] pluginArgs = Arrays.copyOfRange(args, 1, args.length);
                if (processMethod.getType() == ReflectionUtils.MethodType.Type.ANNOTATED) {
                    Object[] params = ReflectionUtils.map(processMethod.getMethod(), pluginArgs, payload);
                    processMethod.getMethod().invoke(null, params);
                } else {
                    processMethod.getMethod().invoke(null, (Object) pluginArgs);
                }

            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }
        } catch (Throwable e) {
            System.err.println("Failed to start plugin process: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }

    @Override
    public void updateTaskState(String taskId, String taskState) {
        // TODO: Implement REST call to worker's PluginEndpoint
    }

    @Override
    public void updateTaskPid(String taskId, String pid) {

    }

    @Override
    public void updateTaskName(String taskId, String name) {

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

}
