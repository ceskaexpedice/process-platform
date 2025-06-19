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

import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.plugin.loader.PluginsLoader;
import org.ceskaexpedice.processplatform.worker.utils.Utils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * TestRest Description
 *
 * @author ppodsednik
 */
public class TestManagerClient {

    public static final String BASE_URI = "http://localhost:9998/process-manager/api/";
    private HttpServer server;


    @BeforeEach
    public void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(ManagerAgentEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        server.start();
    }

    /*
    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ProcessService processServiceMock = mock(ProcessService.class);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new ProcessEndpoint(processServiceMock));
        return resourceConfig;
    }*/

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testGetNextProcess() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, BASE_URI);
        workerConfiguration.set(WorkerConfiguration.WORKER_TAGS_KEY, "testPlugin1-big,testPlugin1-small");
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        ScheduledProcessTO nextProcess = managerClient.getNextProcess();
        System.out.println("entity");
    }

    @Test
    public void testRegisterPlugin() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, BASE_URI);

        URL resource = getClass().getClassLoader().getResource("plugins");

        File pluginDir = new File(resource.getFile());
        List<PluginInfoTO> pluginInfos = PluginsLoader.load(pluginDir);
        assertEquals(2, pluginInfos.size());
        PluginInfoTO testPlugin1 = null;
        for (PluginInfoTO pluginInfo : pluginInfos) {
            if(pluginInfo.getPluginId().equals("testPlugin1")){
                testPlugin1 = pluginInfo;
                break;
            }
        }

        workerConfiguration.set(WorkerConfiguration.WORKER_TAGS_KEY, "testPlugin1-big,testPlugin1-small");
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.registerPlugin(testPlugin1);
        System.out.println("entity");
    }

    @Test
    public void testUpdateProcessPid() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(WorkerConfiguration.MANAGER_BASE_URL_KEY, BASE_URI);
        ManagerClient managerClient = new ManagerClient(workerConfiguration);
        managerClient.updateProcessPid("uuid1", "333");
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
