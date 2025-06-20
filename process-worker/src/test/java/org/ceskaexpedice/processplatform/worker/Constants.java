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
package org.ceskaexpedice.processplatform.worker;

public interface Constants {
    String MANAGER_BASE_URI = "http://localhost:9998/process-manager/api/";

    String PLUGIN1_PROCESS_ID = "testPlugin1ProcessId";
    String PLUGIN1_ID = "testPlugin1";
    String PLUGIN1_MAIN_CLASS = "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1";
    String PLUGIN1_PROFILE_BIG = "testPlugin1-big";
    String PLUGIN1_PROFILE_SMALL = "testPlugin1-small";

    String PLUGIN2_PROCESS_ID = "testPlugin2ProcessId";
    String PLUGIN2_ID = "testPlugin2";
    String PLUGIN2_MAIN_CLASS = "org.ceskaexpedice.processplatform.testplugin2.TestPlugin2";

}
