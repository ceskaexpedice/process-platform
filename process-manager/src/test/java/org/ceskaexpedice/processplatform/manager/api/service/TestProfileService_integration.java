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
public class TestProfileService_integration {
    private static Properties testsProperties;
    private static ProfileService profileService;
    private static DbConnectionProvider dbConnectionProvider;

    @BeforeAll
    static void beforeAll() {
        testsProperties = IntegrationTestsUtils.loadProperties();
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(testsProperties);
        dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        profileService = new ProfileService(managerConfiguration, dbConnectionProvider);
    }

    @BeforeEach
    void beforeEach() {
        IntegrationTestsUtils.checkIntegrationTestsIgnored(testsProperties);
        createTables(dbConnectionProvider);
        loadTestData(dbConnectionProvider);
    }

    @Test
    public void testGetProfile() {
        PluginProfile profile = profileService.getProfile(PROFILE1_ID);
        Assertions.assertNotNull(profile);
        profile = profileService.getProfile(PROFILE1_ID + "notExists");
        Assertions.assertNull(profile);
    }

    @Test
    public void testGetProfiles() {
        List<PluginProfile> profiles = profileService.getProfiles();
        Assertions.assertEquals(3, profiles.size());
    }

    @Test
    public void testGetPluginProfiles() {
        List<PluginProfile> profiles = profileService.getProfiles(PLUGIN1_ID);
        Assertions.assertEquals(2, profiles.size());
    }

    @Test
    public void testUpdateProfile() {
        PluginProfile profile = profileService.getProfile(PROFILE1_ID);
        Assertions.assertEquals(2, profile.getJvmArgs().size());
        List<String> args = List.of("-Xmx32g", "a", "b", "c");
        profile.setJvmArgs(args);
        profileService.updateProfile(profile);
        profile = profileService.getProfile(PROFILE1_ID);
        Assertions.assertEquals(4, profile.getJvmArgs().size());
    }

}
