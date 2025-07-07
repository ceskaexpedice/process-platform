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
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.testutils.IntegrationTestsUtils;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TestPluginService_integration
 *
 * @author ppodsednik
 */
public class TestPluginService_integration {
    private static String PLUGIN1_ID = "testPlugin1";
    private static String PROFILE1_ID = "testPlugin1-big";

    private static Properties testsProperties;
    private static PluginService pluginService;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        DbConnectionProvider dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        pluginService = new PluginService(managerConfiguration, dbConnectionProvider);
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
    }

    // ------ plugins ----------

    @Test
    public void testGetPlugin() {
        PluginInfo pluginInfo = pluginService.getPlugin(PLUGIN1_ID);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(1, pluginInfo.getProfiles().size());
        pluginInfo = pluginService.getPlugin(PLUGIN1_ID + "notExists");
        Assertions.assertNull(pluginInfo);
    }

    @Test
    public void testGetPlugins() {
        List<PluginInfo> plugins = pluginService.getPlugins();
        Assertions.assertEquals(1, plugins.size());
    }

    @Test
    public void testValidatePayload() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        pluginService.validatePayload(PLUGIN1_ID, payload);

        payload.remove("surname");
        assertThrows(BusinessLogicException.class, () -> {
            pluginService.validatePayload(PLUGIN1_ID, payload);
        });

    }

    // ------ profiles ----------

    @Test
    public void testGetProfile() {
        PluginProfile profile = pluginService.getProfile(PROFILE1_ID);
        Assertions.assertNotNull(profile);
        profile = pluginService.getProfile(PROFILE1_ID + "notExists");
        Assertions.assertNull(profile);
    }

    @Test
    public void testGetProfiles() {
        List<PluginProfile> profiles = pluginService.getProfiles();
        Assertions.assertEquals(1, profiles.size());
    }

    @Test
    public void testGetPluginProfiles() {
        List<PluginProfile> profiles = pluginService.getProfiles(PLUGIN1_ID);
        Assertions.assertEquals(1, profiles.size());
    }

    @Test
    public void testCreateProfile() {
    }

    @Test
    public void testUpdateProfile() {
    }

    @Test
    public void testDeleteProfile() {
    }

}
