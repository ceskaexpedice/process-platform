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

import org.ceskaexpedice.processplatform.common.GlobalExceptionMapper;
import org.ceskaexpedice.processplatform.manager.api.*;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletContext;

/**
 * ManagerApplication
 * @author ppodsednik
 */
public class ManagerApplication extends ResourceConfig {

    public ManagerApplication() {
        ServletContext ctx = ManagerStartupListener.getServletContext();

        NodeService nodeService = (NodeService) ctx.getAttribute(NodeService.class.getSimpleName());
        ProcessService processService = (ProcessService) ctx.getAttribute(ProcessService.class.getSimpleName());
        PluginService pluginService = (PluginService) ctx.getAttribute(PluginService.class.getSimpleName());
        ProfileService profileService = (ProfileService) ctx.getAttribute(ProfileService.class.getSimpleName());

        register(new NodeEndpoint(nodeService));
        register(new ProcessEndpoint(processService, nodeService));
        register(new PluginEndpoint(pluginService));
        register(new ProfileEndpoint(profileService));
        register(new ForWorkerEndpoint(pluginService, processService, nodeService));
        register(GlobalExceptionMapper.class);
    }

}
