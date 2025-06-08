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
import org.ceskaexpedice.processplatform.worker.plugin.utils.PluginsLoader;
import org.ceskaexpedice.processplatform.worker.plugin.utils.ReflectionUtils;

import java.io.File;
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

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ProcessStarter <processName> <payload>");
            System.exit(1);
        }

        String pluginPath = args[0];  // e.g., "import"
        String pluginName = args[1];  // e.g., "import"
        String mainClassName = args[2];  // e.g., "import"

        try {
            String encodedPayload = args[3];
            String payloadJson = new String(Base64.getDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            //Map<String, String> payload = new ObjectMapper().readValue(payloadJson, new TypeReference<>() {});
            Map<String, String> payload = parseSimpleJson(payloadJson);

            PluginContextHolder.setContext(new PluginStarter());
            ClassLoader loader = PluginsLoader.createPluginClassLoader(new File(pluginPath), pluginName);
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> clz = loader.loadClass(mainClassName);
                ReflectionUtils.MethodType processMethod = annotatedMethodType(clz);
                if (processMethod == null) {
                    processMethod = mainMethodType(clz);
                }

               // processMethod = mainMethodType(clz);


                if (processMethod.getType() == ReflectionUtils.MethodType.Type.ANNOTATED) {
                    try {
                        Object[] params = ReflectionUtils.map(processMethod.getMethod(), args, payload);
                        processMethod.getMethod().invoke(null, params);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Object[] objs = new Object[1];
                        objs[0] = args;
                        processMethod.getMethod().invoke(null, objs);
                        //processMethod.getMethod().invoke(null, (Object) args);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }
            //String pid = getPID();
            //updatePID(pid);
        } catch (Throwable e) {
            System.err.println("Failed to start process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskState(String taskId, String taskState) {
        // TODO must be able to call worker's PluginEndpoint
    }

    @Override
    public void updateTaskPid(String taskId, String pid) {

    }

    @Override
    public void updateTaskName(String taskId, String name) {

    }

    @Override
    public void scheduleProcess(String processDefinition) {

    }


}
