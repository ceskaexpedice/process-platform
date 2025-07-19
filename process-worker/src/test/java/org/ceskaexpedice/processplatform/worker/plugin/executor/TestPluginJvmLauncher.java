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
package org.ceskaexpedice.processplatform.worker.plugin.executor;

import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.client.ForWorkerTestEndpoint;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.*;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.PLUGIN_PATH_KEY;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.WORKER_ID_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TestPluginJvmLauncher
 *
 * @author ppodsednik
 */
public class TestPluginJvmLauncher {

    private HttpServer server;
    private WorkerConfiguration workerConfiguration;

    @BeforeEach
    public void setUp() throws Exception {
        URL resource = getClass().getClassLoader().getResource("plugins");
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(PLUGIN_PATH_KEY, resource.getFile());
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, MANAGER_BASE_URI);
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
    public void testLaunchPlugin1() {
        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xmx1024m");
        jvmArgs.add("-Xms256m");
        //jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

        Map<String,String> payload = new HashMap<>();
        payload.put("name","Petr");
        payload.put("surname","Harasil");

        ScheduledProcess scheduledProcess = new ScheduledProcess(
                PLUGIN1_PROCESS_ID,
                PLUGIN1_PROFILE_BIG,
                PLUGIN1_ID,
                PLUGIN1_MAIN_CLASS,
                payload,
                jvmArgs,
                "batchId",
                "ownerId");

        int exitCode = PluginJvmLauncher.launchJvm(scheduledProcess, workerConfiguration);
        Assertions.assertEquals(0, exitCode);
    }

}
