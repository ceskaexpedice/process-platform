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
package org.ceskaexpedice.processplatform.manager.api.db;

import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.NodeType;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.DbUtils;
import org.ceskaexpedice.testutils.IntegrationTestsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;

/**
 * TestDbUtils_integration
 *
 * @author ppodsednik
 */
public class TestDbUtils_integration {
    private static Properties testsProperties;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
    }

    @Test
    public void testTableExists() throws SQLException {
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        DbConnectionProvider dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        createTables(dbConnectionProvider);

        try(Connection connection = dbConnectionProvider.get()){
            boolean tableExists = DbUtils.tableExists(connection, ManagerConfiguration.PLUGIN_TABLE);
            Assertions.assertTrue(tableExists);
            dropTables(connection);
            tableExists = DbUtils.tableExists(connection, ManagerConfiguration.PLUGIN_TABLE);
            Assertions.assertFalse(tableExists);
        }
    }

    private static void dropTables(Connection connection) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_process");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_profile");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_plugin");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_node");
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.tryClose(preparedStatement);
        }
    }


}
