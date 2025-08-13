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
package org.ceskaexpedice.processplatform.worker.testutils;

public final class WorkerTestsUtils {

    public static final String MANAGER_BASE_URI = "http://localhost:9998/process-manager/api/";

    public static final String NODE_WORKER1_ID = "worker1";
    public static final String NODE_WORKER2_ID = "worker2";

    public static final String PROCESS_ID_NOT_EXISTS = "processIdNotExists";
    public static final String PLUGIN1_PROCESS_ID = "testPlugin1ProcessId";
    public static final String PLUGIN1_ID = "testPlugin1";
    public static final String PLUGIN1_MAIN_CLASS = "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1";
    public static final String PLUGIN1_PROFILE_BIG = "testPlugin1-big";
    public static final String PLUGIN1_PROFILE_SMALL = "testPlugin1-small";

    public static final String PLUGIN2_PROCESS_ID = "testPlugin2ProcessId";
    public static final String PLUGIN2_ID = "testPlugin2";
    public static final String PLUGIN2_MAIN_CLASS = "org.ceskaexpedice.processplatform.testplugin2.TestPlugin2";

    public static final String PROCESS_PID = "1234";

    private WorkerTestsUtils() {}

}
