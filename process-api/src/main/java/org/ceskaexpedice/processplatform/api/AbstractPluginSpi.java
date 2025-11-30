package org.ceskaexpedice.processplatform.api;

import org.ceskaexpedice.processplatform.api.annotations.IsRequired;
import org.ceskaexpedice.processplatform.api.annotations.ParameterName;
import org.ceskaexpedice.processplatform.api.annotations.ProcessMethod;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPluginSpi implements PluginSpi {

    public static Logger LOGGER = Logger.getLogger(AbstractPluginSpi.class.getName());

    @Override
    public String getPluginId() {
        String pluginId = getManifestProperty("Module-Artifact-Id");
        LOGGER.log(Level.INFO, "getPluginId: pluginid={0}", pluginId);
        return pluginId;
    }

    @Override
    public Set<String> getScheduledProfiles() {
        String defaultProfile = getManifestProperty("Module-Artifact-Id");
        LOGGER.log(Level.INFO, "getScheduledProfiles: scheduledprofiles={0}", Set.of(defaultProfile));
        return Set.of(defaultProfile);
    }



    private String getManifestProperty(String name) {
        try {
            Class<?> aMainClass = loadMainPluginClass();
            Manifest manifest = getManifestFromOwningJar(aMainClass);
            if (manifest != null) {
                String pluginId = manifest.getMainAttributes().getValue(name);
                if (pluginId != null) {
                    return pluginId;
                } else {
                    LOGGER.log(Level.WARNING, String.format("Plugin with id %s not found", name));
                    return "";
                }
            }
            return "";
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        String description = getManifestProperty("Module-description");
        LOGGER.log(Level.INFO, "getDescription: description={0}", description);
        return description;
    }

    public static Manifest getManifestFromOwningJar(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        LOGGER.log(Level.INFO, "getManifestFromOwningJar: location={0}", location);
        if (location == null || !location.getPath().endsWith(".jar")) {
            LOGGER.log(Level.SEVERE, String.format("Cannot find Jar file for %s", clazz.getName()));
            return null;
        }

        JarFile jarFile = null;
        try {
            String jarPath = location.getPath();
            if (jarPath.startsWith("file:")) {
                jarPath = jarPath.substring(5);
            }
            jarFile = new JarFile(new java.io.File(jarPath));

            return jarFile.getManifest();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Cannot find Manifest for %s", clazz.getName()), e);
            return null;
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (java.io.IOException ignored) {
                }
            }
        }
    }

    public Class<?> loadMainPluginClass() throws ClassNotFoundException {
        String clzName = getMainClass();
        ClassLoader pluginClassLoader = Thread.currentThread().getContextClassLoader();
        if (pluginClassLoader == null) {
            pluginClassLoader = this.getClass().getClassLoader();
        }
        return pluginClassLoader.loadClass(clzName);
    }

    @Override
    public Map<String, PayloadFieldSpec> getPayloadSpec() {
        Class<?> mainClass = null;
        try {
            mainClass = loadMainPluginClass();
            Method processMethod = findProcessMethod(mainClass);

            if (processMethod == null) {
                LOGGER.log(Level.SEVERE, String.format("Cannot find process method for %s", mainClass.getName()));
                return Collections.emptyMap();
            }

            return extractParameterSpecs(processMethod);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Method findProcessMethod(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ProcessMethod.class)) {
                return method;
            }
        }
        return null;
    }

    private Map<String, PayloadFieldSpec> extractParameterSpecs(Method method) {
        Map<String, PayloadFieldSpec> specMap = new HashMap<>();

        for (Parameter parameter : method.getParameters()) {
            ParameterName nameAnnotation = parameter.getAnnotation(ParameterName.class);

            if (nameAnnotation == null) {
                if (isContextParameter(parameter.getType())) {
                    continue;
                }
                LOGGER.log(Level.WARNING, "Method " + method.getName() +
                        " contains an undescribed parameter: " + parameter.getName() + ". It will be ignored.");
                continue;
            }

            boolean isRequired = parameter.isAnnotationPresent(IsRequired.class);

            String paramName = nameAnnotation.value();
            String paramType = parameter.getType().getName();
            PayloadFieldType type = PayloadFieldType.STRING;
            type = switch (paramType) {
                case "java.lang.Integer", "int", "java.lang.Long", "long", "java.lang.Double", "double",
                     "java.lang.Float", "float" -> PayloadFieldType.NUMBER;
                case "java.lang.Boolean", "boolean" -> PayloadFieldType.BOOLEAN;
                case "java.util.Date", "java.sql.Date" -> PayloadFieldType.DATE;
                default -> type;
            };
            PayloadFieldSpec spec = new PayloadFieldSpec(type, isRequired);
            specMap.put(paramName, spec);
        }

        return specMap;
    }

    private boolean isContextParameter(Class<?> type) {
        return type.getName().contains("PluginContext");
    }

}
