package org.ceskaexpedice.processplatform.common.model;

/**
 * WorkerInfo
 * @author ppodsednik
 */
public class WorkerInfo {

    private WorkerState state = WorkerState.IDLE;
    private Long currentPid;
    private Integer lastExitCode;
    private boolean jvmAlive;
    private ScheduledProcess currentProcess;
    private ScheduledProcess lastProcess;

    public WorkerInfo() {}

    public WorkerState getState() {
        return state;
    }

    public void setState(WorkerState state) {
        this.state = state;
    }

    public Long getCurrentPid() {
        return currentPid;
    }

    public void setCurrentPid(Long currentPid) {
        this.currentPid = currentPid;
    }

    public Integer getLastExitCode() {
        return lastExitCode;
    }

    public void setLastExitCode(Integer lastExitCode) {
        this.lastExitCode = lastExitCode;
    }

    public boolean isJvmAlive() {
        return jvmAlive;
    }

    public void setJvmAlive(boolean jvmAlive) {
        this.jvmAlive = jvmAlive;
    }

    public ScheduledProcess getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(ScheduledProcess currentProcess) {
        this.currentProcess = currentProcess;
    }

    public ScheduledProcess getLastProcess() {
        return lastProcess;
    }

    public void setLastProcess(ScheduledProcess lastProcess) {
        this.lastProcess = lastProcess;
    }
}