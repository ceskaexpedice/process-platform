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
package org.ceskaexpedice.processplatform.api;

import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;

import java.util.Map;
import java.util.Set;

/**
 * Service Provider Interface (SPI) for plugins.
 * <p>
 * Plugins implement this interface to provide metadata and specifications
 * to the framework. It is typically discovered and loaded using
 * {@code java.util.ServiceLoader}.
 * </p>
 * <p>
 * Implementations must provide a unique plugin identifier, a description,
 * the main class to launch, and specifications for the payload and scheduled profiles.
 * </p>
 */
public interface PluginSpi {

    /**
     * Returns the unique identifier of the plugin.
     * <p>
     * Example: {@code "import"}
     *
     * @return the plugin ID string
     */
    String getPluginId();

    /**
     * Returns a short human-readable description of the plugin.
     * <p>
     * Example: {@code "Imports FOXML into system"}
     *
     * @return the plugin description
     */
    String getDescription();

    /**
     * Returns the fully qualified name of the plugin's main class.
     * <p>
     * This class typically contains the plugin entry point.
     * Example: {@code "cz.kramerius.plugin.importer.Main"}
     *
     * @return the fully qualified main class name
     */
    String getMainClass();

    /**
     * Returns the specification of payload fields expected by the plugin.
     * <p>
     * The map keys are payload field names, and the values describe
     * the field specifications.
     *
     * @return a map of payload field names to their specifications
     */
    Map<String, PayloadFieldSpec> getPayloadSpec();

    /**
     * Returns the set of scheduled profile names supported by this plugin.
     * <p>
     * Scheduled profiles represent predefined configuration or behavior profiles
     * that can be triggered on schedule by the framework.
     *
     * @return a set of profile names supported for scheduling
     */
    Set<String> getScheduledProfiles();
}