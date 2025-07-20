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
import org.ceskaexpedice.processplatform.common.model.PluginInfo;
import org.ceskaexpedice.processplatform.manager.api.service.PluginService;
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

import static org.ceskaexpedice.testutils.ManagerTestsUtils.PLUGIN1_ID;
import static org.ceskaexpedice.testutils.ManagerTestsUtils.PLUGIN2_ID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestPluginEndpoint
 *
 * @author ppodsednik
 */
public class TestPluginEndpoint extends JerseyTest {

    @Mock
    private PluginService pluginServiceMock;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new PluginEndpoint(pluginServiceMock));
        resourceConfig.register(GlobalExceptionMapper.class);
        /* TODO
        resourceConfig.register(new LoggingFeature(
                Logger.getLogger("HTTPLogger"),
                LoggingFeature.Verbosity.PAYLOAD_ANY
        ));
         */
        return resourceConfig;
    }

    @Test
    public void testGetPlugin() throws JsonProcessingException {
        PluginInfo retVal = new PluginInfo(PLUGIN1_ID, null, null, null, null, null);
        when(pluginServiceMock.getPlugin(eq(PLUGIN1_ID))).thenReturn(retVal);

        Response response = target("plugin/" + PLUGIN1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        PluginInfo pluginInfo = mapper.readValue(json,PluginInfo.class);
        Assertions.assertEquals(PLUGIN1_ID, pluginInfo.getPluginId());

        response = target("plugin/" + PLUGIN2_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(404, response.getStatus());

        verify(pluginServiceMock, times(2)).getPlugin(any());
        /* TODO
        when(pluginServiceMock.getPlugin(anyString())).thenAnswer(invocation -> {
            String pluginId = invocation.getArgument(0);
            if ("testPluginError".equals(pluginId)) {
                throw new BusinessLogicException("Plugin 'testPluginError' caused an error");
            } else if ("testPlugin1".equals(pluginId)) {
                return retVal;
            }
            return null; // Or throw for unknown IDs
        });
         */
        /*
        assertThrows(BusinessLogicException.class, () -> {
            pluginServiceMock.getPlugin("testPluginError");
        });*/
    }

    @Test
    public void testGetPlugins() throws JsonProcessingException {
        List<PluginInfo> retVal = new ArrayList<>();
        retVal.add(new PluginInfo(PLUGIN1_ID, null, null, null, null, null));
        retVal.add(new PluginInfo(PLUGIN2_ID, null, null, null, null, null));
        when(pluginServiceMock.getPlugins()).thenReturn(retVal);

        Response response = target("plugin/").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        List<PluginInfo> pluginInfos = mapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertEquals(2, pluginInfos.size());
    }

}
