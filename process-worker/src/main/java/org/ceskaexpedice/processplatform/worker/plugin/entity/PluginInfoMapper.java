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
package org.ceskaexpedice.processplatform.worker.plugin.entity;


import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;

/**
 * PluginInfoMapper
 * @author ppodsednik
 */
public class PluginInfoMapper {

    public static PluginInfoTO toTO(PluginInfo pluginInfo) {
        /*
        List<PluginProfileTO> profiles = pluginInfo.getProfiles().stream()
                .map(profile -> new PluginProfileTO(
                        profile.getProfileId(),
                        pluginInfo.getPluginId(),
                        profile.getStaticParams(),
                        profile.getJvmArgs()
                ))
                .collect(Collectors.toList());

        return new PluginInfoTO(
                pluginInfo.getPluginId(),
                pluginInfo.getDescription(),
                pluginInfo.getMainClass(),
                profiles
        );

         */ return null;
    }

    // Optionally: implement fromDto() if needed
}
