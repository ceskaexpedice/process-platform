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
import org.ceskaexpedice.processplatform.common.to.PluginProfileTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.PluginInfo;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static PluginInfoTO toTO(PluginInfo pluginInfo) {
        List<PluginProfileTO> profiles = pluginInfo.getProfiles().stream()
                .map(profile -> new PluginProfileTO(
                        profile.getProfileId(),
                        pluginInfo.getPluginId(),
                        profile.getJvmArgs()
                ))
                .collect(Collectors.toList());

        return new PluginInfoTO(
                pluginInfo.getPluginId(),
                pluginInfo.getDescription(),
                pluginInfo.getMainClass(),
                pluginInfo.getPayloadFieldSpecMap(),
                profiles
        );
    }

    public static File prepareProcessWorkingDirectory(String processId) {
        String value = WorkerConfiguration.DEFAULT_WORKER_WORKDIR + File.separator + processId;
        File processWorkingDir = new File(value);
        if (!processWorkingDir.exists()) {
            boolean mkdirs = processWorkingDir.mkdirs();
            if (!mkdirs){
                throw new RuntimeException("cannot create directory '" + processWorkingDir.getAbsolutePath() + "'");
            }
        }
        return processWorkingDir;
    }

    public static File errorOutFile(File processWorkingDir) {
        return new File(createFolderIfNotExists(processWorkingDir + File.separator + "plgErr"),"sterr.err");
    }

    public static File standardOutFile(File processWorkingDir) {
        return new File(createFolderIfNotExists(processWorkingDir + File.separator + "plgOut"),"stout.out");
    }

    private static File createFolderIfNotExists(String folder) {
        File fldr = new File(folder);
        if (!fldr.exists()) {
            boolean mkdirs = fldr.mkdirs();
            if (!mkdirs){
                throw new RuntimeException("cannot create directory '" + fldr.getAbsolutePath() + "'");
            }
        }
        return fldr;
    }


}
