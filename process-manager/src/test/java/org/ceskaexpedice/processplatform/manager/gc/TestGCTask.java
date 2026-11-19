package org.ceskaexpedice.processplatform.manager.gc;


import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.WorkerInfo;
import org.ceskaexpedice.processplatform.common.model.WorkerState;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private ConcurrentMap<String, Long> notRunningWorkerUnreachableSince;
    private ConcurrentMap<String, Integer> notRunningFailures;
    private AtomicLong currentTime;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        workerUnreachableSince = new ConcurrentHashMap<>();
        notRunningWorkerUnreachableSince = new ConcurrentHashMap<>();
        notRunningFailures = new ConcurrentHashMap<>();
        currentTime = new AtomicLong(0);

        when(gcScheduler.getWorkerUnreachableSince()).thenReturn(workerUnreachableSince);
        when(gcScheduler.getNotRunningWorkerUnreachableSince()).thenReturn(notRunningWorkerUnreachableSince);
        when(gcScheduler.getNotRunningFailures()).thenReturn(notRunningFailures);
        when(processService.getProcesses(ProcessState.RUNNING)).thenReturn(Collections.singletonList(processInfo));
        when(processService.getProcesses(ProcessState.NOT_RUNNING)).thenReturn(Collections.emptyList());
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
    void testRunning_WorkerUnavailableFirstTime() {
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
    void testRunning_WorkerUnavailableForMoreThan60Seconds() {
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
    void testRunning_SuccessfulCommunicationResetsFailureTimer() {
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
    void testRunning_WorkerIdleMarksProcessFailedImmediately() {
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
    void testRunning_WorkerRunningDifferentProcessMarksFailedImmediately() {
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
    void testRunning_ProcessWithoutPid() {
        when(processInfo.getPid()).thenReturn(null);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        verify(processService, never()).getWorkerInfo(PROCESS_ID);
    }

    @Test
    void testNotRunning_FirstNegativeCheck() {
        prepareNotRunningProcess();
        prepareWorkerIdle();

        runGc();

        Assertions.assertEquals(1, notRunningFailures.get(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_SecondNegativeCheckMarksFailed() {
        prepareNotRunningProcess();
        prepareWorkerIdle();

        runGc();
        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        Assertions.assertFalse(notRunningFailures.containsKey(PROCESS_ID));
    }

    @Test
    void testNotRunning_CorrectProcessResetsFailureCounter() {
        prepareNotRunningProcess();
        notRunningFailures.put(PROCESS_ID, 1);
        ScheduledProcess currentProcess = Mockito.mock(ScheduledProcess.class);
        when(currentProcess.getProcessId()).thenReturn(PROCESS_ID);
        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.RUNNING);
        when(workerInfo.isJvmAlive()).thenReturn(true);
        when(workerInfo.getCurrentProcess()).thenReturn(currentProcess);

        runGc();

        Assertions.assertFalse(notRunningFailures.containsKey(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_WorkerRunsDifferentProcess() {
        prepareNotRunningProcess();

        ScheduledProcess currentProcess = Mockito.mock(ScheduledProcess.class);
        when(currentProcess.getProcessId()).thenReturn("another-process");

        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.RUNNING);
        when(workerInfo.getCurrentProcess()).thenReturn(currentProcess);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        Assertions.assertFalse(notRunningFailures.containsKey(PROCESS_ID));
    }

    @Test
    void testNotRunning_WorkerUnavailableFirstTime() {
        prepareNotRunningProcess();
        when(processService.getWorkerInfo(PROCESS_ID)).thenThrow(new RuntimeException("Worker unavailable"));

        runGc();

        Assertions.assertTrue(notRunningWorkerUnreachableSince.containsKey(PROCESS_ID));
        Assertions.assertFalse(notRunningFailures.containsKey(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_WorkerUnavailableLessThanTimeout() {
        prepareNotRunningProcess();

        when(processService.getWorkerInfo(PROCESS_ID)).thenThrow(new RuntimeException("Worker unavailable"));
        runGc();

        currentTime.set(FAILURE_TIMEOUT - 1);
        runGc();

        Assertions.assertTrue(notRunningWorkerUnreachableSince.containsKey(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_WorkerUnavailableForTimeoutMarksFailed() {
        prepareNotRunningProcess();

        when(processService.getWorkerInfo(PROCESS_ID)).thenThrow(new RuntimeException("Worker unavailable"));
        runGc();

        currentTime.set(FAILURE_TIMEOUT);
        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        Assertions.assertFalse(notRunningWorkerUnreachableSince.containsKey(PROCESS_ID));
    }

    @Test
    void testNotRunning_SuccessfulCommunicationResetsUnreachableTimer() {
        prepareNotRunningProcess();

        when(processService.getWorkerInfo(PROCESS_ID))
                .thenThrow(new RuntimeException("Worker unavailable"))
                .thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.IDLE);

        runGc();

        currentTime.set(TimeUnit.SECONDS.toNanos(30));
        runGc();

        Assertions.assertFalse(notRunningWorkerUnreachableSince.containsKey(PROCESS_ID));
        Assertions.assertEquals(1, notRunningFailures.get(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_LastProcessFinishedSuccessfully() {
        prepareNotRunningProcess();
        prepareLastProcess(0);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FINISHED);
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
    }

    @Test
    void testNotRunning_LastProcessFinishedWithError() {
        prepareNotRunningProcess();
        prepareLastProcess(1);

        runGc();

        verify(processService, times(1)).updateState(PROCESS_ID, ProcessState.FAILED);
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FINISHED);
    }

    @Test
    void testNotRunning_WorkerRunningWithoutCurrentProcess() {
        prepareNotRunningProcess();

        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.RUNNING);
        when(workerInfo.getCurrentProcess()).thenReturn(null);

        runGc();

        Assertions.assertEquals(1, notRunningFailures.get(PROCESS_ID));
        verify(processService, never()).updateState(PROCESS_ID, ProcessState.FAILED);
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

    private void prepareNotRunningProcess() {
        when(processService.getProcesses(ProcessState.RUNNING)).thenReturn(Collections.emptyList());
        when(processService.getProcesses(ProcessState.NOT_RUNNING)).thenReturn(Collections.singletonList(processInfo));
    }

    private void prepareWorkerIdle() {
        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.IDLE);
        when(workerInfo.isJvmAlive()).thenReturn(false);
    }

    private void prepareLastProcess(int exitCode) {
        ScheduledProcess lastProcess = Mockito.mock(ScheduledProcess.class);
        when(lastProcess.getProcessId()).thenReturn(PROCESS_ID);

        when(processService.getWorkerInfo(PROCESS_ID)).thenReturn(workerInfo);
        when(workerInfo.getState()).thenReturn(WorkerState.IDLE);
        when(workerInfo.getLastProcess()).thenReturn(lastProcess);
        when(workerInfo.getLastExitCode()).thenReturn(exitCode);
    }
}
