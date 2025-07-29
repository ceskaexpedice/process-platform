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
package org.ceskaexpedice.processplatform.api.context;

/**
 * Holds the {@link PluginContext} instance accessible to the plugin.
 * <p>
 * This class provides a static mechanism for passing the {@code PluginContext}
 * from the framework to the plugin code. The framework is responsible for setting
 * the context before the plugin starts execution. The plugin can then retrieve
 * the context when needed to interact with the framework.
 *
 */
public class PluginContextHolder {

    private static PluginContext context;

    /**
     * Sets the current {@link PluginContext}.
     * <p>
     * This method is intended to be called by the framework before
     * the plugin begins execution.
     *
     * @param ctx the plugin context to set
     */
    public static void setContext(PluginContext ctx) {
        context = ctx;
    }

    /**
     * Returns the current {@link PluginContext}.
     * <p>
     * This method can be called by the plugin to obtain a reference
     * to the framework context and perform actions like updating
     * process metadata or scheduling subprocesses.
     *
     * @return the current plugin context, or {@code null} if not set
     */
    public static PluginContext getContext() {
        return context;
    }
}