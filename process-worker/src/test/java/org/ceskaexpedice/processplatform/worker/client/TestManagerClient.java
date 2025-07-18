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
package org.ceskaexpedice.processplatform.worker.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.Constants;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static org.ceskaexpedice.processplatform.worker.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * TestManagerClient
 *
 * @author ppodsednik
 */
public class TestManagerClient {

    private HttpServer server;
    private WorkerConfiguration workerConfiguration;

    @BeforeEach
    public void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(WorkerTestEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(Constants.MANAGER_BASE_URI), rc);
        server.start();
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);
        String PROFILES = Constants.PLUGIN1_PROFILE_BIG + "," + Constants.PLUGIN1_PROFILE_SMALL;
        workerConfiguration.set(WorkerConfiguration.WORKER_PROFILES_KEY, PROFILES);
        workerConfiguration.set(WorkerConfiguration.WORKER_ID_KEY, "workerPepo");
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testGetNextScheduledProcess() {
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        ScheduledProcess nextProcess = managerClient.getNextScheduledProcess();
        System.out.println("entity");
    }

    @Test
    public void testRegisterPlugin() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);

        URL resource = getClass().getClassLoader().getResource("plugins");

        File pluginDir = new File(resource.getFile());
        List<PluginInfo> pluginInfos = PluginsLoader.load(pluginDir);
        assertEquals(2, pluginInfos.size());
        PluginInfo testPlugin1 = null;
        for (PluginInfo pluginInfo : pluginInfos) {
            if (pluginInfo.getPluginId().equals("testPlugin1")) {
                testPlugin1 = pluginInfo;
                break;
            }
        }

        workerConfiguration.set(WorkerConfiguration.WORKER_PROFILES_KEY, "testPlugin1-big,testPlugin1-small");
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.registerPlugin(testPlugin1);
        System.out.println("entity");
    }

    @Test
    public void testUpdateProcessPid() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessPid("uuid1", "333");
        System.out.println("entity");
    }

    @Test
    public void testUpdateProcessState() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessState("uuid1", ProcessState.FINISHED);
        System.out.println("entity");
    }

    @Test
    public void testUpdateProcessName() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessName("uuid1", "newName");
        System.out.println("entity");
    }

    @Test
    public void testScheduleSubProcess() throws JsonProcessingException {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Petr");
        payload.put("surname", "Harasil");

        ObjectMapper mapper = new ObjectMapper();
        // Build arbitrary context JSON
        ObjectNode context = mapper.createObjectNode();
        context.put("env", "production");
        context.put("retries", 3);
        ObjectNode metadata = mapper.createObjectNode();
        metadata.put("owner", "team-a");
        metadata.putArray("features").add("fast").add("secure");
        context.set("metadata", metadata);


        ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess(
                PLUGIN1_PROFILE_BIG,
                payload
        );
        scheduleSubProcess.setBatchId("batchId");

        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, Constants.MANAGER_BASE_URI);
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.scheduleSubProcess(scheduleSubProcess);
        System.out.println("entity");
    }

/*
    @Test
    public void testGetProcessByProcessId() {
        Response response = target("processes/by_process_id/pid")
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        //Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        System.out.println(entity);
    }

    @Test
    public void testScheduleProcess() {
        Response response = target("processes").request(MediaType.APPLICATION_JSON).post((Entity.entity(
                "{\"uf\": 99}", MediaType.APPLICATION_JSON_TYPE)));
        Assertions.assertEquals(200, response.getStatus());
        String responseBody = response.readEntity(String.class);
        System.out.println(responseBody);
    }
    */


}
