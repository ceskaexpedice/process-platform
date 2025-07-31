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
package org.ceskaexpedice.testutils;

import org.apache.commons.io.IOUtils;
import org.ceskaexpedice.processplatform.manager.api.service.TestPluginService_integration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.DbUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public final class ManagerTestsUtils {
    public static final String WORKER_BASE_URI = "http://localhost:9998/process-worker/api/";

    public static final String NODE_WORKER1_ID = "worker1";
    public static final String NODE_WORKER2_ID = "worker2";

    public static final String PROCESS1_ID = "process1";
    public static final String PROCESS2_ID = "process2";

    public static final String PLUGIN1_ID = "testPlugin1";
    public static final String PLUGIN2_ID = "testPlugin2";
    public static final String PLUGIN3_ID = "testPlugin3";
    public static final String PLUGIN_NEW_ID = "newPluginId";

    public static final String PROFILE1_ID = "testPlugin1-big";
    public static final String PROFILE2_ID = "testPlugin1-small";
    public static final String PROFILE_NEW_ID = "newProfileId";

    private ManagerTestsUtils() {}

    public static void createTables(DbConnectionProvider dbConnectionProvider) {
        Connection connection = dbConnectionProvider.get();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_process");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_profile");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_plugin");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS pcp_node");
            preparedStatement.executeUpdate();

            InputStream is = TestPluginService_integration.class.getClassLoader().getResourceAsStream("pcp_plugin.sql");
            String sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            is = TestPluginService_integration.class.getClassLoader().getResourceAsStream("pcp_profile.sql");
            sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            is = TestPluginService_integration.class.getClassLoader().getResourceAsStream("pcp_process.sql");
            sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            is = TestPluginService_integration.class.getClassLoader().getResourceAsStream("pcp_node.sql");
            sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.tryClose(connection);
        }
    }

    public static void loadTestData(DbConnectionProvider dbConnectionProvider) {
        Connection connection = dbConnectionProvider.get();
        try (InputStream is = TestPluginService_integration.class.getClassLoader().getResourceAsStream("test_data.sql")) {
            String sql = IOUtils.toString(is, StandardCharsets.UTF_8);
            // Split by semicolon if multiple statements (not perfect but works for simple scripts)
            String[] statements = sql.split(";");
            for (String raw : statements) {
                String trimmed = raw.trim();
                if (!trimmed.isEmpty()) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.tryClose(connection);
        }
    }

}
