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
package org.ceskaexpedice.processplatform.worker.startprocess;

import org.ceskaexpedice.processplatform.api.PluginContext;
import org.ceskaexpedice.processplatform.api.PluginContextHolder;

import java.lang.reflect.Method;

public class ProcessStarter implements PluginContext {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ProcessStarter <processName> <payload>");
            System.exit(1);
        }

        String processName = args[0];  // e.g., "import"
        String payload = args[1];      // JSON or command string

        try {
            PluginContextHolder.setContext(new ProcessStarter());
            ClassLoader loader = PluginLoader.loadProcessClassLoader(processName);
            String mainClassName = "org.ceskaexpedice.processplatform.processes." + processName + "." + capitalize(processName) + "Main";

            Class<?> mainClass = loader.loadClass(mainClassName);
            Method mainMethod = mainClass.getMethod("main", String[].class);

            mainMethod.invoke(null, (Object) new String[]{ payload });

        } catch (Exception e) {
            System.err.println("Failed to start process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public String callRestApi(String endpoint, String payload) {
        // call manager ProcessTaskResource
        return "";
    }
}
