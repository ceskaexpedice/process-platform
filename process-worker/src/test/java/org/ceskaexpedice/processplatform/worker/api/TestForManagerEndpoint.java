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

import org.ceskaexpedice.processplatform.common.model.ScheduledProcess;
import org.ceskaexpedice.processplatform.common.model.WorkerInfo;
import org.ceskaexpedice.processplatform.common.model.WorkerState;
import org.ceskaexpedice.processplatform.worker.api.service.ForManagerService;
import org.ceskaexpedice.processplatform.worker.utils.WorkerInfoMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.PLUGIN1_PROCESS_ID;
import static org.ceskaexpedice.processplatform.worker.testutils.WorkerTestsUtils.PROCESS_PID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    public void testGetInfo() {
        Response response = target("manager/info").request().get();
        Assertions.assertEquals(200, response.getStatus());
        String responseSt = response.readEntity(String.class);
        JSONObject responseJson = new JSONObject(responseSt);
        String state = responseJson.getString("state");
        Assertions.assertEquals(WorkerState.IDLE, WorkerState.valueOf(state));
    }

    /* TODO
    @Test
    public void testUff() {
        ScheduledProcess scheduledProcess = new ScheduledProcess();
        scheduledProcess.setProcessId(PROCESS_PID);
        Map<String,String> payload = new HashMap<>();
        payload.put("name", "pe");
        payload.put("surname", "po");
        scheduledProcess.setPayload(payload);
        scheduledProcess.setMainClass("com.kramerius.Import");
        scheduledProcess.setProfileId("import");
        scheduledProcess.setPluginId("import");
        List<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xms128m");
        scheduledProcess.setJvmArgs(jvmArgs);

        WorkerInfo workerInfo = new WorkerInfo();
        workerInfo.setLastExitCode(0);
        workerInfo.setState(WorkerState.RUNNING);
        //workerInfo.setLastProcess();
        workerInfo.setJvmAlive(true);
        workerInfo.setCurrentProcess(scheduledProcess);
        workerInfo.setCurrentPid(123L);

        JSONObject jsonObject = WorkerInfoMapper.mapToJson(workerInfo);
        System.out.println(jsonObject.toString());
        //WorkerInfo workerInfo1 = WorkerInfoMapper.mapFromJson(jsonObject);
        System.out.println();
    }

     */

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

    @Test
    public void testGetOutLogLines() {
        long logSize = 21;
        List<String> lines = new ArrayList<>();
        lines.add("first line");
        lines.add("second line");

        when(forManagerServiceMock.getProcessLogSize(eq(PLUGIN1_PROCESS_ID), eq(false))).thenReturn(logSize);
        when(forManagerServiceMock.getLogOffset(any())).thenReturn(0L);
        when(forManagerServiceMock.getLogLimit(any())).thenReturn(logSize);
        when(forManagerServiceMock.getProcessLogLines(eq(PLUGIN1_PROCESS_ID), eq(false), eq(0L), eq(logSize))).thenReturn(lines);

        Response response = target("manager/" + PLUGIN1_PROCESS_ID + "/log/out/lines")
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String responseSt = response.readEntity(String.class);
        JSONObject responseJson = new JSONObject(responseSt);
        Assertions.assertEquals(2, responseJson.getJSONArray("lines").length());
    }

    @Test
    public void testGetErrLogLines() {
        long logSize = 21;
        List<String> lines = new ArrayList<>();
        lines.add("first line");
        lines.add("second line");

        when(forManagerServiceMock.getProcessLogSize(eq(PLUGIN1_PROCESS_ID), eq(true))).thenReturn(logSize);
        when(forManagerServiceMock.getLogOffset(any())).thenReturn(0L);
        when(forManagerServiceMock.getLogLimit(any())).thenReturn(logSize);
        when(forManagerServiceMock.getProcessLogLines(eq(PLUGIN1_PROCESS_ID), eq(true), eq(0L), eq(logSize))).thenReturn(lines);

        Response response = target("manager/" + PLUGIN1_PROCESS_ID + "/log/err/lines")
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String responseSt = response.readEntity(String.class);
        JSONObject responseJson = new JSONObject(responseSt);
        Assertions.assertEquals(2, responseJson.getJSONArray("lines").length());
    }

    @Test
    public void testDeleteProcessWorkingDirectory() {
        Response response = target("manager/" + PLUGIN1_PROCESS_ID + "/directory")
                .request().accept(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertTrue(((String)jsonObject.get("message")).contains(PLUGIN1_PROCESS_ID));
        verify(forManagerServiceMock, times(1)).deleteWorkingDir(eq(PLUGIN1_PROCESS_ID));

    }

    @Test
    public void testKillProcessJvm() {
        when(forManagerServiceMock.killProcessJvm(eq(PROCESS_PID))).thenReturn(true);

        Response response = target("manager/" + PROCESS_PID + "/kill")
                .request().accept(MediaType.APPLICATION_JSON).delete();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertTrue(((String)jsonObject.get("message")).contains(PROCESS_PID));
    }

}
