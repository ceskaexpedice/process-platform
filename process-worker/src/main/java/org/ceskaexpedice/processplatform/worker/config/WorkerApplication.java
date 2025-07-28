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
package org.ceskaexpedice.processplatform.worker.config;

import org.ceskaexpedice.processplatform.common.GlobalExceptionMapper;
import org.ceskaexpedice.processplatform.worker.api.ForManagerEndpoint;
import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;

/**
 * WorkerApplication
 * @author ppodsednik
 */
public class WorkerApplication extends ResourceConfig {

    public WorkerApplication() {
        ServletContext ctx = WorkerStartupListener.getServletContext();

        ForManagerService forManagerService = (ForManagerService) ctx.getAttribute(ForManagerService.class.getSimpleName());
        register(new ForManagerEndpoint(forManagerService));
        register(GlobalExceptionMapper.class);
    }

}
