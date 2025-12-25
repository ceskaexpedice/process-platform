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
    public static final String CONFIG_FILE = "worker.properties";
    public static final String WORKING_DIR = System.getProperty("user.home") + File.separator + ".processplatform";
    public static final String WORKER_CONFIG_BASE_64 = "workerConfigBase64";
    private static final String TOMCAT_HOME = System.getProperty("catalina.home");

    private static final String WORKER_LOOP_SLEEP_SEC_KEY = "WORKER_LOOP_SLEEP_SECS";
    private static final String PLUGIN_PATH_KEY = "PLUGIN_PATH";
    private static final String STARTER_CLASSPATH_KEY = "STARTER_CLASSPATH";
    private static final String MANAGER_BASE_URL_KEY = "MANAGER_BASE_URL";
    private static final String WORKER_BASE_URL_KEY = "WORKER_BASE_URL";
    private static final String WORKER_ID_KEY = "WORKER_ID";

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

    // ---------- effective configuration

    public double getWorkerLoopSleepSecs() {
        String sleepSecKey = get(WORKER_LOOP_SLEEP_SEC_KEY);
        if (sleepSecKey != null) {
            return Double.parseDouble(sleepSecKey);
        }else {
            return 1;
        }
    }

    public void setWorkerLoopSleepSecs(int  sleepSec) {
        set(WORKER_LOOP_SLEEP_SEC_KEY, String.valueOf(sleepSec));
    }

    public File getPluginDirectory() {
        return new File(getPluginPath());
    }

    public void setPluginDirectory(String pluginDirectory) {
        set(PLUGIN_PATH_KEY, pluginDirectory);
    }

    private String getPluginPath() {
        String defaultVal = TOMCAT_HOME != null
                ? TOMCAT_HOME + File.separator + "lib" + File.separator + "plugins"
                : "lib" + File.separator + "plugins";
        String configured = get(WorkerConfiguration.PLUGIN_PATH_KEY);
        if (configured != null && new File(configured).exists()) {
            return configured;
        }
        return defaultVal;
    }

    public String getManagerBaseUrl() {
        String managerUrl = get(WorkerConfiguration.MANAGER_BASE_URL_KEY);
        if (managerUrl != null && !managerUrl.isEmpty()) {
            return managerUrl.endsWith("/") ? managerUrl : managerUrl + "/";
        }
        return "http://localhost:8080/";
    }

    public void setManagerBaseUrl(String  managerBaseUrl) {
        set(MANAGER_BASE_URL_KEY, managerBaseUrl);
    }

    public String getStarterClasspath() {
        return get(STARTER_CLASSPATH_KEY);
    }

    public void setStarterClasspath(String  starterClasspath) {
        set(STARTER_CLASSPATH_KEY, String.valueOf(starterClasspath));
    }

    public String getWorkerBaseUrl() {
        return get(WORKER_BASE_URL_KEY);
    }

    public void setWorkerBaseUrl(String workerBaseUrl) {
        set(WORKER_BASE_URL_KEY, workerBaseUrl);
    }

    public String getWorkerId() {
        return get(WORKER_ID_KEY);
    }

    public void setWorkerId(String workerId) {
        set(WORKER_ID_KEY, workerId);
    }

    public static WorkerConfiguration decodeWorkerConfig() {
        String workerConfigBase64 = System.getProperty(WORKER_CONFIG_BASE_64);
        String workerConfigJson = new String(Base64.getDecoder().decode(workerConfigBase64), StandardCharsets.UTF_8);
        Map<String, String> workerProps = parseSimpleJson(workerConfigJson);
        WorkerConfiguration workerConfig = new WorkerConfiguration(workerProps);
        return workerConfig;
    }

}
