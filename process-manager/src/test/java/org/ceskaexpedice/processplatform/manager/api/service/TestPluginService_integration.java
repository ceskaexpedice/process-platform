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
import org.ceskaexpedice.processplatform.common.model.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.model.PayloadFieldType;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
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
 * TestPluginService_integration
 *
 * @author ppodsednik
 */
public class TestPluginService_integration {
    private static Properties testsProperties;
    private static PluginService pluginService;
    private static DbConnectionProvider dbConnectionProvider;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        pluginService = new PluginService(managerConfiguration, dbConnectionProvider);
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
        createTables(dbConnectionProvider);
        loadTestData(dbConnectionProvider);
    }

    @Test
    public void testGetPlugin_noProfiles_noScheduledProfilesRecursive() {
        PluginInfo pluginInfo = pluginService.getPlugin(PLUGIN1_ID, false, false);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(0, pluginInfo.getProfiles().size());
        Assertions.assertEquals(2, pluginInfo.getPayloadFieldSpecMap().size());
        Assertions.assertEquals(1, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN2_ID, false, false);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(0, pluginInfo.getProfiles().size());
        Assertions.assertNull(pluginInfo.getPayloadFieldSpecMap());
        Assertions.assertEquals(1, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN1_ID + "notExists", false, false);
        Assertions.assertNull(pluginInfo);
    }

    @Test
    public void testGetPlugin_withProfiles_noScheduledProfilesRecursive() {
        PluginInfo pluginInfo = pluginService.getPlugin(PLUGIN1_ID, true, false);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(2, pluginInfo.getProfiles().size());
        Assertions.assertEquals(2, pluginInfo.getPayloadFieldSpecMap().size());
        Assertions.assertEquals(1, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN2_ID, true, false);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(1, pluginInfo.getProfiles().size());
        Assertions.assertNull(pluginInfo.getPayloadFieldSpecMap());
        Assertions.assertEquals(1, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN1_ID + "notExists", true, false);
        Assertions.assertNull(pluginInfo);
    }

    @Test
    public void testGetPlugin_withProfiles_withScheduledProfilesRecursive() {
        PluginInfo pluginInfo = pluginService.getPlugin(PLUGIN1_ID, true, true);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(2, pluginInfo.getProfiles().size());
        Assertions.assertEquals(2, pluginInfo.getPayloadFieldSpecMap().size());
        Assertions.assertEquals(2, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN2_ID, true, true);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(1, pluginInfo.getProfiles().size());
        Assertions.assertNull(pluginInfo.getPayloadFieldSpecMap());
        Assertions.assertEquals(1, pluginInfo.getScheduledProfiles().size());

        pluginInfo = pluginService.getPlugin(PLUGIN1_ID + "notExists", true, true);
        Assertions.assertNull(pluginInfo);
    }

    @Test
    public void testGetPlugins() {
        List<PluginInfo> plugins = pluginService.getPlugins();
        Assertions.assertEquals(3, plugins.size());
    }

    @Test
    public void testValidatePayload() {
        // check required
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        pluginService.validatePayload(PLUGIN1_ID, payload);
        payload.remove("surname");
        assertThrows(BusinessLogicException.class, () -> {
            pluginService.validatePayload(PLUGIN1_ID, payload);
        });

        // check boolean
        payload.clear();
        payload.put("booleanField", "true");
        pluginService.validatePayload(PLUGIN3_ID, payload);
        payload.put("booleanField", "notBoolean");
        assertThrows(BusinessLogicException.class, () -> {
            pluginService.validatePayload(PLUGIN3_ID, payload);
        });

        // check number
        payload.clear();
        payload.put("numberField", "123");
        pluginService.validatePayload(PLUGIN3_ID, payload);
        payload.put("numberField", "12notNumber");
        assertThrows(BusinessLogicException.class, () -> {
            pluginService.validatePayload(PLUGIN3_ID, payload);
        });

        // check date
        payload.clear();
        payload.put("dateField", "1960-11-12");
        pluginService.validatePayload(PLUGIN3_ID, payload);
        payload.put("dateField", "1960-99-12");
        assertThrows(BusinessLogicException.class, () -> {
            pluginService.validatePayload(PLUGIN3_ID, payload);
        });

    }

    @Test
    public void testRegisterPlugin() {
        PluginInfo pluginInfo = pluginService.getPlugin(PLUGIN_NEW_ID, false, false);
        Assertions.assertNull(pluginInfo);

        Map<String, PayloadFieldSpec> payloadFieldSpecMap = new HashMap<>();
        payloadFieldSpecMap.put("name", new PayloadFieldSpec(PayloadFieldType.STRING, true));
        Set<String> scheduledProfiles = new HashSet<>();
        scheduledProfiles.add(PROFILE_NEW_ID);

        List<PluginProfile> profiles = new ArrayList<>();
        PluginProfile pluginProfile = new PluginProfile(PROFILE_NEW_ID, "Test", PLUGIN_NEW_ID, new ArrayList<>());
        profiles.add(pluginProfile);

        PluginInfo pluginInfoNew = new PluginInfo(PLUGIN_NEW_ID, "Test plugin New", "com.mainClass",
                payloadFieldSpecMap, scheduledProfiles, profiles);

        pluginService.registerPlugin(pluginInfoNew);

        pluginInfo = pluginService.getPlugin(PLUGIN_NEW_ID, true, false);
        Assertions.assertNotNull(pluginInfo);
        Assertions.assertEquals(1, pluginInfo.getProfiles().size());
    }

}
