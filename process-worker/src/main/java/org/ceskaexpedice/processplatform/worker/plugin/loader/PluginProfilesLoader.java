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
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * PluginProfilesLoader
 * @author ppodsednik
 */
final class PluginProfilesLoader {

    private PluginProfilesLoader() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    static List<PluginProfile> loadProfiles(File pluginJar, File externalProfilesDir, String pluginId) {
        List<PluginProfile> internalProfiles = loadInternalProfiles(pluginJar, pluginId);
        List<PluginProfile> externalProfiles = loadExternalProfiles(externalProfilesDir, pluginId);

        // Merge: external overrides internal by 'profileId'
        Map<String, PluginProfile> merged = new LinkedHashMap<>();
        for (PluginProfile p : internalProfiles) {
            p.setPluginId(pluginId);
            merged.put(p.getProfileId(), p);
        }
        for (PluginProfile p : externalProfiles) {
            p.setPluginId(pluginId);
            merged.put(p.getProfileId(), p);
        }

        return new ArrayList<>(merged.values());
    }

    private static List<PluginProfile> loadInternalProfiles(File jarFile, String pluginId) {
        try {
            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry entry = jar.getJarEntry(pluginId + ".json");
                if (entry == null) return Collections.emptyList();
                try (InputStream in = jar.getInputStream(entry)) {
                    List<PluginProfile> profiles = Arrays.asList(mapper.readValue(in, PluginProfile[].class));
                    return profiles;
                }
            }
        } catch (IOException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

    private static List<PluginProfile> loadExternalProfiles(File pluginDir, String pluginId) {
        try {
            File json = new File(pluginDir, pluginId + ".json");
            if (!json.exists()) return Collections.emptyList();
            try (InputStream in = new FileInputStream(json)) {
                List<PluginProfile> profiles = Arrays.asList(mapper.readValue(in, PluginProfile[].class));
                return profiles;
            }
        } catch (IOException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }
}
