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
package org.ceskaexpedice.processplatform.manager.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TestWorkerEndpoint
 *
 * @author ppodsednik
 */
public class TestWorkerEndpoint extends JerseyTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private PluginService pluginServiceMock;
    @Mock
    private ProcessService processServiceMock;
    @Mock
    private NodeService nodeServiceMock;

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new WorkerEndpoint(pluginServiceMock, processServiceMock, nodeServiceMock));
        resourceConfig.register(GlobalExceptionMapper.class);
        return resourceConfig;
    }

    @Test
    public void testRegisterNode() throws JsonProcessingException {
        Node node = new Node();
        node.setNodeId(NODE_WORKER_ID);
        String json = mapper.writeValueAsString(node);
        Response response = target("worker/register_node").request(MediaType.APPLICATION_JSON).post((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(nodeServiceMock, times(1)).registerNode(any());
    }

    @Test
    public void testRegisterPlugin() throws JsonProcessingException {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(PLUGIN1_ID);
        String json = mapper.writeValueAsString(pluginInfo);
        Response response = target("worker/register_plugin").request(MediaType.APPLICATION_JSON).post((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(pluginServiceMock, times(1)).registerPlugin(any());
    }

    @Test
    public void testNextProcess() {
        Response response = target("agent/next-process").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        ScheduledProcess entity = response.readEntity(ScheduledProcess.class);
        System.out.println(entity);
    }

    @Test
    public void testScheduleSubProcess() {
    }

    @Test
    public void testUpdateProcessPid() throws JsonProcessingException {
    }

    @Test
    public void testUpdateProcessState() throws JsonProcessingException {
    }

    @Test
    public void testUpdateProcessName() throws JsonProcessingException {
    }


}
