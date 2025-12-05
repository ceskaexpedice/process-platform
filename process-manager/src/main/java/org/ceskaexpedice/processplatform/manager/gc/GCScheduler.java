package org.ceskaexpedice.processplatform.manager.gc;

import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;

import java.util.Timer;
import java.util.logging.Logger;

/**
 * GCScheduler
 * @author ppodsednik
 */
public class GCScheduler {

    private static Logger LOGGER = Logger.getLogger(GCScheduler.class.getName());

    private int interval;
    private Timer timer;
    private ProcessService processService;
    private ManagerConfiguration managerConfiguration;

    public GCScheduler(ProcessService processService, ManagerConfiguration managerConfiguration) {
        this.processService = processService;
        this.managerConfiguration = managerConfiguration;
        this.timer = new Timer(GCScheduler.class.getName() + "-thread", false);
    }

    public void init() {
        this.interval = managerConfiguration.getGcSchedulerCheckInterval();
        this.scheduleDoGc();
    }

    public void scheduleDoGc() {
        GCTask findCandidates = new GCTask(this.processService, this);
        this.timer.schedule(findCandidates, this.interval);
    }

    public void shutdown() {
        LOGGER.info("Cancelling gcScheduler");
        this.timer.cancel();
    }
}
