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

import org.ceskaexpedice.processplatform.manager.tasks.TasksPlanning;
import org.ceskaexpedice.processplatform.manager.tasks.TaskQueue;
import org.ceskaexpedice.processplatform.manager.tasks.TaskRepository;

public class ManagerModule extends AbstractModule {
    @Override
    protected void configure() {
        //bind(TaskRepository.class).to(JdbcTaskRepository.class).in(Singleton.class); // example

        bind(TaskQueue.class).asEagerSingleton();
        bind(TaskRepository.class).asEagerSingleton();
        bind(TasksPlanning.class).asEagerSingleton();
    }
}
