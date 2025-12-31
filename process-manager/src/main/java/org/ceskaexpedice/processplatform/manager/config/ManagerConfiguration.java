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


/**
 * WorkerConfiguration
 * @author ppodsednik
 */
public class ManagerConfiguration {
    public static final String CONFIG_FILE = "manager.properties";

    public static final String NODE_TABLE = "pcp_node";
    public static final String PLUGIN_TABLE = "pcp_plugin";
    public static final String PROFILE_TABLE = "pcp_profile";
    public static final String PROCESS_TABLE = "pcp_process";

    private static final String JDBC_URL_KEY = "JDBC_URL";
    private static final String JDBC_USER_NAME_KEY = "JDBC_USERNAME";
    private static final String JDBC_USER_PASSWORD_KEY = "JDBC_PASSWORD";
    private static final String GC_SCHEDULER_CHECK_INTERVAL_KEY = "GC_SCHEDULER_CHECK_INTERVAL";
    private static final String NEXT_SCHEDULED_PROCESS_STRATEGY_KEY = "NEXT_SCHEDULED_PROCESS_STRATEGY";

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

    // --- effective -------------------------------

    public String getJdbcUrl() {
        return get(JDBC_URL_KEY);
    }

    public String getJdbcUsername() {
        return get(JDBC_USER_NAME_KEY);
    }

    public String getJdbcPassword() {
        return get(JDBC_USER_PASSWORD_KEY);
    }

    public int getGcSchedulerCheckInterval() {
        String gcSchedulerCheckInterval = get(GC_SCHEDULER_CHECK_INTERVAL_KEY);
        if (gcSchedulerCheckInterval != null) {
            return Integer.parseInt(gcSchedulerCheckInterval);
        }else {
            return 10000;
        }
    }

    public NextScheduledProcessStrategyType getNextScheduledProcessStrategyType() {
        String value = get(NEXT_SCHEDULED_PROCESS_STRATEGY_KEY);
        if(value != null) {
            return NextScheduledProcessStrategyType.valueOf(value);
        }else{
            return NextScheduledProcessStrategyType.BATCH_AFFINITY;
        }
    }

}