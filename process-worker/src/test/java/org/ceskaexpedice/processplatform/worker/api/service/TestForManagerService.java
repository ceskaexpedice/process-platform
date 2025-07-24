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
package org.ceskaexpedice.processplatform.worker.api.service;

import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.worker.client.ForWorkerTestEndpoint;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.executor.PluginJvmLauncher;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.PLUGIN_PATH_KEY;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.WORKER_ID_KEY;
import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.*;

/**
 * TestForManagerService
 *
 * @author ppodsednik
 */
public class TestForManagerService {
    private static final String OUT_LOG_PART = "TestPlugin1.createFullName:name-Petr,surname-Harasil";
    private static final String ERR_LOG_PART = "Connection refused";

    private HttpServer server;
    private WorkerConfiguration workerConfiguration;
    private ForManagerService forManagerService;


    @BeforeEach
    public void setUp() {
        URL resource = getClass().getClassLoader().getResource("plugins");
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(PLUGIN_PATH_KEY, resource.getFile());
        String starterClasspath = System.getProperty("java.class.path");
        workerConfiguration.set(WorkerConfiguration.STARTER_CLASSPATH_KEY, starterClasspath);
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, MANAGER_BASE_URI);
        workerConfiguration.set(WORKER_ID_KEY, "testWorker");

        forManagerService = new ForManagerService(workerConfiguration);

    }

    @AfterEach
    public void tearDown() {
        if(server != null){
            server.shutdownNow();
        }
    }

    @Test
    public void testGetOutLog() throws IOException {
        InputStream is = null;
        try{
            final ResourceConfig rc = new ResourceConfig(ForWorkerTestEndpoint.class);
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create(MANAGER_BASE_URI), rc);
            server.start();
            launchPlugin1();

            is = forManagerService.getProcessLog(PLUGIN1_PROCESS_ID, false);
            String outLog = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Assertions.assertTrue(outLog.contains(OUT_LOG_PART));

            is = forManagerService.getProcessLog(PLUGIN1_PROCESS_ID, true);
            String errLog = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Assertions.assertFalse(errLog.contains(ERR_LOG_PART));
        }finally {
            is.close();
        }
    }

    @Test
    public void testGetErrLog() throws IOException {
        InputStream is = null;
        try{
            server = null;
            launchPlugin1();

            is = forManagerService.getProcessLog(PLUGIN1_PROCESS_ID, true);
            String errLog = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Assertions.assertTrue(errLog.contains(ERR_LOG_PART));

            is = forManagerService.getProcessLog(PLUGIN1_PROCESS_ID, false);
            String outLog = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Assertions.assertFalse(outLog.contains(OUT_LOG_PART));
        }finally {
            is.close();
        }
    }

    private void launchPlugin1() {
        Map<String,String> payload = new HashMap<>();
        payload.put("name","Petr");
        payload.put("surname","Harasil");
        ScheduledProcess scheduledProcess = new ScheduledProcess(
                PLUGIN1_PROCESS_ID,
                PLUGIN1_PROFILE_BIG,
                PLUGIN1_ID,
                PLUGIN1_MAIN_CLASS,
                payload,
                new ArrayList<>(),
                "batchId",
                "ownerId");
        int exitCode = PluginJvmLauncher.launchJvm(scheduledProcess, workerConfiguration);
        Assertions.assertEquals(0, exitCode);
    }
}
