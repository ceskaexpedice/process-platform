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

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.ceskaexpedice.processplatform.manager.api.AgentEndpoint;
import org.ceskaexpedice.processplatform.manager.api.service.AgentService;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

/**
 * ManagerEndpointFactory
 * @author ppodsednik
 */
public class ProfileEndpointFactory implements Factory<AgentEndpoint> {

    @Inject
    private HttpServletRequest request;

    @Override
    public AgentEndpoint provide() {
        ServletContext ctx = request.getServletContext();
       // WorkerMain workerMain = (WorkerMain) ctx.getAttribute("workerMain");
        return new AgentEndpoint(new AgentService());
    }

    @Override
    public void dispose(AgentEndpoint t) {
    }
}
