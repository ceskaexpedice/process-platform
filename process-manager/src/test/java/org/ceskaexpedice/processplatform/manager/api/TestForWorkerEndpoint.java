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
import org.ceskaexpedice.processplatform.common.GlobalExceptionMapper;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
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

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TestForWorkerEndpoint
 *
 * @author ppodsednik
 */
public class TestForWorkerEndpoint extends JerseyTest {
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
        resourceConfig.register(new ForWorkerEndpoint(pluginServiceMock, processServiceMock, nodeServiceMock));
        resourceConfig.register(GlobalExceptionMapper.class);
        return resourceConfig;
    }

    @Test
    public void testRegisterNode() throws JsonProcessingException {
        Node node = new Node();
        node.setNodeId(NODE_WORKER1_ID);
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
    public void testGetNextProcess() {
        ScheduledProcess retVal = new ScheduledProcess();
        retVal.setProcessId(PROCESS1_ID);
        when(processServiceMock.getNextScheduledProcess(eq(NODE_WORKER1_ID))).thenReturn(retVal);

        Response response = target("worker/next_process/" + NODE_WORKER1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        ScheduledProcess entity = response.readEntity(ScheduledProcess.class);
        Assertions.assertEquals(entity.getProcessId(), retVal.getProcessId());

        response = target("worker/next_process/" + NODE_WORKER2_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(404, response.getStatus());
        String entity1 = response.readEntity(String.class);
        verify(processServiceMock, times(2)).getNextScheduledProcess(any());
    }

    @Test
    public void testScheduleSubProcess() throws JsonProcessingException {
        ScheduleSubProcess scheduleSubProcess = new ScheduleSubProcess();
        scheduleSubProcess.setProfileId(PROFILE1_ID);
        String json = mapper.writeValueAsString(scheduleSubProcess);

        Response response = target("worker/schedule_sub_process").request(MediaType.APPLICATION_JSON).post((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));

        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(processServiceMock, times(1)).scheduleSubProcess(any(ScheduleSubProcess.class));
    }

    @Test
    public void testUpdateProcessPid() {
        String json = "{}";
        Response response = target("worker/pid/" + PROCESS1_ID).queryParam("pid",123).request(MediaType.APPLICATION_JSON).put((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(processServiceMock, times(1)).updatePid(eq(PROCESS1_ID), eq(123));
    }

    @Test
    public void testUpdateProcessState() {
        String json = "{}";
        Response response = target("worker/state/" + PROCESS1_ID).queryParam("state",ProcessState.FINISHED).request(MediaType.APPLICATION_JSON).put((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(processServiceMock, times(1)).updateState(eq(PROCESS1_ID), eq(ProcessState.FINISHED));
    }

    @Test
    public void testUpdateProcessDescription() {
        String json = "{}";
        Response response = target("worker/description/" + PROCESS1_ID).queryParam("description","new name").request(MediaType.APPLICATION_JSON).put((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(processServiceMock, times(1)).updateDescription(eq(PROCESS1_ID), eq("new name"));
    }


}
