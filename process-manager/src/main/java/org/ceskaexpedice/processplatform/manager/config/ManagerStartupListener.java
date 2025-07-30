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
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;
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
import java.util.logging.Logger;

import static org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration.*;

// TODO add openapi swagger
// TODO implement properly build plugins process via Gradle
// TODO check all Tomcat config - like web.xml - include it to the build
// TODO revisit manager.properties placement and pars name convention
public class ManagerStartupListener implements ServletContextListener {

    private static Logger LOGGER = Logger.getLogger(ManagerStartupListener.class.getName());
    private static ServletContext ctx;
    private DbConnectionProvider dbProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.ctx = sce.getServletContext();
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                LOGGER.info("Load config file [" + CONFIG_FILE + "]");
                props.load(in);
            }
        } catch (IOException e) {
            throw new ApplicationException("Cannot load properties file", e);
        }
        ManagerConfiguration config = new ManagerConfiguration(props);
        dbProvider = new DbConnectionProvider(config);

        initServices(config, dbProvider);
        initDb(dbProvider);
    }

    private void initDb(DbConnectionProvider dbProvider) {
        makeSureTableExists(dbProvider, NODE_TABLE);
        makeSureTableExists(dbProvider, PROCESS_TABLE);
        makeSureTableExists(dbProvider, PLUGIN_TABLE);
        makeSureTableExists(dbProvider, PROFILE_TABLE);
    }

    private static void initServices(ManagerConfiguration config, DbConnectionProvider dbProvider) {
        NodeService nodeService = new NodeService(config, dbProvider);
        PluginService pluginService = new PluginService(config, dbProvider);
        ProfileService profileService = new ProfileService(config, dbProvider);
        ProcessService processService = new ProcessService(config, dbProvider, pluginService, nodeService);

        ctx.setAttribute(NodeService.class.getSimpleName(), nodeService);
        ctx.setAttribute(PluginService.class.getSimpleName(), pluginService);
        ctx.setAttribute(ProfileService.class.getSimpleName(), profileService);
        ctx.setAttribute(ProcessService.class.getSimpleName(), processService);
    }

    private void makeSureTableExists(DbConnectionProvider dbProvider, String tableName) {
        try {
            Connection connection = dbProvider.get();
            boolean tableExists = DbUtils.tableExists(connection, tableName);
            if (!tableExists) {
                LOGGER.info("Creating table [" + tableName + "]");
                JDBCUpdateTemplate updateTemplate = new JDBCUpdateTemplate(connection, true);
                InputStream is = getClass().getClassLoader().getResourceAsStream(String.format("%s.sql", tableName));
                String sql = IOUtils.toString(is, "UTF-8");
                updateTemplate.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    static ServletContext getServletContext() {
        return ctx;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        dbProvider.close();
    }
}
