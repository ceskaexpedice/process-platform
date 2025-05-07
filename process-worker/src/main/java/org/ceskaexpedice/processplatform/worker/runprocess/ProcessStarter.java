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
package org.ceskaexpedice.processplatform.worker.runprocess;

import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ProcessStarter {

    public static void main(String[] args) throws Exception {
        String processType = args[0];
        String payload = args[1];

        File jar = new File("plugins/" + processType + ".jar");
        URLClassLoader cl = new URLClassLoader(new URL[]{ jar.toURI().toURL() });

        Class<?> clazz = cl.loadClass("org.ceskaexpedice.processplatform.processes." + processType + "." + capitalize(processType) + "Main");
        Method main = clazz.getMethod("main", String[].class);
        main.invoke(null, (Object) new String[]{ payload });
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
