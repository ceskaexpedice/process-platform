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

import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import org.ceskaexpedice.processplatform.manager.api.service.ProfileService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.mockito.Mockito.mock;

/**
 * TestProfileEndpoint
 *
 * @author ppodsednik
 */
public class TestProfileEndpoint extends JerseyTest {

    //public static final String BASE_URI = "http://localhost:9998/processplatform/processes/";
    //private HttpServer server;

  /*
  @BeforeEach
  public void setUp() throws Exception {
    final ResourceConfig rc = new ResourceConfig(ProcessResource.class);
    server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    server.start();
  }*/

    @Override
    protected Application configure() {
        MockitoAnnotations.openMocks(this);
        ProfileService profileServiceMock = mock(ProfileService.class);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new ProfileEndpoint(profileServiceMock));
        return resourceConfig;
    }

  /*
  @AfterEach
  public void tearDown() throws Exception {
    //server.shutdownNow();
  }*/

    @Test
    public void testGetProfiles() {
        Response response = target("admin/profiles").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        //Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
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
