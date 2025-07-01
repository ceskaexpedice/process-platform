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

import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.manager.db.DatabaseProcessManager;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.common.entity.ProcessState;
import org.ceskaexpedice.processplatform.manager.db.LRProcess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

/**
 * TestWorkerMain
 *
 * @author ppodsednik
 */
public class TestToDelete {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testDb() {
        DbConnectionProvider dbConnectionProvider = new DbConnectionProvider(null);
        Connection connection = dbConnectionProvider.get();
        DatabaseProcessManager databaseProcessManager = new DatabaseProcessManager(dbConnectionProvider);
        List<PluginProfile> profiles = databaseProcessManager.getProfiles("testPlugin1-big");
        List<PluginInfo> plugins = databaseProcessManager.getPlugins("testPlugin1", profiles);
        System.out.println("Profiles: " + profiles);
    }

}
