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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.ceskaexpedice.testutils.ManagerTestsUtils.PLUGIN1_ID;
import static org.ceskaexpedice.testutils.ManagerTestsUtils.PROFILE1_ID;
import static org.ceskaexpedice.testutils.ManagerTestsUtils.PROFILE2_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestNodeEndpoint
 *
 * @author ppodsednik
 */
public class TestNodeEndpoint extends JerseyTest {
    @Mock
    private NodeService nodeServiceMock;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new NodeEndpoint(nodeServiceMock));
        return resourceConfig;
    }

    @Test
    public void testGetNode() throws JsonProcessingException {
        Node retVal = new Node();
        retVal.setNodeId(NODE_WORKER1_ID);
        when(nodeServiceMock.getNode(eq(NODE_WORKER1_ID))).thenReturn(retVal);

        Response response = target("node/" + NODE_WORKER1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        Node node = mapper.readValue(json,Node.class);
        Assertions.assertEquals(NODE_WORKER1_ID, node.getNodeId());

        response = target("node/" + NODE_WORKER2_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(404, response.getStatus());

        verify(nodeServiceMock, times(2)).getNode(any());
    }

    @Test
    public void testGetNodes() throws JsonProcessingException {
        List<Node> retVal = new ArrayList<>();
        Node node1 = new Node();
        node1.setNodeId(NODE_WORKER1_ID);
        Node node2 = new Node();
        node2.setNodeId(NODE_WORKER2_ID);
        retVal.add(node1);
        retVal.add(node2);
        when(nodeServiceMock.getNodes()).thenReturn(retVal);

        Response response = target("node/").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        List<Node> nodes = mapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertEquals(2, nodes.size());
    }

}
