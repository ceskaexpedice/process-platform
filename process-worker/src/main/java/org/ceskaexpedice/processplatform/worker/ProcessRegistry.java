package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.common.model.WorkerState;

/**
 * ProcessRegistry
 * @author ppodsednik
 */
public class ProcessRegistry {

    private static final ProcessRegistry INSTANCE = new ProcessRegistry();

    private volatile WorkerState state = WorkerState.IDLE;
    private volatile Long currentPid;
    private volatile Integer lastExitCode;
    private volatile ScheduledProcess currentProcess;
    private volatile ScheduledProcess lastProcess;

    private ProcessRegistry() {}

    public static ProcessRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void enterRunning(Process process, ScheduledProcess scheduledProcess) {
        this.state = WorkerState.RUNNING;
        this.currentPid = process.pid();
        this.lastExitCode = null;
        this.currentProcess = scheduledProcess;
    }

    public synchronized void markFinished(int exitCode) {
        this.lastProcess = this.currentProcess;
        this.lastExitCode = exitCode;

        this.currentProcess = null;
        this.currentPid = null;
        this.state = WorkerState.IDLE;
    }

    public synchronized void markIdle() {
        this.state = WorkerState.IDLE;
        this.currentPid = null;
        this.currentProcess = null;
    }

    public WorkerState getState() {
        return state;
    }

    public Long getCurrentPid() {
        return currentPid;
    }

    public boolean isCurrentAlive() {
        Long pid = currentPid;
        if (pid == null) return false;
        return ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
    }

    public Integer getLastExitCode() {
        return lastExitCode;
    }

    public ScheduledProcess getCurrentProcess() {
        return currentProcess;
    }

    public ScheduledProcess getLastProcess() {
        return lastProcess;
    }
}