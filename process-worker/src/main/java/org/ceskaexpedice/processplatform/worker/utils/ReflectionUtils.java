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
package org.ceskaexpedice.processplatform.worker.utils;

import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;
import org.ceskaexpedice.processplatform.common.ApplicationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReflectionUtils
 * @author ppodsednik
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static MethodType mainMethodType(Class clz) throws NoSuchMethodException {
        Method mainMethod = mainMethod(clz);
        return mainMethod != null ? new MethodType(mainMethod, MethodType.Type.MAIN) : null;
    }

    public static MethodType annotatedMethodType(Class clz) {
        Method annotatedMethod = annotatedMethod(clz);
        return annotatedMethod != null ? new MethodType(annotatedMethod, MethodType.Type.ANNOTATED) : null;
    }

    public static Object[] map(Method processMethod, String[] defaultParams, Map<String, String> processParametersProperties) {
        Annotation[][] annots = processMethod.getParameterAnnotations();
        Class<?>[] types = processMethod.getParameterTypes();
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < types.length; i++) {
            Annotation[] ann = annots[i];
            Annotation nameAnnot = findNameAnnot(ann);
            String val = null;
            if (nameAnnot != null) {
                String parameterName = ((ParameterName) nameAnnot).value();
                val = processParametersProperties.get(parameterName);
                val = val != null ? val : defaultParam(defaultParams, i);
            } else {
                val = defaultParam(defaultParams, i);
            }

            if (!(types[i].equals(String.class))) {
                try {
                    params.add(instantiate(val, types[i]));
                } catch (Exception e) {
                    throw new ApplicationException(e.toString(), e);
                }
            } else {
                params.add(val);
            }
        }
        return params.toArray();
    }


    /**
     * Wrapper which represents found method
     *
     * @author pavels
     */
    public static class MethodType {

        public static enum Type {MAIN, ANNOTATED}

        private Method method;
        private Type type;

        public MethodType(Method method, Type type) {
            super();
            this.method = method;
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public Method getMethod() {
            return method;
        }
    }

    private static Method mainMethod(Class clz) throws NoSuchMethodException {
        Method mainMethod = clz.getMethod("main", (new String[0]).getClass());
        return mainMethod;
    }

    private static Method annotatedMethod(Class clz) {
        Method annotatedMethod = null;
        for (Method m : clz.getMethods()) {
            if (m.isAnnotationPresent(ProcessMethod.class)) {
                if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    annotatedMethod = m;
                    break;
                }
            }
        }
        return annotatedMethod;
    }

    private static String defaultParam(String[] defaultParams, int i) {
        return defaultParams.length > i ? defaultParams[i] : null;
    }

    private static Annotation findNameAnnot(Annotation[] ann) {
        Annotation nameAnnot = null;
        for (Annotation paramAn : ann) {
            if (paramAn instanceof ParameterName) {
                nameAnnot = paramAn;
                break;
            }
        }
        return nameAnnot;
    }

    private static Object instantiate(String paramValue, Class<?> paramType) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Object value;
        if (paramType.equals(java.util.Date.class)) {
            value = java.sql.Timestamp.valueOf(paramValue);
        } else if (paramType.equals(java.time.LocalDateTime.class)) {
            value = java.time.LocalDateTime.parse(
                    paramValue,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
        } else if (paramType.equals(java.time.LocalDate.class)) {
            value = java.time.LocalDate.parse(
                    paramValue,
                    DateTimeFormatter.ISO_LOCAL_DATE
            );
        } else {
            // Fallback for other parameter types
            Constructor<?> ctor = paramType.getConstructor(String.class);
            value = ctor.newInstance(paramValue);
        }
        return value;
    }

}
