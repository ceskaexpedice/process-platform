package org.ceskaexpedice.processplatform.manager.gc;


import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.WorkerInfo;
import org.ceskaexpedice.processplatform.common.model.WorkerState;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.*;

class TestGCTask {

    private static final String PROCESS_ID = "process-123";
    private static final Long PROCESS_PID = 123L;
    private static final long FAILURE_TIMEOUT = TimeUnit.SECONDS.toNanos(60);

    @Mock
    private ProcessService processService;
    @Mock
    private NodeService nodeService;
    @Mock
    private GCScheduler gcScheduler;
    @Mock
    private ProcessInfo processInfo;
    @Mock
    private WorkerInfo workerInfo;

    private AutoCloseable mocks;
    private ConcurrentMap<String, Long> workerUnreachableSince;
    private AtomicLong currentTime;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        workerUnreachableSince = new ConcurrentHashMap<>();
        currentTime = new AtomicLong(0);

        when(gcScheduler.getWorkerUnreachableSince()).thenReturn(workerUnreachableSince);
        when(processService.getProcesses(ProcessState.RUNNING)).thenReturn(Collections.singletonList(processInfo));
        when(processInfo.getProcessId()).thenReturn(PROCESS_ID);
        when(processInfo.getPid()).thenReturn(PROCESS_PID);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    /**
     * First communication failure must not mark the process as FAILED.
     */
    @Test
    void testWorkerUnavailableFirstTime() {
        when(processService.getWorkerInfo(PROCESS_ID)).thenThrow(new RuntimeException("Worker unavailable"));
        runGc();
        Assertions.assertTrue(workerUnreachableSince.containsKey(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    /**
     * Process must be marked FAILED after 60 seconds
     * of consecutive communication failures.
     * <p>
     * Each GC run creates a new GCTask instance.
     */
    @Test
    void testWorkerUnavailableForMoreThan60Seconds() {
        when(processService.getWorkerInfo(PROCESS_ID)).thenThrow(new RuntimeException("Worker unavailable"));
        // First GC run.
        currentTime.set(0);

        runGc();

        Assertions.assertTrue(workerUnreachableSince.containsKey(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
        // Second GC run - 61 seconds later.
        currentTime.set(FAILURE_TIMEOUT + 1);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        Assertions.assertFalse(workerUnreachableSince.containsKey(PROCESS_ID));
    }

    /**
     * Successful communication resets the failure timer.
     * A subsequent failure starts a new 60-second period.
     */
    @Test
    void testSuccessfulCommunicationResetsFailureTimer() {
        when(processService.getWorkerInfo(PROCESS_ID))
                .thenThrow(new RuntimeException("Worker unavailable"))
                .thenReturn(workerInfo)
                .thenThrow(new RuntimeException("Worker unavailable"));
        // First failure.
        currentTime.set(0);

        runGc();

        Assertions.assertTrue(workerUnreachableSince.containsKey(PROCESS_ID));
        // Successful communication after 30 seconds.
        currentTime.set(TimeUnit.SECONDS.toNanos(30));
        when(workerInfo.getState()).thenReturn(WorkerState.RUNNING);
        when(workerInfo.isJvmAlive()).thenReturn(true);
        when(workerInfo.getCurrentPid()).thenReturn(PROCESS_PID);

        runGc();

        Assertions.assertFalse(workerUnreachableSince.containsKey(PROCESS_ID));

        // Another failure after 31 seconds.
        currentTime.set(TimeUnit.SECONDS.toNanos(31));
        runGc();

        // The timer must have started again from the last failure.
        Assertions.assertEquals(
                TimeUnit.SECONDS.toNanos(31),
                workerUnreachableSince.get(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    /**
     * Worker responds, but reports that it is idle.
     * Process must be marked FAILED immediately.
     */
    @Test
    void testWorkerIdleMarksProcessFailedImmediately() {
        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.IDLE);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        Assertions.assertFalse(workerUnreachableSince.containsKey(PROCESS_ID));
    }

    /**
     * Worker responds, but reports a different PID.
     * Process must be marked FAILED immediately.
     */
    @Test
    void testWorkerRunningDifferentProcessMarksFailedImmediately() {
        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);

        when(workerInfo.getState()).thenReturn(WorkerState.RUNNING);
        when(workerInfo.isJvmAlive()).thenReturn(true);
        when(workerInfo.getCurrentPid()).thenReturn(456L);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    /**
     * Running process without PID must be marked FAILED immediately.
     */
    @Test
    void testRunningProcessWithoutPid() {
        when(processInfo.getPid()).thenReturn(null);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        verify(processService, never()).getWorkerInfo(PROCESS_ID);
    }

    /**
     * Helper creates a new GCTask instance for every GC run,
     * just like GCScheduler.scheduleDoGc().
     */
    private void runGc() {
        GCTask gcTask = new GCTask(processService, nodeService, gcScheduler, currentTime::get);
        gcTask.run();
        verify(gcScheduler, atLeastOnce()).scheduleDoGc();
        clearInvocations(gcScheduler);
    }
}
