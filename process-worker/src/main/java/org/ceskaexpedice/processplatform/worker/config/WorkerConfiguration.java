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
package org.ceskaexpedice.processplatform.worker.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.utils.Utils.parseSimpleJson;

/**
 * WorkerConfiguration
 * @author ppodsednik
 */
public class WorkerConfiguration {
    public static final String WORKING_DIR = System.getProperty("user.home") + File.separator + ".processplatform";
    public static final String WORKER_CONFIG_BASE64_KEY = "workerConfigBase64";

    public static final String WORKER_LOOP_SLEEP_SEC_KEY = "workerLoopSleepSec";

    public static final String PLUGIN_PATH_KEY = "pluginPath";
    public static final String STARTER_CLASSPATH_KEY = "starterClasspath";
    public static final String MANAGER_BASE_URL_KEY = "managerBaseUrl";
    public static final String WORKER_BASE_URL_KEY = "workerBaseUrl";
    public static final String WORKER_PROFILES_KEY = "workerProfiles";
    public static final String WORKER_ID_KEY = "workerId";

    private final Properties props = new Properties();

    public WorkerConfiguration(Properties fileProps) {
        // Load from environment first
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
        }

        // Add properties from file (only if not already set by env)
        for (String name : fileProps.stringPropertyNames()) {
            props.putIfAbsent(name, fileProps.getProperty(name));
        }
    }

    // Overloaded constructor for Map-based initialization
    public WorkerConfiguration(Map<String, String> directProps) {
        props.putAll(directProps);
    }

    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            result.put(name, props.getProperty(name));
        }
        return result;
    }

    /**
     * Gets a property value by key.
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Allows programmatic setting of a property.
     * This is useful for tests or for dynamic overrides.
     * @param key the property key
     * @param value the property value
     */
    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    /**
     * Convenience method to get a property with a default fallback.
     * @param key the property key
     * @param defaultValue the value to return if key is not found
     * @return the property value or default
     */
    public String getOrDefault(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static WorkerConfiguration getWorkerConfig() {
        String workerConfigBase64 = System.getProperty(WORKER_CONFIG_BASE64_KEY);
        String workerConfigJson = new String(Base64.getDecoder().decode(workerConfigBase64), StandardCharsets.UTF_8);
        Map<String, String> workerProps = parseSimpleJson(workerConfigJson);
        WorkerConfiguration workerConfig = new WorkerConfiguration(workerProps);
        return workerConfig;
    }

}
