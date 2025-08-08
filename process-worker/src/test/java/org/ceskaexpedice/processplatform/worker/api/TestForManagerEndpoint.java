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

import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.PLUGIN1_PROCESS_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * TestForManagerEndpoint
 * @author ppodsednik
 */
public class TestForManagerEndpoint extends JerseyTest {
    @Mock
    private ForManagerService forManagerServiceMock;

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new ForManagerEndpoint(forManagerServiceMock));
        return resourceConfig;
    }

    @Test
    public void testGetOutLog() throws IOException {
        InputStream is = new ByteArrayInputStream("test out content".getBytes(StandardCharsets.UTF_8));
        when(forManagerServiceMock.getProcessLog(eq(PLUGIN1_PROCESS_ID), eq(false))).thenReturn(is);

        Response response = target("manager/" + PLUGIN1_PROCESS_ID + "/log/out")
                .request().accept(MediaType.APPLICATION_OCTET_STREAM).get();
        Assertions.assertEquals(200, response.getStatus());
        InputStream responseStream = response.readEntity(InputStream.class);
        String resultOutLog = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertTrue(resultOutLog.contains("test out content"));
    }

    @Test
    public void testGetErrLog() throws IOException {
        InputStream is = new ByteArrayInputStream("test err content".getBytes(StandardCharsets.UTF_8));
        when(forManagerServiceMock.getProcessLog(eq(PLUGIN1_PROCESS_ID), eq(true))).thenReturn(is);

        Response response = target("manager/" + PLUGIN1_PROCESS_ID + "/log/err")
                .request().accept(MediaType.APPLICATION_OCTET_STREAM).get();
        Assertions.assertEquals(200, response.getStatus());
        InputStream responseStream = response.readEntity(InputStream.class);
        String resultErrLog = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertTrue(resultErrLog.contains("test err content"));
    }

}
