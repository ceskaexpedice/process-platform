/*
 * Copyright (C) 2025  Inovatika
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

public class EffectiveWorkerConfiguration {

    private static final String TOMCAT_HOME = System.getProperty("catalina.home");
    private static final String MANAGER_BASE_URL = System.getenv("MANAGER_BASE_URL");

    private final WorkerConfiguration base;

    public EffectiveWorkerConfiguration(WorkerConfiguration base) {
        this.base = base;
    }

    private String getPluginPath() {
        String defaultVal = TOMCAT_HOME != null
                ? TOMCAT_HOME + File.separator + "lib" + File.separator + "plugins"
                : "lib" + File.separator + "plugins";

        String configured = base.get(WorkerConfiguration.PLUGIN_PATH_KEY);
        if (configured != null && new File(configured).exists()) {
            return configured;
        }

        return defaultVal;
    }

    public File getPluginDirectory() {
        return new File(getPluginPath());
    }


    public String getManagerBaseUrl() {
        if (MANAGER_BASE_URL != null && !MANAGER_BASE_URL.isEmpty()) {
            return MANAGER_BASE_URL.endsWith("/") ? MANAGER_BASE_URL : MANAGER_BASE_URL + "/";
        }
        String propUrl = base.get(WorkerConfiguration.MANAGER_BASE_URL_KEY);
        if (propUrl != null && !propUrl.isEmpty()) {
            return propUrl.endsWith("/") ? propUrl : propUrl + "/";
        }
        return "http://localhost:8080/";
    }

}
