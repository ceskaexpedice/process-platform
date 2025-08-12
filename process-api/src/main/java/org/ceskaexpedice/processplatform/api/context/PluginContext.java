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


import org.ceskaexpedice.processplatform.common.model.ScheduleSubProcess;

/**
 * Provides context access for Java plugins to communicate with the Process Platform.
 * <p>
 * This interface is implemented by the framework and passed to plugins at runtime.
 * Plugins can use it to report updates or request actions such as scheduling subprocesses.
 * @author ppodsednik
 */
public interface PluginContext {

    /**
     * Updates the name of the currently running process.
     * <p>
     * This method allows the plugin to change the process name that is visible
     * in logs, monitoring tools, or the user interface.
     *
     * @param name the new name of the process
     */
    void updateProcessName(String name);

    /**
     * Requests the framework to schedule a new subprocess.
     * <p>
     * The provided {@link ScheduleSubProcess} object defines the details
     * of the subprocess to be executed. The framework is responsible for
     * handling the scheduling and execution.
     *
     * @param scheduleSubProcess an object containing the configuration of the subprocess
     */
    void scheduleSubProcess(ScheduleSubProcess scheduleSubProcess);
}