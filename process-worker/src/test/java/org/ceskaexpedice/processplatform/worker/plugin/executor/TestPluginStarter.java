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
package org.ceskaexpedice.processplatform.worker.plugin.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.api.context.PluginContext;
import org.ceskaexpedice.processplatform.worker.client.ManagerClient;
import org.ceskaexpedice.processplatform.worker.client.ManagerClientFactory;
import org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.ceskaexpedice.processplatform.worker.config.ProcessConfiguration.PROCESS_CONFIG_BASE64_KEY;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.PLUGIN_PATH_KEY;
import static org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration.WORKER_CONFIG_BASE64_KEY;
import static org.ceskaexpedice.processplatform.worker.utils.ProcessDirUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestPluginStarter
 *
 * @author ppodsednik
 */
public class TestPluginStarter {

    private WorkerConfiguration workerConfiguration;

    @BeforeEach
    public void setUp() throws Exception {
        URL resource = getClass().getClassLoader().getResource("plugins");
        workerConfiguration = new WorkerConfiguration(new Properties());
        workerConfiguration.set(PLUGIN_PATH_KEY, resource.getFile());
    }

    @Test
    public void testStartPlugin1() throws Exception {
        ManagerClient managerClientMock = Mockito.mock(ManagerClient.class);
        PluginContext pluginContextMock = Mockito.mock(PluginContext.class);
        try (
                MockedStatic<ManagerClientFactory> managerClientFactoryMockedStatic = mockStatic(ManagerClientFactory.class);
                MockedStatic<PluginContextFactory> pluginContextFactoryMockedStatic = mockStatic(PluginContextFactory.class)
        ) {
            managerClientFactoryMockedStatic.when(() -> ManagerClientFactory.createManagerClient(any())).thenReturn(managerClientMock);
            pluginContextFactoryMockedStatic.when(() -> PluginContextFactory.createPluginContext(any(), any())).thenReturn(pluginContextMock);

            String workerConfigJson = new ObjectMapper().writeValueAsString(workerConfiguration.getAll());
            String encodedConfig = Base64.getEncoder().encodeToString(workerConfigJson.getBytes(StandardCharsets.UTF_8));
            System.setProperty(WORKER_CONFIG_BASE64_KEY, encodedConfig);

            ProcessConfiguration processConfiguration = new ProcessConfiguration();
            processConfiguration.set(ProcessConfiguration.MAIN_CLASS_KEY, "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1");
            processConfiguration.set(ProcessConfiguration.PLUGIN_ID_KEY, "testPlugin1");
            processConfiguration.set(ProcessConfiguration.PROCESS_ID_KEY, "testPlugin1ProcessId");
            Map<String, String> payload = new HashMap<>();
            payload.put("name", "Petr");
            payload.put("surname", "Harasil");
            String payloadJson = new ObjectMapper().writeValueAsString(payload);
            String encodedPayload = Base64.getEncoder().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            processConfiguration.set(ProcessConfiguration.PLUGIN_PAYLOAD_BASE64_KEY, encodedPayload);
            File processWorkingDir = prepareProcessWorkingDirectory("testPlugin1ProcessId");
            File standardStreamFile = standardOutFile(processWorkingDir);
            File errStreamFile = errorOutFile(processWorkingDir);
            processConfiguration.set(ProcessConfiguration.SOUT_FILE_KEY, standardStreamFile.getAbsolutePath());
            processConfiguration.set(ProcessConfiguration.SERR_FILE_KEY, errStreamFile.getAbsolutePath());
            String processConfigJson = new ObjectMapper().writeValueAsString(processConfiguration.getAll());
            String encodedProcessConfig = Base64.getEncoder().encodeToString(processConfigJson.getBytes(StandardCharsets.UTF_8));
            System.setProperty(PROCESS_CONFIG_BASE64_KEY, encodedProcessConfig);

            PluginStarter.main(new String[]{});
        }
        verify(managerClientMock, times(1)).updateProcessPid(any(), eq("testPlugin1ProcessId"));
        verify(pluginContextMock, times(1)).scheduleProcess(any());
        verify(pluginContextMock, times(1)).updateProcessName(eq("NewProcessName-PetrHarasil"));
        //verify(pluginContextMock, times(1)).updateProcessState(ProcessState.WARNING);
        //verify(pluginContextMock, times(1)).updateProcessState(ProcessState.RUNNING);
        //verify(pluginContextMock, times(1)).updateProcessState(ProcessState.FINISHED);
    }

}
