package org.ceskaexpedice.processplatform.manager.gc;

import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.WorkerInfo;
import org.ceskaexpedice.processplatform.common.model.WorkerState;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GCTask
 *
 * @author ppodsednik
 */


public class GCTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(GCTask.class.getName());
    private static final long WORKER_FAILURE_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(60);

    private final ProcessService processService;
    private final NodeService nodeService;
    private final GCScheduler gcScheduler;
    private final LongSupplier nanoTime;
    private final ConcurrentMap<String, Long> workerUnreachableSince;

    public GCTask(ProcessService processService, NodeService nodeService, GCScheduler gcScheduler) {
        this(processService, nodeService, gcScheduler, System::nanoTime);
    }

    GCTask(ProcessService processService, NodeService nodeService, GCScheduler gcScheduler, LongSupplier nanoTime) {
        this.processService = processService;
        this.gcScheduler = gcScheduler;
        this.nodeService = nodeService;
        this.workerUnreachableSince = gcScheduler.getWorkerUnreachableSince();
        this.nanoTime = nanoTime;
    }

    @Override
    public void run() {
        try {
            // GC processes
            List<ProcessInfo> processInfos = processService.getProcesses(ProcessState.RUNNING);
            // Remove failure records for processes that are no longer RUNNING.
            Set<String> runningProcessIds = new HashSet<>();
            for (ProcessInfo processInfo : processInfos) {
                runningProcessIds.add(processInfo.getProcessId());
            }
            workerUnreachableSince.keySet().removeIf(processId -> !runningProcessIds.contains(processId));
            for (ProcessInfo processInfo : processInfos) {
                String processId = processInfo.getProcessId();
                try {
                    if (processInfo.getPid() == null) {
                        LOGGER.warning("Found running process without pid: " + processId);
                        workerUnreachableSince.remove(processId);
                        processService.updateState(processId, ProcessState.FAILED);
                        continue;
                    }
                    // Fetch info from the worker server.
                    WorkerInfo workerInfo;
                    try {
                        workerInfo = processService.getWorkerInfo(processId);
                        // Communication succeeded - reset failure counter.
                        workerUnreachableSince.remove(processId);
                    } catch (Exception e) {
                        long now = nanoTime.getAsLong();
                        Long firstFailure = workerUnreachableSince.putIfAbsent(processId, now);
                        if (firstFailure == null) {
                            firstFailure = now;
                        }
                        long unavailableNanos = now - firstFailure;
                        if (unavailableNanos >= WORKER_FAILURE_TIMEOUT_NANOS) {
                            LOGGER.log(Level.SEVERE,
                                    "Worker unreachable for at least 60 seconds. "
                                            + "Marking process as FAILED: " + processId,
                                    e);
                            processService.updateState(processId, ProcessState.FAILED);
                            workerUnreachableSince.remove(processId);
                        } else {
                            LOGGER.log(Level.WARNING,
                                    "Cannot access worker for process: " + processId
                                            + ". Keeping process RUNNING.",
                                    e);
                        }
                        continue;
                    }
                    // Worker responded - verify that it runs the expected process.
                    if (workerInfo != null
                            && workerInfo.getState() == WorkerState.RUNNING
                            && workerInfo.isJvmAlive()
                            && workerInfo.getCurrentPid() != null
                            && workerInfo.getCurrentPid().equals(
                            processInfo.getPid())) {
                        continue;
                    }
                    LOGGER.severe("Process' worker idle or not running the process: " + processId);
                    workerUnreachableSince.remove(processId);
                    processService.updateState(processId, ProcessState.FAILED);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error during GC of process: " + processId, e);
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            this.gcScheduler.scheduleDoGc();
        }
    }
}
