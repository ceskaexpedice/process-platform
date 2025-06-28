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
package org.ceskaexpedice.processplatform.api;

import org.ceskaexpedice.processplatform.common.entity.PayloadFieldSpec;

import java.util.Map;
import java.util.Set;

/**
 * ProcessPlugin
 * @author ppodsednik
 */
public interface PluginSpi {

    String getPluginId();        // e.g., "import"

    String getDescription();     // e.g., "Imports FOXML into system"

    String getMainClass();       // e.g., "cz.kramerius.plugin.importer.Main"

    Map<String, PayloadFieldSpec> getPayloadSpec();

    Set<String> getScheduledProfiles();
}