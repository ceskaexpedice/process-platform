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
package org.ceskaexpedice.processplatform.worker.api;

import org.ceskaexpedice.processplatform.worker.Constants;
import org.ceskaexpedice.processplatform.worker.api.service.WorkerAgentService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.mockito.Mockito.mock;

/**
 * TestAgentEndpoint
 * @author ppodsednik
 */
public class TestWorkerAgentEndpoint extends JerseyTest {

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        WorkerAgentService agentServiceMock = mock(WorkerAgentService.class);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new WorkerAgentEndpoint(agentServiceMock));
        return resourceConfig;
    }


    @Test
    public void testGetLogs() {
        Response response = target("agent/logs/" + Constants.PLUGIN1_PROCESS_ID)
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        //Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        System.out.println(entity);
    }

}
