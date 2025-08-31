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
package org.ceskaexpedice.processplatform.worker.client;

import org.ceskaexpedice.processplatform.common.RemoteNodeException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TestManagerClient
 *
 * @author ppodsednik
 */
public class TestManagerClient {

    private HttpServer server;
    private WorkerConfiguration workerConfiguration;

    @BeforeEach
    public void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(ForWorkerTestEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(WorkerTestsUtils.MANAGER_BASE_URI), rc);
        server.start();
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.setManagerBaseUrl(WorkerTestsUtils.MANAGER_BASE_URI);
        workerConfiguration.setWorkerId("workerPepo");
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testRegisterNode() {
        Node node = new Node();
        node.setNodeId(NODE_WORKER1_ID);

        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.registerNode(node);
    }

    @Test
    public void testRegisterPlugin() {
        PluginInfo testPlugin1 = new  PluginInfo();
        testPlugin1.setPluginId(PLUGIN1_ID);
        PluginProfile pluginProfile = new  PluginProfile();
        pluginProfile.setProfileId(PLUGIN1_PROFILE_SMALL);
        pluginProfile.setPluginId(PLUGIN1_ID);
        testPlugin1.setProfiles(Collections.singletonList(pluginProfile));

        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.registerPlugin(testPlugin1);
    }

    @Test
    public void testGetNextScheduledProcess() {
        ForWorkerTestEndpoint.counter = 0;
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        ScheduledProcess nextProcess = managerClient.getNextScheduledProcess();
        Assertions.assertEquals(PLUGIN1_PROCESS_ID, nextProcess.getProcessId());
        Assertions.assertEquals(PLUGIN1_PROFILE_SMALL, nextProcess.getProfileId());
    }

    @Test
    public void testScheduleSubProcess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Petr");
        payload.put("surname", "Harasil");
        ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess(
                PLUGIN1_PROFILE_BIG,
                payload
        );
        scheduleSubProcess.setBatchId("batchId1");

        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.scheduleSubProcess(scheduleSubProcess);
    }

    @Test
    public void testUpdateProcessPid() {
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessPid(PLUGIN1_PROCESS_ID, "333");
    }

    @Test
    public void testUpdateProcessPid_error() {
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        assertThrows(RemoteNodeException.class, () -> {
            managerClient.updateProcessPid(PROCESS_ID_NOT_EXISTS, "333");
        });
    }

    @Test
    public void testUpdateProcessState() {
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessState(PLUGIN1_PROCESS_ID, ProcessState.FINISHED);
    }

    @Test
    public void testUpdateProcessDescription() {
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessDescription(PLUGIN1_PROCESS_ID, "newName");
    }

}
