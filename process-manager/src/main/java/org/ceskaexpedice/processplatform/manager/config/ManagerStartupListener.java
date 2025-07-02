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

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ManagerStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("manager.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load manager.properties", e);
        }
        ManagerConfiguration config = new ManagerConfiguration(props);
        DbConnectionProvider dbProvider = new DbConnectionProvider(config);
        PluginService pluginService = new PluginService(config, dbProvider);
        ProcessService processService = new ProcessService(config, dbProvider);

        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("pluginService", pluginService);
        ctx.setAttribute("processService", processService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO dbProvider.close();
    }
}
