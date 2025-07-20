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
import org.ceskaexpedice.processplatform.common.model.PluginProfile;
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
import java.util.List;

import static org.ceskaexpedice.testutils.ManagerTestsUtils.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestPluginEndpoint
 *
 * @author ppodsednik
 */
public class TestProfileEndpoint extends JerseyTest {

    @Mock
    private ProfileService profileServiceMock;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new ProfileEndpoint(profileServiceMock));
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
    public void testGetProfile() throws JsonProcessingException {
        PluginProfile retVal = new PluginProfile(PROFILE1_ID, null, PLUGIN1_ID, null);
        when(profileServiceMock.getProfile(eq(PROFILE1_ID))).thenReturn(retVal);

        Response response = target("profile/" + PROFILE1_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        PluginProfile profile = mapper.readValue(json,PluginProfile.class);
        Assertions.assertEquals(PROFILE1_ID, profile.getProfileId());

        response = target("profile/" + PROFILE2_ID).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(404, response.getStatus());

        verify(profileServiceMock, times(2)).getProfile(any());
    }

    @Test
    public void testGetProfiles() throws JsonProcessingException {
        List<PluginProfile> retVal = new ArrayList<>();
        retVal.add(new PluginProfile(PROFILE1_ID, null, PLUGIN1_ID, null));
        retVal.add(new PluginProfile(PROFILE2_ID, null, PLUGIN1_ID, null));
        when(profileServiceMock.getProfiles()).thenReturn(retVal);

        Response response = target("profile/").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        Assertions.assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        List<PluginProfile> profiles = mapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertEquals(2, profiles.size());
    }

    @Test
    public void testUpdateProfile() throws JsonProcessingException {
        PluginProfile pluginProfile = new PluginProfile(PROFILE1_ID, null, PLUGIN1_ID, new ArrayList<>());
        String json = mapper.writeValueAsString(pluginProfile);
        Response response = target("profile/" + PROFILE1_ID).request(MediaType.APPLICATION_JSON).put((Entity.entity(
                json, MediaType.APPLICATION_JSON_TYPE)));
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        verify(profileServiceMock, times(1)).updateProfile(any());
    }

}
