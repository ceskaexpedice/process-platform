package org.ceskaexpedice.processplatform.manager.gc;

import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * GCScheduler
 *
 * @author ppodsednik
 */
public class GCScheduler {

    private static Logger LOGGER = Logger.getLogger(GCScheduler.class.getName());

    private int interval;
    private Timer timer;
    private ProcessService processService;
    private NodeService nodeService;
    private ManagerConfiguration managerConfiguration;
    private final ConcurrentMap<String, Long> workerUnreachableSince = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> notRunningFailures = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> notRunningWorkerUnreachableSince =            new ConcurrentHashMap<>();

    public GCScheduler(ProcessService processService, NodeService nodeService, ManagerConfiguration managerConfiguration) {
        this.processService = processService;
        this.managerConfiguration = managerConfiguration;
        this.nodeService = nodeService;
        this.timer = new Timer(GCScheduler.class.getName() + "-thread", false);
    }

    public void init() {
        this.interval = managerConfiguration.getGcSchedulerCheckInterval();
        this.scheduleDoGc();
    }

    public void scheduleDoGc() {
        GCTask gcTask = new GCTask(this.processService, this.nodeService, this);
        this.timer.schedule(gcTask, this.interval);
    }

    public void shutdown() {
        LOGGER.info("Cancelling gcScheduler");
        this.timer.cancel();
    }

    public ConcurrentMap<String, Long> getWorkerUnreachableSince() {
        return workerUnreachableSince;
    }

    public ConcurrentMap<String, Integer> getNotRunningFailures() {
        return notRunningFailures;
    }

    public ConcurrentMap<String, Long> getNotRunningWorkerUnreachableSince() {
        return notRunningWorkerUnreachableSince;
    }
}
