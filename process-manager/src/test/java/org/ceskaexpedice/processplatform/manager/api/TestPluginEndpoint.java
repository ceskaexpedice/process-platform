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

import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * TestPluginEndpoint
 *
 * @author ppodsednik
 */
public class TestPluginEndpoint extends JerseyTest {

    @Mock
    private PluginService pluginServiceMock;

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);

        /*
        ManagerConfiguration managerConfiguration = new ManagerConfiguration(new Properties());
        managerConfiguration.set(ManagerConfiguration.JDBC_URL_KEY, "jdbc:postgresql://localhost:15432/kramerius");
        managerConfiguration.set(ManagerConfiguration.JDBC_USER_NAME_KEY, "fedoraAdmin");
        managerConfiguration.set(ManagerConfiguration.JDBC_USER_PASSWORD_KEY, "fedoraAdmin");
        DbConnectionProvider dbConnectionProvider = new DbConnectionProvider(managerConfiguration);
        PluginService pluginService = new PluginService(managerConfiguration, dbConnectionProvider);

         */


        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new PluginEndpoint(pluginServiceMock));
        resourceConfig.register(GlobalExceptionMapper.class);
        resourceConfig.register(new LoggingFeature(
                Logger.getLogger("HTTPLogger"),
                LoggingFeature.Verbosity.PAYLOAD_ANY
        ));
        return resourceConfig;
    }

    @Test
    public void testGetPlugin() {
        PluginInfo pluginInfo = new PluginInfo("testPlugin1", null, null, null, null, null);
        //when(pluginServiceMock.getPlugin(eq("testPlugin1"))).thenReturn(pluginInfo);
        when(pluginServiceMock.getPlugin(anyString())).thenAnswer(invocation -> {
            String pluginId = invocation.getArgument(0);
            if ("testPluginError".equals(pluginId)) {
                throw new BusinessLogicException("Plugin 'testPluginError' caused an error");
            } else if ("testPlugin1".equals(pluginId)) {
                return pluginInfo;
            }
            return null; // Or throw for unknown IDs
        });

        Response response = target("plugin/testPluginError").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        Assertions.assertEquals(400, response.getStatus());
        /*
        assertThrows(BusinessLogicException.class, () -> {
            pluginServiceMock.getPlugin("testPluginError");
        });*/

        String entity = response.readEntity(String.class);
        verify(pluginServiceMock, times(1)).getPlugin(eq("testPluginError"));
        System.out.println(entity);
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
