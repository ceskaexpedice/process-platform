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
package org.ceskaexpedice.processplatform.manager;


import org.ceskaexpedice.processplatform.manager.config.ManagerModule;
import org.ceskaexpedice.processplatform.manager.tasks.TasksPlanning;

public class ManagerStartup {


}
/*
public class ManagerStartup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Injector injector = Guice.createInjector(new ManagerModule());
        TasksPlanning poller = injector.getInstance(TasksPlanning.class);
        poller.start(); // background DB polling
        sce.getServletContext().setAttribute("injector", injector);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }

}*/
