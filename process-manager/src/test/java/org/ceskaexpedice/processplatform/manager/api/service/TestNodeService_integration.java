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

/**
 * TestNodeService_integration
 *
 * @author ppodsednik
 */
public class TestNodeService_integration {
    private static Properties testsProperties;
    private static NodeService nodeService;
    private static DbConnectionProvider dbConnectionProvider;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        nodeService = new NodeService(managerConfiguration, dbConnectionProvider);
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
        createTables(dbConnectionProvider);
        loadTestData(dbConnectionProvider);
    }

    @Test
    public void testRegisterNode() {
        Node node = nodeService.getNode(NODE_WORKER1_ID);
        Assertions.assertNull(node);

        Node newNode = new Node();
        newNode.setNodeId(NODE_WORKER1_ID);
        newNode.setDescription("test node");
        newNode.setType(NodeType.worker);
        newNode.setUrl("http://localhost");
        Set<String> tags = new HashSet<>();
        tags.add(PROFILE1_ID);
        tags.add(PROFILE2_ID);
        tags.add(PROFILE_NEW_ID);
        newNode.setTags(tags);

        nodeService.registerNode(newNode);

        node = nodeService.getNode(NODE_WORKER1_ID);
        Assertions.assertNotNull(node);
        Assertions.assertEquals(3, node.getTags().size());
    }

}
