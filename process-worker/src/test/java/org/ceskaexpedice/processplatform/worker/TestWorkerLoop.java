/*
 * Copyright © 2021 Accenture and/or its affiliates. All Rights Reserved.
 * Permission to any use, copy, modify, and distribute this software and
 * its documentation for any purpose is subject to a licensing agreement
 * duly entered into with the copyright owner or its affiliate.
 * All information contained herein is, and remains the property of Accenture
 * and/or its affiliates and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Accenture and/or
 * its affiliates and its suppliers and may be covered by one or more patents
 * or pending patent applications in one or more jurisdictions worldwide,
 * and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Accenture and/or its affiliates.
 */
package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TestWorkerLoop {

    private WorkerConfiguration workerConfiguration;
    private ManagerClient managerClient;
    private Node node;
    private PluginInfo plugin1;
    private PluginInfo plugin2;

    @BeforeEach
    void setUp() {
        workerConfiguration = Mockito.mock(WorkerConfiguration.class);
        managerClient = Mockito.mock(ManagerClient.class);

        node = Mockito.mock(Node.class);
        plugin1 = Mockito.mock(PluginInfo.class);
        plugin2 = Mockito.mock(PluginInfo.class);

        when(node.getNodeId()).thenReturn("testWorker");
        when(plugin1.getPluginId()).thenReturn("plugin1");
        when(plugin2.getPluginId()).thenReturn("plugin2");

        // Long idle interval. The test will stop the worker explicitly.
        when(workerConfiguration.getWorkerLoopSleepSecs()).thenReturn(60.0);
        when(workerConfiguration.getWorkerLoopPrintSecs()).thenReturn(60.0);
    }

    /**
     * Successful registration should be followed by polling.
     */
    @Test
    void testSuccessfulRegistrationAndPolling() throws Exception {
        WorkerLoop workerLoop = createWorkerLoop();
        try {
            workerLoop.start();
            // Wait until the first polling request is made.
            verify(managerClient, timeout(5000)).getNextScheduledProcess();
            verify(managerClient, times(1)).registerNode(node);
            verify(managerClient, times(1)).registerPlugin(plugin1);
            verify(managerClient, times(1)).registerPlugin(plugin2);
        } finally {
            workerLoop.stop();
            awaitWorkerStopped(workerLoop);
        }
    }

    /**
     * If node registration fails, the worker should retry registration.
     */
    @Test
    void testRetryRegistrationWhenRegisterNodeFails() throws Exception {
        doThrow(new RuntimeException("Manager unavailable"))
                .doNothing()
                .when(managerClient).registerNode(node);
        WorkerLoop workerLoop = createWorkerLoop();
        try {
            workerLoop.start();
            // The second attempt should happen after the initial 2-second backoff.
            verify(managerClient, timeout(5000).times(2)).registerNode(node);
            // After successful node registration, plugins should be registered.
            verify(managerClient, timeout(5000)).registerPlugin(plugin1);
            verify(managerClient, times(1)).registerPlugin(plugin2);
            // Worker should continue with polling.
            verify(managerClient, timeout(5000)).getNextScheduledProcess();
        } finally {
            workerLoop.stop();
            awaitWorkerStopped(workerLoop);
        }
    }

    /**
     * If plugin registration fails, the complete registration
     * (node and all plugins) should be retried.
     */
    @Test
    void testRetryRegistrationWhenRegisterPluginFails() throws Exception {
        doThrow(new RuntimeException("Manager unavailable"))
                .doNothing()
                .when(managerClient).registerPlugin(plugin1);
        WorkerLoop workerLoop = createWorkerLoop();
        try {
            workerLoop.start();
            // First registration attempt fails on plugin1.
            // The second attempt starts again with node registration.
            verify(managerClient, timeout(5000).times(2)).registerNode(node);
            verify(managerClient, timeout(5000).times(2)).registerPlugin(plugin1);
            // plugin2 is registered only during the successful attempt.
            verify(managerClient, timeout(5000).times(1)).registerPlugin(plugin2);
            // Polling starts only after successful registration.
            verify(managerClient, timeout(5000)).getNextScheduledProcess();
        } finally {
            workerLoop.stop();
            awaitWorkerStopped(workerLoop);
        }
    }

    /**
     * If polling fails, the worker should not terminate.
     * It should retry polling after backoff.
     */
    @Test
    void testRetryPollingWhenManagerFails() throws Exception {
        when(managerClient.getNextScheduledProcess())
                .thenThrow(new RuntimeException("Manager unavailable"))
                .thenReturn(null);
        WorkerLoop workerLoop = createWorkerLoop();
        try {
            workerLoop.start();
            // First polling request fails, second request succeeds.
            verify(managerClient, timeout(5000).times(2)).getNextScheduledProcess();
            // Registration must not be repeated after a polling failure.
            verify(managerClient, times(1)).registerNode(node);
            verify(managerClient, times(1)).registerPlugin(plugin1);
            verify(managerClient, times(1)).registerPlugin(plugin2);
        } finally {
            workerLoop.stop();
            awaitWorkerStopped(workerLoop);
        }
    }

    /**
     * Stopping the worker during registration retry wait
     * should wake up the thread and terminate it.
     */
    @Test
    void testStopDuringRegistrationRetry() throws Exception {
        doThrow(new RuntimeException("Manager unavailable")).when(managerClient).registerNode(node);

        WorkerLoop workerLoop = createWorkerLoop();
        workerLoop.start();
        // Wait until the first registration attempt has failed.
        verify(managerClient, timeout(3000)).registerNode(node);
        // The worker is now waiting for the retry.
        workerLoop.stop();
        awaitWorkerStopped(workerLoop);
        // No second registration attempt should occur.
        verify(managerClient, times(1)).registerNode(node);

        verify(managerClient, never()).registerPlugin(any());
        verify(managerClient, never()).getNextScheduledProcess();
    }

    /**
     * Wait until the polling thread has actually terminated.
     */
    private void awaitWorkerStopped(WorkerLoop workerLoop) throws Exception {
        Field field = WorkerLoop.class.getDeclaredField("pollingThread");
        field.setAccessible(true);
        Thread pollingThread = (Thread) field.get(workerLoop);
        assertNotNull(pollingThread);
        pollingThread.join(TimeUnit.SECONDS.toMillis(5));
        assertFalse(pollingThread.isAlive(), "Worker polling thread should have terminated");
    }

    private WorkerLoop createWorkerLoop() {
        return new WorkerLoop(
                workerConfiguration,
                managerClient,
                node,
                List.of(plugin1, plugin2)
        );
    }

}
