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

package org.ceskaexpedice.processplatform.worker.plugin.executor;

import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

/**
 * PluginContextFactory
 * @author ppodsednik
 */
public final class PluginContextFactory {

    private PluginContextFactory() {
    }

    public static PluginContext createPluginContext(WorkerConfiguration configuration, ProcessConfiguration processConfiguration) {
        PluginStarter pluginStarter = new PluginStarter(configuration, processConfiguration);
        return pluginStarter;
    }

}
