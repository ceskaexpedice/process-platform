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

import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.DbUtils;
import org.ceskaexpedice.processplatform.manager.db.JDBCUpdateTemplate;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerStartupListener implements ServletContextListener {

    public static Logger LOGGER = Logger.getLogger(ManagerStartupListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("manager.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ApplicationException("Cannot load manager.properties", e);
        }
        ManagerConfiguration config = new ManagerConfiguration(props);
        DbConnectionProvider dbProvider = new DbConnectionProvider(config);
        PluginService pluginService = new PluginService(config, dbProvider);
        ProcessService processService = new ProcessService(config, dbProvider, pluginService);

        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("pluginService", pluginService);
        ctx.setAttribute("processService", processService);

        makeSureTableExists(dbProvider, "pcp_profile");
        makeSureTableExists(dbProvider, "pcp_process");
        makeSureTableExists(dbProvider, "pcp_plugin");
    }

    private void makeSureTableExists(DbConnectionProvider dbProvider, String tableName) {
        try {
            Connection connection = dbProvider.get();
            boolean pcpProfile = DbUtils.tableExists(connection, tableName);
            if (!pcpProfile) {
                JDBCUpdateTemplate updateTemplate = new JDBCUpdateTemplate(connection, true);
                InputStream is = getClass().getClassLoader().getResourceAsStream(String.format("%s.sql", tableName));
                String sql = IOUtils.toString(is, "UTF-8");
                updateTemplate.executeUpdate(sql);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO dbProvider.close();
    }
}
