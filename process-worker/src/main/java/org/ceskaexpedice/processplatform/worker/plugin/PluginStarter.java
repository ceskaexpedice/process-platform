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
package org.ceskaexpedice.processplatform.worker.plugin;

import org.ceskaexpedice.processplatform.api.PluginContext;
import org.ceskaexpedice.processplatform.api.PluginContextHolder;

import java.lang.reflect.Method;

public class PluginStarter implements PluginContext {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ProcessStarter <processName> <payload>");
            System.exit(1);
        }

        String processName = args[0];  // e.g., "import"
        String mainClassName = args[1];  // e.g., "import"
        String payload = args[2];      // JSON or command string

        try {
            PluginContextHolder.setContext(new PluginStarter());
            ClassLoader loader = PluginLoader.loadProcessClassLoader(processName);
            //String mainClassName = "org.ceskaexpedice.processplatform.processes." + processName + "." + capitalize(processName) + "Main";
            Class<?> clz = loader.loadClass(mainClassName);
            MethodType processMethod = mainMethodType(clz);
            Object[] objs = new Object[1];
            objs[0] = args;
            processMethod.getMethod().invoke(null, objs);
            //String pid = getPID();
            //updatePID(pid);
        } catch (Exception e) {
            System.err.println("Failed to start process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static MethodType mainMethodType(Class clz) throws SecurityException, NoSuchMethodException {
        Method mainMethod = mainMethod(clz);
        return mainMethod != null ? new MethodType(mainMethod, MethodType.Type.MAIN) : null;
    }

    private static Method mainMethod(Class clz) throws NoSuchMethodException {
        Method mainMethod = clz.getMethod("main", (new String[0]).getClass());
        return mainMethod;
    }

    @Override
    public void updateTaskState(String taskId, String taskState) {
        // TODO must be able to call worker's PluginEndpoint
    }

    @Override
    public void updateTaskPid(String taskId, String pid) {

    }

    @Override
    public void updateTaskName(String taskId, String name) {

    }

    @Override
    public void scheduleProcess(String processDefinition) {

    }

    /**
     * Wrapper which represents found method
     *
     * @author pavels
     */
    static class MethodType {

        /**
         * enum for type of method
         */
        static enum Type {MAIN, ANNOTATED}

        ;

        private Method method;
        private Type type;

        public MethodType(Method method, Type type) {
            super();
            this.method = method;
            this.type = type;
        }


        /**
         * Returns type of method
         *
         * @return
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns method
         *
         * @return
         */
        public Method getMethod() {
            return method;
        }
    }

}
