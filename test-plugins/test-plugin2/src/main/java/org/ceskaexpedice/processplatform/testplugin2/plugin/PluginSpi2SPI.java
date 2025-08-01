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
package org.ceskaexpedice.processplatform.testplugin2.plugin;

import org.ceskaexpedice.processplatform.api.PluginSpi;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PluginSpi2SPI implements PluginSpi {

    @Override
    public String getPluginId() {
        return "testPlugin2";
    }

    @Override
    public String getDescription() {
        return "Testing plugin 2";
    }

    @Override
    public String getMainClass() {
        return "org.ceskaexpedice.processplatform.testplugin2.TestPlugin2";
    }

    @Override
    public Map<String, PayloadFieldSpec> getPayloadSpec() {
        Map<String, PayloadFieldSpec>  map = new HashMap<>();
        return map;
    }

    @Override
    public Set<String> getScheduledProfiles() {
        return Set.of();
    }

}
