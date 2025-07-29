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
package org.ceskaexpedice.processplatform.worker.plugin.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.TechnicalException;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * PluginProfilesLoader
 * @author ppodsednik
 */
final class PluginProfilesLoader {
    private static final String PROFILES_FILE_Name = "profile.json";

    private PluginProfilesLoader() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    static List<PluginProfile> loadProfiles(File pluginJar) {
        List<PluginProfile> internalProfiles = loadInternalProfiles(pluginJar);
        return internalProfiles;
    }

    private static List<PluginProfile> loadInternalProfiles(File jarFile) {
        try {
            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry entry = jar.getJarEntry(PROFILES_FILE_Name);
                if (entry == null) return new ArrayList<>();
                try (InputStream in = jar.getInputStream(entry)) {
                    List<PluginProfile> profiles = Arrays.asList(mapper.readValue(in, PluginProfile[].class));
                    return profiles;
                }
            }
        } catch (IOException e) {
            throw new TechnicalException(e.toString(), e);
        }
    }

}
