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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.utils.Utils.parseSimpleJson;

/**
 * ProcessConfiguration
 * @author ppodsednik
 */
public class ProcessConfiguration {
    public static final String PROCESS_CONFIG_BASE64_KEY = "processConfigBase64";
    public static final String MAIN_CLASS_KEY = "mainClass";
    public static final String PLUGIN_ID_KEY = "pluginId";
    public static final String PROCESS_ID_KEY = "processId";
    public static final String BATCH_ID_KEY = "batchId";
    public static final String PLUGIN_PAYLOAD_BASE64_KEY = "pluginPayloadBase64";
    public static final String SOUT_FILE_KEY = "SOUT";
    public static final String SERR_FILE_KEY = "SERR";

    private final Properties props = new Properties();

    public ProcessConfiguration() {
    }

    // Overloaded constructor for Map-based initialization
    public ProcessConfiguration(Map<String, String> directProps) {
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

    public static Map<String, String> getPluginPayload(ProcessConfiguration processConfig) {
        String payloadBase64 = processConfig.get(PLUGIN_PAYLOAD_BASE64_KEY);
        String payloadJson = new String(Base64.getDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
        Map<String, String> pluginPayload = parseSimpleJson(payloadJson);
        return pluginPayload;
    }

    public static ProcessConfiguration getProcessConfig() {
        String processConfigBase64 = System.getProperty(PROCESS_CONFIG_BASE64_KEY);
        String processConfigJson = new String(Base64.getDecoder().decode(processConfigBase64), StandardCharsets.UTF_8);
        Map<String, String> processProps = parseSimpleJson(processConfigJson);
        ProcessConfiguration processConfig = new ProcessConfiguration(processProps);
        return processConfig;
    }

}
