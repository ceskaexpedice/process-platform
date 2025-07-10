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
package org.ceskaexpedice.processplatform.manager.api.service;

import org.ceskaexpedice.processplatform.common.entity.ScheduleMainProcess;
import org.ceskaexpedice.processplatform.common.entity.ScheduleSubProcess;
import org.ceskaexpedice.processplatform.common.entity.ScheduledProcess;
import org.ceskaexpedice.processplatform.manager.api.dao.ProcessDao;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * ProcessService
 * @author ppodsednik
 */
public class ProcessService {
    private final ManagerConfiguration managerConfiguration;
    private final ProcessDao processDao;

    public ProcessService(ManagerConfiguration managerConfiguration, DbConnectionProvider dbConnectionProvider) {
        this.managerConfiguration = managerConfiguration;
        this.processDao = new ProcessDao(dbConnectionProvider, managerConfiguration);
    }

    public void scheduleProcess(ScheduleMainProcess scheduleMainProcess) {
        processDao.createProcess(scheduleMainProcess);
    }

    public void scheduleProcess(ScheduleSubProcess scheduleSubProcess) {

    }

    public ScheduledProcess getNextScheduledProcess(List<String> tags) {
        List<ScheduledProcess> scheduledProcesses = processDao.getScheduledProcesses();
        ScheduledProcess selectedProcess = null;
        for (ScheduledProcess scheduledProcess : scheduledProcesses) {
            if(tags.contains(scheduledProcess.getProfileId())){
                selectedProcess = scheduledProcess;
                break;
            }
        }
        // TODO populate pluginId, mainClass, jvmArgs from plugin
        // set process state to NOT_RUNNING

        return selectedProcess;
    }

}
