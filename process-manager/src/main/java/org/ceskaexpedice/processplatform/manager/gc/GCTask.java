
package org.ceskaexpedice.processplatform.manager.gc;

import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.common.model.WorkerInfo;
import org.ceskaexpedice.processplatform.common.model.WorkerState;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(GCTask.class.getName());
    private static final long WORKER_FAILURE_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(60);
    private static final long NOT_RUNNING_WORKER_FAILURE_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(60);
    private static final int NOT_RUNNING_FAILURE_LIMIT = 2;

    private final ConcurrentMap<String, Long> workerUnreachableSince;
    private final ConcurrentMap<String, Integer> notRunningFailures;
    private final ConcurrentMap<String, Long> notRunningWorkerUnreachableSince;
    private final ProcessService processService;
    private final NodeService nodeService;
    private final GCScheduler gcScheduler;
    private final LongSupplier nanoTime;

    public GCTask(ProcessService processService, NodeService nodeService, GCScheduler gcScheduler) {
        this(processService, nodeService, gcScheduler, System::nanoTime);
    }

    GCTask(ProcessService processService, NodeService nodeService, GCScheduler gcScheduler, LongSupplier nanoTime) {
        this.processService = processService;
        this.gcScheduler = gcScheduler;
        this.nodeService = nodeService;
        this.workerUnreachableSince = gcScheduler.getWorkerUnreachableSince();
        this.notRunningFailures = gcScheduler.getNotRunningFailures();
        this.notRunningWorkerUnreachableSince = gcScheduler.getNotRunningWorkerUnreachableSince();
        this.nanoTime = nanoTime;
    }

    @Override
    public void run() {
        try {
            checkRunningProcesses();
            checkNotRunningProcesses();
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Error during GC", e);
        } finally {
            gcScheduler.scheduleDoGc();
        }
    }

    private void checkRunningProcesses() {
        List<ProcessInfo> processInfos = processService.getProcesses(ProcessState.RUNNING);
        Set<String> runningProcessIds = new HashSet<>();
        for (ProcessInfo processInfo : processInfos) {
            runningProcessIds.add(processInfo.getProcessId());
        }
        // Remove records for processes that are no longer RUNNING.
        workerUnreachableSince.keySet().removeIf(processId -> !runningProcessIds.contains(processId));
        for (ProcessInfo processInfo : processInfos) {
            checkRunningProcess(processInfo);
        }
    }

    private void checkRunningProcess(ProcessInfo processInfo) {
        String processId = processInfo.getProcessId();
        try {
            if (processInfo.getPid() == null) {
                LOGGER.warning("Found RUNNING process without pid: " + processId);
                processService.updateState(processId, ProcessState.FAILED);
                workerUnreachableSince.remove(processId);
                return;
            }
            WorkerInfo workerInfo;
            try {
                workerInfo = processService.getWorkerInfo(processId);
            } catch (Exception e) {
                handleUnreachableWorker(processId, e);
                return;
            }
            // Communication succeeded - reset the timeout.
            workerUnreachableSince.remove(processId);
            if (workerInfo != null
                    && workerInfo.getState() == WorkerState.RUNNING
                    && workerInfo.isJvmAlive()
                    && workerInfo.getCurrentPid() != null
                    && workerInfo.getCurrentPid().equals(
                    processInfo.getPid())) {
                return;
            }
            LOGGER.severe("Process worker is idle or not running the expected process: " + processId);
            processService.updateState(processId, ProcessState.FAILED);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during GC of RUNNING process: " + processId, e);
        }
    }

    private void handleUnreachableWorker(String processId, Exception e) {
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
            // Remove the record only after a successful state update.
            processService.updateState(processId, ProcessState.FAILED);
            workerUnreachableSince.remove(processId);
        } else {
            LOGGER.log(Level.WARNING,
                    "Cannot access worker for process: " + processId
                            + ". Keeping process RUNNING.",
                    e);
        }
    }

    private void checkNotRunningProcesses() {
        List<ProcessInfo> processInfos = processService.getProcesses(ProcessState.NOT_RUNNING);
        Set<String> notRunningProcessIds = new HashSet<>();
        for (ProcessInfo processInfo : processInfos) {
            notRunningProcessIds.add(processInfo.getProcessId());
        }
        // Remove records for processes that are no longer NOT_RUNNING.
        notRunningFailures.keySet().removeIf(processId -> !notRunningProcessIds.contains(processId));
        for (ProcessInfo processInfo : processInfos) {
            checkNotRunningProcess(processInfo);
        }
    }

    private void checkNotRunningProcess(ProcessInfo processInfo) {
        String processId = processInfo.getProcessId();
        try {
            WorkerInfo workerInfo;
            try {
                workerInfo = processService.getWorkerInfo(processId);
            } catch (Exception e) {
                handleNotRunningWorkerUnavailable(processId, e);
                return;
            }
            // No worker node found - treat it as unavailable.
            if (workerInfo == null) {
                handleNotRunningWorkerUnavailable(processId, null);
                return;
            }
            // Communication succeeded.
            notRunningWorkerUnreachableSince.remove(processId);
            // The expected process has already finished on the worker.
            if (isLastProcess(workerInfo, processId)) {
                Integer exitCode = workerInfo.getLastExitCode();
                if (exitCode == null) {
                    LOGGER.warning("Worker reports the process as lastProcess, "
                            + "but lastExitCode is null: " + processId);
                    notRunningFailures.remove(processId);
                    return;
                }
                ProcessState finalState = exitCode == 0 ? ProcessState.FINISHED : ProcessState.FAILED;
                LOGGER.info("Process " + processId + " has already finished on worker. Exit code: "
                        + exitCode + ", setting state to " + finalState);
                processService.updateState(processId, finalState);
                notRunningFailures.remove(processId);
                return;
            }
            // The worker is currently starting or running the expected process.
            if (isWorkerRunningThisProcess(workerInfo, processId)) {
                notRunningFailures.remove(processId);
                return;
            }
            // Worker is running a different process.
            // Since one worker can execute only one process at a time,
            // the expected process cannot be running.
            if (workerInfo.getState() == WorkerState.RUNNING && workerInfo.getCurrentProcess() != null) {
                LOGGER.severe("Worker is running a different process. "
                        + "Marking process as FAILED: " + processId);
                processService.updateState(processId, ProcessState.FAILED);
                notRunningFailures.remove(processId);
                return;
            }

            // Worker is idle or its state is inconclusive.
            // Require two consecutive negative checks.
            int failures = notRunningFailures.merge(processId, 1, Integer::sum);
            LOGGER.warning("Worker is not running process " + processId
                    + ". Negative check: " + failures + "/2");
            if (failures >= 2) {
                LOGGER.severe("Process was not running on worker "
                        + "during two consecutive checks. Marking as FAILED: "
                        + processId);
                processService.updateState(processId, ProcessState.FAILED);
                notRunningFailures.remove(processId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during GC of NOT_RUNNING process: " + processId, e);
        }
    }

    private void handleNotRunningWorkerUnavailable(String processId, Exception exception) {
        // Communication failure is not a negative check.
        notRunningFailures.remove(processId);
        long now = nanoTime.getAsLong();
        Long firstFailure = notRunningWorkerUnreachableSince.putIfAbsent(processId, now);
        if (firstFailure == null) {
            firstFailure = now;
        }
        long unavailableNanos = now - firstFailure;
        if (unavailableNanos >= NOT_RUNNING_WORKER_FAILURE_TIMEOUT_NANOS) {
            LOGGER.log(Level.SEVERE,
                    "Worker unreachable for at least 60 seconds. "
                            + "Marking NOT_RUNNING process as FAILED: " + processId,
                    exception);
            processService.updateState(processId, ProcessState.FAILED);
            notRunningWorkerUnreachableSince.remove(processId);
        } else {
            LOGGER.log(Level.WARNING,
                    "Cannot access worker for NOT_RUNNING process: " + processId
                            + ". Keeping process NOT_RUNNING.",
                    exception);
        }
    }

    private boolean isWorkerRunningThisProcess(WorkerInfo workerInfo, String processId) {
        if (workerInfo.getState() != WorkerState.RUNNING) {
            return false;
        }
        ScheduledProcess currentProcess = workerInfo.getCurrentProcess();
        return currentProcess != null && processId.equals(currentProcess.getProcessId());
    }

    private boolean isLastProcess(WorkerInfo workerInfo, String processId) {
        ScheduledProcess lastProcess = workerInfo.getLastProcess();
        return lastProcess != null && processId.equals(lastProcess.getProcessId());
    }
}
