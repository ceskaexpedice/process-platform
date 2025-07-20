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

import org.ceskaexpedice.processplatform.worker.client.ForWorkerTestEndpoint;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.client.ManagerClientFactory;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils;
import org.ceskaexpedice.processplatform.worker.utils.Utils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.*;
import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.MANAGER_BASE_URI;

/**
 * TestWorkerMain
 *
 * @author ppodsednik
 */
public class TestWorkerLoop {

    private WorkerConfiguration workerConfiguration;
    private HttpServer server;

    @BeforeEach
    public void setUp() throws Exception {
        URL resource = getClass().getClassLoader().getResource("plugins");
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(PLUGIN_PATH_KEY, resource.getFile());
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, MANAGER_BASE_URI);
        String TAGS = WorkerTestsUtils.PLUGIN1_PROFILE_BIG + "," + WorkerTestsUtils.PLUGIN1_PROFILE_SMALL;
        workerConfiguration.set(WorkerConfiguration.WORKER_PROFILES_KEY, TAGS);
        workerConfiguration.set(WORKER_LOOP_SLEEP_SEC_KEY,"10");
        workerConfiguration.set(WORKER_ID_KEY, "testWorker");

        final ResourceConfig rc = new ResourceConfig(ForWorkerTestEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(MANAGER_BASE_URI), rc);
        server.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testWorkerLoop() {
        ManagerClient managerClient = ManagerClientFactory.createManagerClient(workerConfiguration);
        WorkerLoop workerLoop = new WorkerLoop(workerConfiguration, managerClient);
        workerLoop.start();
        System.out.println("First sleep 5 sec to wait until 2 processes finishes");
        Utils.sleep(5000);
        workerLoop.stop();
        System.out.println("Stopping workerLoopThread. Second sleep 15 sec");
        Utils.sleep(15000);
    }

}
