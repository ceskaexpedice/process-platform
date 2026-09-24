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
    private static final String JDBC_POOL_MAX_SIZE_KEY = "JDBC_POOL_MAX_SIZE";
    private static final String JDBC_POOL_CONNECTION_TIMEOUT_MS_KEY = "JDBC_POOL_CONNECTION_TIMEOUT_MS";
    private static final String JDBC_POOL_VALIDATION_TIMEOUT_MS_KEY = "JDBC_POOL_VALIDATION_TIMEOUT_MS";
    private static final String JDBC_POOL_IDLE_TIMEOUT_MS_KEY = "JDBC_POOL_IDLE_TIMEOUT_MS";
    private static final String JDBC_POOL_MAX_LIFETIME_MS_KEY = "JDBC_POOL_MAX_LIFETIME_MS";
    private static final String JDBC_POOL_KEEPALIVE_TIME_MS_KEY = "JDBC_POOL_KEEPALIVE_TIME_MS";
    private static final String HTTP_CLIENT_MAX_CONNECTIONS_KEY = "HTTP_CLIENT_MAX_CONNECTIONS";
    private static final String HTTP_CLIENT_MAX_CONNECTIONS_PER_ROUTE_KEY = "HTTP_CLIENT_MAX_CONNECTIONS_PER_ROUTE";
    private static final String HTTP_CLIENT_CONNECT_TIMEOUT_MS_KEY = "HTTP_CLIENT_CONNECT_TIMEOUT_MS";
    private static final String HTTP_CLIENT_SOCKET_TIMEOUT_MS_KEY = "HTTP_CLIENT_SOCKET_TIMEOUT_MS";
    private static final String HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT_MS_KEY = "HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT_MS";
    private static final String HTTP_CLIENT_RESPONSE_TIMEOUT_MS_KEY = "HTTP_CLIENT_RESPONSE_TIMEOUT_MS";
    private static final String HTTP_CLIENT_VALIDATE_AFTER_INACTIVITY_MS_KEY = "HTTP_CLIENT_VALIDATE_AFTER_INACTIVITY_MS";
    private static final String HTTP_CLIENT_EVICT_IDLE_CONNECTIONS_MS_KEY = "HTTP_CLIENT_EVICT_IDLE_CONNECTIONS_MS";
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

    public int getJdbcPoolMaxSize() {
        return getInt(JDBC_POOL_MAX_SIZE_KEY, 10);
    }

    public long getJdbcPoolConnectionTimeoutMs() {
        return getLong(JDBC_POOL_CONNECTION_TIMEOUT_MS_KEY, 10000);
    }

    public long getJdbcPoolValidationTimeoutMs() {
        return getLong(JDBC_POOL_VALIDATION_TIMEOUT_MS_KEY, 5000);
    }

    public long getJdbcPoolIdleTimeoutMs() {
        return getLong(JDBC_POOL_IDLE_TIMEOUT_MS_KEY, 300000);
    }

    public long getJdbcPoolMaxLifetimeMs() {
        return getLong(JDBC_POOL_MAX_LIFETIME_MS_KEY, 1500000);
    }

    public long getJdbcPoolKeepaliveTimeMs() {
        return getLong(JDBC_POOL_KEEPALIVE_TIME_MS_KEY, 120000);
    }

    public int getHttpClientMaxConnections() {
        return getInt(HTTP_CLIENT_MAX_CONNECTIONS_KEY, 20);
    }

    public int getHttpClientMaxConnectionsPerRoute() {
        return getInt(HTTP_CLIENT_MAX_CONNECTIONS_PER_ROUTE_KEY, 10);
    }

    public long getHttpClientConnectTimeoutMs() {
        return getLong(HTTP_CLIENT_CONNECT_TIMEOUT_MS_KEY, 10000);
    }

    public long getHttpClientSocketTimeoutMs() {
        return getLong(HTTP_CLIENT_SOCKET_TIMEOUT_MS_KEY, 60000);
    }

    public long getHttpClientConnectionRequestTimeoutMs() {
        return getLong(HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT_MS_KEY, 10000);
    }

    public long getHttpClientResponseTimeoutMs() {
        return getLong(HTTP_CLIENT_RESPONSE_TIMEOUT_MS_KEY, 60000);
    }

    public long getHttpClientValidateAfterInactivityMs() {
        return getLong(HTTP_CLIENT_VALIDATE_AFTER_INACTIVITY_MS_KEY, 10000);
    }

    public long getHttpClientEvictIdleConnectionsMs() {
        return getLong(HTTP_CLIENT_EVICT_IDLE_CONNECTIONS_MS_KEY, 30000);
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

    private int getInt(String key, int defaultValue) {
        String value = get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private long getLong(String key, long defaultValue) {
        String value = get(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

}
