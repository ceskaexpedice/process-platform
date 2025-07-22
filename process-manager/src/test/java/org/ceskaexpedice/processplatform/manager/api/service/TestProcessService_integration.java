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
package org.ceskaexpedice.processplatform.manager.api.service;

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.testutils.IntegrationTestsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TestProcessService_integration
 *
 * @author ppodsednik
 */
public class TestProcessService_integration {
    private static Properties testsProperties;
    private static ProcessService processService;
    private static NodeService nodeService;
    private static DbConnectionProvider dbConnectionProvider;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        PluginService pluginService = new PluginService(managerConfiguration, dbConnectionProvider);
        processService = new ProcessService(managerConfiguration, dbConnectionProvider, pluginService);
        nodeService = new NodeService(managerConfiguration, dbConnectionProvider);
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
        createTables(dbConnectionProvider);
        loadTestData(dbConnectionProvider);
    }

    @Test
    public void testScheduleMainProcess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess(PROFILE1_ID, payload, "PePo");
        String processId = processService.scheduleMainProcess(scheduleMainProcess);
        ProcessInfo processInfo = processService.getProcess(processId);
        Assertions.assertNotNull(processInfo);
    }

    @Test
    public void testScheduleMainProcess_payloadError() {
        Map<String, String> payload = new HashMap<>();
        payload.put("surname", "Po");
        assertThrows(BusinessLogicException.class, () -> {
            ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess(PROFILE1_ID, payload, "PePo");
            processService.scheduleMainProcess(scheduleMainProcess);
        });
    }

    @Test
    public void testScheduleSubProcess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess(PROFILE1_ID, payload, "PePo");
        String mainProcessId = processService.scheduleMainProcess(scheduleMainProcess);
        ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess(PROFILE2_ID, payload);
        scheduleSubProcess.setBatchId(mainProcessId);
        String subProcessId = processService.scheduleSubProcess(scheduleSubProcess);
        ProcessInfo processInfo = processService.getProcess(subProcessId);
        Assertions.assertNotNull(processInfo);
    }

    @Test
    public void testScheduleSubProcess_batchError() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        assertThrows(BusinessLogicException.class, () -> {
            ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess(PROFILE2_ID, payload);
            scheduleSubProcess.setBatchId("wrongBatchId");
            String subProcessId = processService.scheduleSubProcess(scheduleSubProcess);
            ProcessInfo processInfo = processService.getProcess(subProcessId);
            Assertions.assertNotNull(processInfo);
        });
    }

    @Test
    public void testGetNextScheduledProcess() {
        Node node = new Node();
        node.setNodeId(NODE_WORKER1_ID);
        node.setType(NodeType.worker);
        Set<String> tags = new HashSet<>();
        tags.add(PROFILE1_ID);
        node.setTags(tags);
        nodeService.registerNode(node);

        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess(PROFILE1_ID, payload, "PePo");
        processService.scheduleMainProcess(scheduleMainProcess);
        ScheduledProcess nextScheduledProcess = processService.getNextScheduledProcess(NODE_WORKER1_ID);
        Assertions.assertNotNull(nextScheduledProcess);
        Assertions.assertEquals(2, nextScheduledProcess.getJvmArgs().size());
        ProcessInfo processInfo = processService.getProcess(nextScheduledProcess.getProcessId());
        Assertions.assertEquals(NODE_WORKER1_ID, processInfo.getWorkerId());
        Assertions.assertEquals(ProcessState.NOT_RUNNING, processInfo.getStatus());
    }

    @Test
    public void testGetNextScheduledProcess_none() {
        Node node = new Node();
        node.setNodeId(NODE_WORKER1_ID);
        node.setType(NodeType.worker);
        Set<String> tags = new HashSet<>();
        tags.add("anotherOne");
        node.setTags(tags);
        nodeService.registerNode(node);

        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess(PROFILE1_ID, payload, "PePo");
        processService.scheduleMainProcess(scheduleMainProcess);
        ScheduledProcess nextScheduledProcess = processService.getNextScheduledProcess(NODE_WORKER1_ID);
        Assertions.assertNull(nextScheduledProcess);
    }

}
