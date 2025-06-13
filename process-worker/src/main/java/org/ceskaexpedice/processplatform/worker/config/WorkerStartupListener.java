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

import org.ceskaexpedice.processplatform.worker.WorkerMain;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * WorkerStartupListener
 * @author ppodsednik
 */
public class WorkerStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties fileProps = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("worker.properties")) {
            if (in != null) {
                fileProps.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }
        WorkerConfiguration config = new WorkerConfiguration(fileProps);
        setStarterClasspath(sce, config);
        WorkerMain workerMain = new WorkerMain();
        workerMain.initialize(config);
        sce.getServletContext().setAttribute("workerMain", workerMain);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        WorkerMain wm = (WorkerMain) sce.getServletContext().getAttribute("workerMain");
        if (wm != null) {
            wm.shutdown();
        }
    }

    private static void setStarterClasspath(ServletContextEvent sce, WorkerConfiguration config) {
        String libPath = sce.getServletContext().getRealPath("/WEB-INF/lib");
        String starterClasspath = buildClasspath(libPath);
        config.set("starter.classpath", starterClasspath);
        String envClasspath = System.getenv("STARTER_CLASSPATH");
        if (envClasspath != null) {
            config.set("starter.classpath", envClasspath);
        }
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

}
