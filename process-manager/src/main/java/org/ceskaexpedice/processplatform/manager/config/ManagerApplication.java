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
package org.ceskaexpedice.processplatform.manager.config;

import javax.servlet.ServletContext;
import org.ceskaexpedice.processplatform.manager.api.ForWorkerEndpoint;
import org.ceskaexpedice.processplatform.manager.api.PluginEndpoint;
import org.ceskaexpedice.processplatform.manager.api.ProcessEndpoint;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

/**
 * ManagerApplication
 * @author ppodsednik
 */
@ApplicationPath("/api")
public class ManagerApplication extends ResourceConfig {

    public ManagerApplication(@Context ServletContext context) {
        PluginService pluginService = (PluginService) context.getAttribute("pluginService");
        ProcessService processService = (ProcessService) context.getAttribute("processService");
        NodeService nodeService = (NodeService) context.getAttribute("nodeService");

        register(new PluginEndpoint(pluginService));
        register(new ProcessEndpoint(processService));
        register(new ForWorkerEndpoint(pluginService, processService, nodeService));
        // TODO register(GlobalExceptionMapper.class);
    }

}
