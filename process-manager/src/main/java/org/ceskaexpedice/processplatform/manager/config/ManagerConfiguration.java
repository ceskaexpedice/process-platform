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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * WorkerConfiguration
 * @author ppodsednik
 */
public class ManagerConfiguration {
    private static final String WORKER_URL_KEY_PREFIX = "worker.";
    private static final String WORKER_URL_KEY_SUFFIX = ".url";
    public static final String JDBC_URL_KEY = "jdbcUrl";
    public static final String JDBC_USER_NAME_KEY = "jdbcUsername";
    public static final String JDBC_USER_PASSWORD_KEY = "jdbcPassword";

    private final Properties props = new Properties();

    public ManagerConfiguration(Properties fileProps) {
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
    public ManagerConfiguration(Map<String, String> directProps) {
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

    public String getWorkerUrl(String workerId) {
        return getWorkerUrlMap().get(workerId);
    }

    /**
     * Returns a mapping of workerId to base URL
     */
    private Map<String, String> getWorkerUrlMap() {
        return props.entrySet().stream()
                .filter(entry -> {
                    String key = entry.getKey().toString();
                    return key.startsWith(WORKER_URL_KEY_PREFIX) && key.endsWith(WORKER_URL_KEY_SUFFIX);
                })
                .collect(Collectors.toMap(
                        entry -> extractWorkerId(entry.getKey().toString()),
                        entry -> entry.getValue().toString()
                ));
    }

    /**
     * Extracts workerId from key like "worker.worker-1.url"
     * Example:
     * worker.worker-1.url = http://localhost:8081
     * worker.worker-2.url = http://10.0.0.5:8081
     */
    private String extractWorkerId(String key) {
        return key.substring(WORKER_URL_KEY_PREFIX.length(), key.length() - WORKER_URL_KEY_SUFFIX.length());
    }

}