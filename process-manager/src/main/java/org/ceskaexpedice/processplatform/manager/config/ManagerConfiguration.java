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
package org.ceskaexpedice.processplatform.manager.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.ceskaexpedice.processplatform.worker.utils.Utils.parseSimpleJson;

/**
 * WorkerConfiguration
 * @author ppodsednik
 */
public class ManagerConfiguration {
    public static final String WORKING_DIR = System.getProperty("user.home") + File.separator + ".processplatform";
    public static final String DEFAULT_MANAGER_WORKDIR = WORKING_DIR + File.separator + "manager";

    public static final String WORKER_URL_PREFIX = "worker.";
    public static final String WORKER_URL_SUFFIX = ".url";

    private final Properties props;

    public ManagerConfiguration(Properties props) {
        this.props = props;
    }

    /**
     * Returns a mapping of workerId to base URL
     */
    public Map<String, String> getWorkerUrlMap() {
        return props.entrySet().stream()
                .filter(entry -> {
                    String key = entry.getKey().toString();
                    return key.startsWith(WORKER_URL_PREFIX) && key.endsWith(WORKER_URL_SUFFIX);
                })
                .collect(Collectors.toMap(
                        entry -> extractWorkerId(entry.getKey().toString()),
                        entry -> entry.getValue().toString()
                ));
    }

    /**
     * Extracts workerId from key like "worker.worker-1.url"
     */
    private String extractWorkerId(String key) {
        return key.substring(WORKER_URL_PREFIX.length(), key.length() - WORKER_URL_SUFFIX.length());
    }

    public String getWorkerUrl(String workerId) {
        return getWorkerUrlMap().get(workerId);
    }

    // Optional: default workdir if you want to use it for anything
    public String getDefaultWorkdir() {
        return DEFAULT_MANAGER_WORKDIR;
    }
    /*
    worker.worker-1.url = http://localhost:8081
worker.worker-2.url = http://10.0.0.5:8081

ManagerConfiguration config = new ManagerConfiguration(loadProps());
String url = config.getWorkerUrl("worker-1");

     */
}