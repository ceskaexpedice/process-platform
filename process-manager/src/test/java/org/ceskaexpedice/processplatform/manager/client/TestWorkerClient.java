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
package org.ceskaexpedice.processplatform.manager.client;

import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.testutils.ManagerTestsUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.ceskaexpedice.processplatform.manager.client.ForManagerTestEndpoint.OUT_LOG_PART;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * TestWorkerClient
 *
 * @author ppodsednik
 */
public class TestWorkerClient {
    @Mock
    private ProcessService processServiceMock;
    @Mock
    private NodeService nodeServiceMock;

    private HttpServer server;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        final ResourceConfig rc = new ResourceConfig(ForManagerTestEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(ManagerTestsUtils.WORKER_BASE_URI), rc);
        server.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testGetProcessLog() throws IOException {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcessId(ManagerTestsUtils.PROCESS1_ID);
        processInfo.setWorkerId(ManagerTestsUtils.NODE_WORKER1_ID);
        when(processServiceMock.getProcess(eq(ManagerTestsUtils.PROCESS1_ID))).thenReturn(processInfo);
        Node node = new Node();
        node.setNodeId(ManagerTestsUtils.NODE_WORKER1_ID);
        node.setUrl(ManagerTestsUtils.WORKER_BASE_URI);
        when(nodeServiceMock.getNode(eq(ManagerTestsUtils.NODE_WORKER1_ID))).thenReturn(node);

        WorkerClient workerClient = new WorkerClient(processServiceMock, nodeServiceMock);
        InputStream processLog = workerClient.getProcessLog(ManagerTestsUtils.PROCESS1_ID, false);
        String outLog = new String(processLog.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertTrue(outLog.contains(OUT_LOG_PART));
    }
}
