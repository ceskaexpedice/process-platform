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
package org.ceskaexpedice.processplatform.worker.utils;

import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Utils {

    private Utils() {}

    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
            if (!json.isEmpty()) {
                String[] entries = json.split(",");
                for (String entry : entries) {
                    String[] keyValue = entry.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                        String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
                        map.put(key, value);
                    }
                }
            }
        }
        return map;
    }

    public static void setStarterClasspath(ServletContextEvent sce, WorkerConfiguration config) {
        String libPath = sce.getServletContext().getRealPath("/WEB-INF/lib");
        String starterClasspath = buildClasspath(libPath);
        config.set("starter.classpath", starterClasspath);
        String envClasspath = System.getenv("STARTER_CLASSPATH");
        if (envClasspath != null) {
            config.set("starter.classpath", envClasspath);
        }
    }

    private static String buildClasspath(String libDir) {
        File dir = new File(libDir);
        StringBuilder classpath = new StringBuilder();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files != null) {
            for (File jar : files) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(jar.getAbsolutePath());
            }
        }
        return classpath.toString();
    }

    public static PluginInfoTO toTO(PluginInfo pluginInfo) {
        /*
        List<PluginProfileTO> profiles = pluginInfo.getProfiles().stream()
                .map(profile -> new PluginProfileTO(
                        profile.getProfileId(),
                        pluginInfo.getPluginId(),
                        profile.getStaticParams(),
                        profile.getJvmArgs()
                ))
                .collect(Collectors.toList());

        return new PluginInfoTO(
                pluginInfo.getPluginId(),
                pluginInfo.getDescription(),
                pluginInfo.getMainClass(),
                profiles
        );

         */ return null;
    }

}
