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
package org.ceskaexpedice.processplatform.common;

import java.util.List;

public enum TaskState {

    /**
     * Not running process
     */
    NOT_RUNNING(0),

    /**
     * Running proces
     */
    RUNNING(1),

    /**
     * Correct finished proces
     */
    FINISHED(2),

    /**
     * FAiled with some errors
     */
    FAILED(3),

    /**
     * Killed process
     */
    KILLED(4),

    /**
     * Planned (process is waiting to start)
     */
    PLANNED(5),

    /**
     * Finished with some errors
     */
    WARNING(9),

    /*
     * WARNING(10)
     */

    /**
     * Batch process started (contains child processes and all of them are
     * PLANNED or RUNNING).
     */
    @Deprecated
    BATCH_STARTED(6),


    /**
     * Batch process failed (some of child process FAILED)
     */
    @Deprecated
    BATCH_FAILED(7),

    /**
     * Batch process finished (all child processes finished with state FINISH)
     */
    @Deprecated
    BATCH_FINISHED(8);



    /**
     * Load state from value
     * @param v Given value of state
     * @return
     */
    public static TaskState load(int v) {
        for (TaskState st : TaskState.values()) {
            if (st.getVal() == v)
                return st;
        }
        return null;
    }

    private int val;

    private TaskState(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

//    /**
//     * Calculate master batch process state
//     * @param childStates Child process states
//     * @return
//     */
//    public static States calculateBatchState(List<States> childStates) {
//        // ve stavu planned nebo running
//        if (one(childStates, FAILED)) {
//            return BATCH_FAILED;
//        } else {
//            if (one(childStates, PLANNED, RUNNING)) {
//                return BATCH_STARTED;
//            }
//            return BATCH_FINISHED;
//        }
//    }

    /**
     * Returns true if one of given childStates contains any expecting state
     * @param childStates Child states
     * @param exp Expecting states
     * @return
     */
    public static boolean one(List<TaskState> childStates, TaskState... exp) {
        for (TaskState st : childStates) {
            if (expect(st, exp))
                return true;
        }
        return false;
    }

    /**
     * Returns true if all of given child states contains any expecting state
     * @param childStates Child states
     * @param exp Execting state
     * @return
     */
    public static boolean all(List<TaskState> childStates, TaskState... exp) {
        for (TaskState st : childStates) {
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
    public static boolean expect(TaskState real, TaskState... expected) {
        for (TaskState exp : expected) {
            if (real.equals(exp))
                return true;
        }
        return false;
    }



    /**
     * Returns true given state is not running state
     * @param realState
     * @return
     */
    public static boolean notRunningState(TaskState realState) {
        return expect(realState, TaskState.FAILED, TaskState.FINISHED, TaskState.KILLED, TaskState.NOT_RUNNING, TaskState.WARNING, TaskState.BATCH_FAILED, TaskState.BATCH_FINISHED, TaskState.BATCH_STARTED);
    }}