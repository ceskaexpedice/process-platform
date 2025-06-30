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

import org.ceskaexpedice.processplatform.worker.api.WorkerAgentEndpoint;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * WorkerApplication
 * @author ppodsednik
 */
@ApplicationPath("/api")
public class WorkerApplication extends ResourceConfig {

    public WorkerApplication() {
        register(WorkerAgentEndpoint.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(WorkerAgentEndpointFactory.class).to(WorkerAgentEndpoint.class);
            }
        });
    }

}
