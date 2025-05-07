/*
 * Copyright (C) 2010 Pavel Stastny
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
package org.ceskaexpedice.processplatform.manager.todelete;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.kramerius.database.VersionDbInitializer;
import cz.incad.kramerius.database.VersionService;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.processes.database.MostDesirableDbInitializer;
import cz.incad.kramerius.processes.database.ProcessDbInitializer;
import cz.incad.kramerius.rest.oai.db.OAIDBInitializer;
import cz.incad.kramerius.security.database.SecurityDbInitializer;
import cz.incad.kramerius.service.LifeCycleHookRegistry;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.statistics.database.StatisticDbInitializer;
import cz.incad.kramerius.users.database.LoggedUserDbHelper;
import cz.incad.kramerius.utils.DatabaseUtils;
import cz.incad.kramerius.workmode.WorkModeDbInitializer;
import cz.inovatika.cdk.cache.CDKCacheInitializer;
import cz.inovatika.folders.db.FolderDatabaseInitializer;
import org.ceskaexpedice.processplatform.manager.tasks.TasksPlanning;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class StartupServlet extends HttpServlet {

    @Inject
    TasksPlanning pollingLoop;

    private Thread pollingThread;

    @Override
    public void init() {
        pollingThread = new Thread(pollingLoop, "db-task-poller");
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    @Override
    public void destroy() {
        pollingLoop.stop();
        pollingThread.interrupt();
    }
}
