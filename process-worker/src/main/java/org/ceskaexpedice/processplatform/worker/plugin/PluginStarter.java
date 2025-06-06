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

import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.Process;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.api.context.PluginContextHolder;
import org.ceskaexpedice.processplatform.worker.plugin.utils.PluginsLoader;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * PluginStarter
 * @author ppodsednik
 */
public class PluginStarter implements PluginContext {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ProcessStarter <processName> <payload>");
            System.exit(1);
        }

        String pluginPath = args[0];  // e.g., "import"
        String pluginName = args[1];  // e.g., "import"
        String mainClassName = args[2];  // e.g., "import"

        Map<String, String> payload = new HashMap<>();
        payload.put("name", args[3]);
        payload.put("surname", args[4]);

        try {
            PluginContextHolder.setContext(new PluginStarter());
            ClassLoader loader = PluginsLoader.createPluginClassLoader(new File(pluginPath), pluginName);
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> clz = loader.loadClass(mainClassName);
                MethodType processMethod = annotatedMethodType(clz);
                if (processMethod == null) {
                    processMethod = mainMethodType(clz);
                }

               // processMethod = mainMethodType(clz);


                if (processMethod.getType() == MethodType.Type.ANNOTATED) {
                    try {
                        Object[] params = map(processMethod.getMethod(), args, payload);
                        processMethod.getMethod().invoke(null, params);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Object[] objs = new Object[1];
                        objs[0] = args;
                        processMethod.getMethod().invoke(null, objs);
                        //processMethod.getMethod().invoke(null, (Object) args);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }
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
        try {
            Method mainMethod = clz.getMethod("main", (new String[0]).getClass());
            return mainMethod;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static MethodType annotatedMethodType(Class clz) {
        Method annotatedMethod = annotatedMethod(clz);
        return annotatedMethod != null ? new MethodType(annotatedMethod, MethodType.Type.ANNOTATED) : null;
    }

    public static Method annotatedMethod(Class clz) {
        Method annotatedMethod = null;
        for (Method m : clz.getMethods()) {
            if (m.isAnnotationPresent(Process.class)) {
                if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    annotatedMethod = m;
                    break;
                }
            }
        }
        return annotatedMethod;
    }

    private static Object[] map(Method processMethod, String[] defaultParams, Map<String, String> processParametersProperties) {
        Annotation[][] annots = processMethod.getParameterAnnotations();
        Class<?>[] types = processMethod.getParameterTypes();
        //if (defaultParams.length < types.length) throw new IllegalArgumentException("defaultParams.length is small array. It must have at least "+types.length+" items");
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < types.length; i++) {
            Annotation[] ann = annots[i];
            Annotation nameAnnot = findNameAnnot(ann);
            String val = null;
            if (nameAnnot != null) {
                String parameterName = ((ParameterName) nameAnnot).value();
                val = (String) processParametersProperties.get(parameterName);
                val = val != null ? val : defaultParam(defaultParams, i);
            } else {
                val = defaultParam(defaultParams, i);
            }

            if (!(types[i].equals(String.class))) {
                try {
                    params.add(instantiate(val, types[i]));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                params.add(val);
            }
        }
        return params.toArray();
    }

    public static String defaultParam(String[] defaultParams, int i) {
        return defaultParams.length > i ? defaultParams[i] : null;
    }

    public static Annotation findNameAnnot(Annotation[] ann) {
        Annotation nameAnnot = null;
        for (Annotation paramAn : ann) {
            if (paramAn instanceof ParameterName) {
                nameAnnot = paramAn;
                break;
            }
        }
        return nameAnnot;
    }

    private static Object instantiate(String val, Class<?> class1) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Constructor<?> constructor = class1.getConstructor(new Class[]{String.class});
        return constructor.newInstance(val);
    }

    /*
    private static MethodType mainMethodType(Class<?> clz, ClassLoader loader) {
        Method mainMethod = mainMethod(clz, loader);
        return mainMethod != null ? new MethodType(mainMethod, MethodType.Type.MAIN) : null;
    }

    private static Method mainMethod(Class<?> clz, ClassLoader loader) {
        try {
            // Use the same loader to resolve parameter type (String[]) explicitly
            Class<?> stringArrayClass = loader.loadClass("[java.lang.String;");
            Method mainMethod = clz.getMethod("main", stringArrayClass);
            return mainMethod;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }*/

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
