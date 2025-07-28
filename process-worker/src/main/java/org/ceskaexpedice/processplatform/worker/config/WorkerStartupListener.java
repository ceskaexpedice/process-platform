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
package org.ceskaexpedice.processplatform.worker.config;

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.worker.WorkerMain;
import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.CONFIG_FILE;

/**
 * WorkerStartupListener
 * @author ppodsednik
 */
// TODO check proper logging everywhere
// TODO all Tomcat and Jersey like wiring
// TODO add openapi swagger
// TODO implement properly build plugins process via Gradle
// TODO check all Tomcat config - like web.xml
public class WorkerStartupListener implements ServletContextListener {
    private static ServletContext ctx;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.ctx = sce.getServletContext();
        Properties fileProps = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                fileProps.load(in);
            }
        } catch (IOException e) {
            throw new ApplicationException("Error loading properties file", e);
        }
        WorkerConfiguration config = new WorkerConfiguration(fileProps);
        setStarterClasspath(config);
        ForManagerService forManagerService = new ForManagerService(config);
        ctx.setAttribute(ForManagerService.class.getSimpleName(), forManagerService);

        WorkerMain workerMain = new WorkerMain();
        workerMain.initialize(config);
        ctx.setAttribute(WorkerMain.class.getSimpleName(), workerMain);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        WorkerMain wm = (WorkerMain) sce.getServletContext().getAttribute(WorkerMain.class.getSimpleName());
        if (wm != null) {
            wm.shutdown();
        }
    }

    private static void setStarterClasspath(WorkerConfiguration config) {
        String libPath = ctx.getRealPath("/WEB-INF/lib");
        String starterClasspath = buildClasspath(libPath);
        config.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);
    }

    private static String buildClasspath(String libDir) {
        File dir = new File(libDir);
        StringBuilder classpath = new StringBuilder();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files != null) {
            for (File jar : files) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(jar.getAbsolutePath());
            }
        }
        return classpath.toString();
    }

    static ServletContext getServletContext() {
        return ctx;
    }

}
