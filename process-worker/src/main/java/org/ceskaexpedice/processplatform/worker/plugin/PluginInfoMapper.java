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


import org.ceskaexpedice.processplatform.common.dto.PluginInfoDto;
import org.ceskaexpedice.processplatform.common.dto.PluginProfileDto;

import java.util.List;
import java.util.stream.Collectors;

public class PluginInfoMapper {

    public static PluginInfoDto toDto(PluginInfo pluginInfo) {
        List<PluginProfileDto> profiles = pluginInfo.getProfiles().stream()
                .map(profile -> new PluginProfileDto(
                        profile.getProfileId(),
                        pluginInfo.getPluginId(),
                        profile.getStaticParams(),
                        profile.getJvmArgs()
                ))
                .collect(Collectors.toList());

        return new PluginInfoDto(
                pluginInfo.getPluginId(),
                pluginInfo.getDescription(),
                pluginInfo.getMainClass(),
                profiles
        );
    }

    // Optionally: implement fromDto() if needed
}
