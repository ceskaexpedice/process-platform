package org.ceskaexpedice.processplatform.common.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Processes states
 *
 * @author pavels
 */
public enum ProcessState {

    /**
     * Correct finished proces
     */
    FINISHED(0),

    /**
     * FAiled with some errors
     */
    FAILED(1),

    /**
     * Killed process
     */
    KILLED(2),

    /**
     * Finished with some errors
     */
    WARNING(3),

    /**
     * Not running process
     */
    NOT_RUNNING(4),

    /**
     * Planned (process is waiting to start)
     */
    PLANNED(5),

    /**
     * Running proces
     */
    RUNNING(6);

    /**
     * Load state from value
     * @param v Given value of state
     * @return
     */
    public static ProcessState load(int v) {
        for (ProcessState st : ProcessState.values()) {
            if (st.getVal() == v)
                return st;
        }
        return null;
    }

    private int val;

    private ProcessState(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    /**
     * Calculate master batch process state
     * @param childStates Child process states
     * @return
     */
    public static ProcessState calculateBatchState(List<ProcessState> childStates) {
        return childStates.stream()
                .max(Comparator.comparingInt(ProcessState::getVal))
                .orElse(ProcessState.PLANNED); // default if empty list
    }

    /**
     * Returns true if one of given childStates contains any expecting state
     * @param childProcessStates Child states
     * @param exp Expecting states
     * @return
     */
    public static boolean one(List<ProcessState> childProcessStates, ProcessState... exp) {
        for (ProcessState st : childProcessStates) {
            if (expect(st, exp))
                return true;
        }
        return false;
    }

    /**
     * Returns true if all of given child states contains any expecting state
     * @param childProcessStates Child states
     * @param exp Execting state
     * @return
     */
    public static boolean all(List<ProcessState> childProcessStates, ProcessState... exp) {
        for (ProcessState st : childProcessStates) {
            if (!expect(st, exp))
                return false;
        }
        return true;
    }

    /**
     * Returns true if given real state is one of expecting state
     * @param real Real state
     * @param expected Expecting states
     * @return
     */
    public static boolean expect(ProcessState real, ProcessState... expected) {
        for (ProcessState exp : expected) {
            if (real.equals(exp))
                return true;
        }
        return false;
    }


    /**
     * Returns true given state is not running state
     * @param realProcessState
     * @return
     */
    public static boolean notRunningState(ProcessState realProcessState) {
        return expect(realProcessState, ProcessState.FAILED, ProcessState.FINISHED, ProcessState.KILLED, ProcessState.NOT_RUNNING, ProcessState.WARNING);
    }

    public static boolean isBatchStateKillable(ProcessState batchState) {
        return expect(batchState, ProcessState.PLANNED, ProcessState.NOT_RUNNING, ProcessState.RUNNING);
    }

    public static boolean isBatchStateDeletable(ProcessState batchState) {
        return expect(batchState, ProcessState.PLANNED, ProcessState.FAILED, ProcessState.KILLED);
    }

    public static boolean completedState(ProcessState realProcessState) {
        return expect(realProcessState, ProcessState.FAILED, ProcessState.FINISHED, ProcessState.KILLED, ProcessState.WARNING);
    }

}
