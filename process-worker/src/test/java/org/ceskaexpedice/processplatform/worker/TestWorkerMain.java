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
package org.ceskaexpedice.processplatform.worker;

import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.mockito.Mockito.mock;

/**
 * TestWorkerMain
 *
 * @author ppodsednik
 */
public class TestWorkerMain {

    @Test
    public void testMain() {
        Properties props = new Properties();
        props.put("pluginPath", "C:\\projects\\process-platform\\process-worker\\src\\test\\resources\\plugins");
        props.put("processApiPath", "c:\\Users\\petr\\.m2\\repository\\org\\ceskaexpedice\\process-api\\1.0-SNAPSHOT\\process-api-1.0-SNAPSHOT.jar");
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(props);

        WorkerMain workerMain = new WorkerMain();
        workerMain.initialize(workerConfiguration);
    }

}
