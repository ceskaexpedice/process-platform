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
import org.ceskaexpedice.processplatform.common.model.Batch;
import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.common.model.ScheduleMainProcess;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TestProcessEndpoint
 *
 * @author ppodsednik
 */
public class TestProcessEndpoint extends JerseyTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private NodeService nodeServiceMock;
    @Mock
    private ProcessService processServiceMock;

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new ProcessEndpoint(processServiceMock, nodeServiceMock));
        resourceConfig.register(GlobalExceptionMapper.class);
        return resourceConfig;
    }

    @Test
    public void testScheduleMainProcess() throws JsonProcessingException {
        when(processServiceMock.scheduleMainProcess(any())).thenReturn(PROCESS1_ID);
        ScheduleMainProcess scheduleMainProcess = new ScheduleMainProcess();
        scheduleMainProcess.setProfileId(PROFILE1_ID);
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Pe");
        payload.put("surname", "Po");
        scheduleMainProcess.setPayload(payload);
        scheduleMainProcess.setOwnerId("PePo");
        String json = mapper.writeValueAsString(scheduleMainProcess);

        Response response = target("process/").request(MediaType.APPLICATION_JSON).post((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));

        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(processServiceMock, times(1)).scheduleMainProcess(any(ScheduleMainProcess.class));
    }

    @Test
    public void testGetProcess() throws JsonProcessingException {
        ProcessInfo retVal = new ProcessInfo();
        retVal.setProcessId(PROCESS1_ID);
        retVal.setPlanned(new Date());
        when(processServiceMock.getProcess(eq(PROCESS1_ID))).thenReturn(retVal);

        Response response = target("process/" + PROCESS1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        ProcessInfo processInfo = mapper.readValue(json,ProcessInfo.class);
        Assertions.assertEquals(PROCESS1_ID, processInfo.getProcessId());

        response = target("process/" + PROCESS2_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(404, response.getStatus());

        verify(processServiceMock, times(2)).getProcess(any());
    }

    @Test
    public void testGetBatches() {
        List<Batch> batches = new ArrayList<>();
        Batch batch1 = new Batch();
        batch1.setMainProcessId(PROCESS1_ID);
        batch1.setStatus(ProcessState.WARNING);
        ProcessInfo processInfo1 = new ProcessInfo();
        processInfo1.setPlanned(new Date());
        processInfo1.setProcessId(PROCESS1_ID);
        processInfo1.setBatchId(PROCESS1_ID);
        processInfo1.setStatus(ProcessState.FINISHED);
        batch1.getProcesses().add(processInfo1);
        ProcessInfo processInfo2 = new ProcessInfo();
        processInfo2.setProcessId(PROCESS2_ID);
        processInfo2.setBatchId(PROCESS1_ID);
        processInfo2.setStatus(ProcessState.WARNING);
        batch1.getProcesses().add(processInfo2);
        batches.add(batch1);

        Batch batch2 = new Batch();
        batch2.setMainProcessId(PROCESS3_ID);
        batch1.setStatus(ProcessState.PLANNED);
        ProcessInfo processInfo21 = new ProcessInfo();
        processInfo21.setProcessId(PROCESS3_ID);
        processInfo21.setBatchId(PROCESS3_ID);
        processInfo21.setStatus(ProcessState.PLANNED);
        batch2.getProcesses().add(processInfo21);
        batches.add(batch2);

        when(processServiceMock.countBatchHeaders(any())).thenReturn(2);
        when(processServiceMock.getBatches(any(), eq(0), eq(10))).thenReturn(batches);

        Response response = target("process/batch").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertEquals(2, jsonObject.getJSONArray("batches").length());

        verify(processServiceMock, times(1)).countBatchHeaders(any());
        verify(processServiceMock, times(1)).getBatches(any(), eq(0), eq(10));
    }

    @Test
    public void testDeleteBatch() {
        when(processServiceMock.deleteBatch(eq(PROCESS1_ID))).thenReturn(3);

        Response response = target("process/batch/" + PROCESS1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).delete();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertEquals(3, jsonObject.get("deleted"));
        Assertions.assertEquals(PROCESS1_ID, jsonObject.get("mainProcessId"));

        verify(processServiceMock, times(1)).deleteBatch(eq(PROCESS1_ID));
    }

    @Test
    public void testKillBatch() {
        when(processServiceMock.killBatch(eq(PROCESS1_ID))).thenReturn(5);

        Response response = target("process/batch/" + PROCESS1_ID + "/execution").request().accept(MediaType.APPLICATION_JSON_TYPE).delete();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertEquals(5, jsonObject.get("killed"));
        Assertions.assertEquals(PROCESS1_ID, jsonObject.get("mainProcessId"));

        verify(processServiceMock, times(1)).killBatch(eq(PROCESS1_ID));
    }

    @Test
    public void testGetOwners() {
        List<String> owners = new ArrayList<>();
        owners.add("PaSt");
        owners.add("PePo");
        when(processServiceMock.getOwners()).thenReturn(owners);

        Response response = target("process/owner").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        Assertions.assertEquals(2, jsonObject.getJSONArray("owners").length());

        verify(processServiceMock, times(1)).getOwners();
    }

    @Test
    public void testGetOutLog() throws IOException {
        InputStream is = new ByteArrayInputStream("test out content".getBytes(StandardCharsets.UTF_8));
        when(processServiceMock.getProcessLog(eq(PROCESS1_ID), eq(false))).thenReturn(is);

        Response response = target("process/" + PROCESS1_ID + "/log/out")
                .request().accept(MediaType.APPLICATION_OCTET_STREAM).get();
        Assertions.assertEquals(200, response.getStatus());
        InputStream responseStream = response.readEntity(InputStream.class);
        String resultOutLog = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertTrue(resultOutLog.contains("test out content"));
    }

    @Test
    public void testGetErrLog() throws IOException {
        InputStream is = new ByteArrayInputStream("test err content".getBytes(StandardCharsets.UTF_8));
        when(processServiceMock.getProcessLog(eq(PROCESS1_ID), eq(true))).thenReturn(is);

        Response response = target("process/" + PROCESS1_ID + "/log/err")
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
        JSONObject result = new JSONObject();
        result.put("totalSize", logSize);
        JSONArray linesJson = new JSONArray();
        for (String line : lines) {
            linesJson.put(line);
        }
        result.put("lines", linesJson);

        when(processServiceMock.getProcessLogLines(eq(PROCESS1_ID), eq("0"), eq("50"), eq(false))).thenReturn(result);

        Response response = target("process/" + PROCESS1_ID + "/log/out/lines").queryParam("processId", PROCESS1_ID).
                queryParam("offset", "0").queryParam("limit", "50")
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String responseSt = response.readEntity(String.class);
        JSONObject responseJson = new JSONObject(responseSt);
        Assertions.assertEquals(2, responseJson.getJSONArray("lines").length());
    }


}
