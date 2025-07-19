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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.ceskaexpedice.processplatform.worker.WorkerMain;
import org.ceskaexpedice.processplatform.worker.api.ForManagerEndpoint;
import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

/**
 * ManagerEndpointFactory
 * @author ppodsednik
 */
public class WorkerAgentEndpointFactory implements Factory<ForManagerEndpoint> {

    @Inject
    private HttpServletRequest request;

    @Override
    public ForManagerEndpoint provide() {
        ServletContext ctx = request.getServletContext();
        WorkerMain workerMain = (WorkerMain) ctx.getAttribute("workerMain");
        return new ForManagerEndpoint(new ForManagerService(workerMain));
    }

    @Override
    public void dispose(ForManagerEndpoint t) {
    }
}
