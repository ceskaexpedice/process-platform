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
package org.ceskaexpedice.processplatform.worker.utils;


import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * ManagerAgentEndpoint
 *
 * @author ppodsednik
 */
@Path("/agent")
public class ManagerAgentEndpoint {
  //private final AgentService agentService;

  /*
  public ManagerAgentEndpoint(AgentService agentService) {
    //this.agentService = agentService;
  }*/

  @GET
  @Path("/next-process")
  @Produces(MediaType.APPLICATION_JSON)
  public ScheduledProcessTO getNextProcess() {
    List<String> jvmArgs = new ArrayList<>();
    jvmArgs.add("-Xmx1024m");
    jvmArgs.add("-Xms256m");
    jvmArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=58001");

    Map<String,String> payload = new HashMap<>();
    payload.put("name","Petr");
    payload.put("surname","Harasil");

    ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
            UUID.randomUUID(),
            "testPlugin1",
            "org.ceskaexpedice.processplatform.testplugin1.TestPlugin1",
            payload,
            jvmArgs);
    return scheduledProcessTO;
    //return agentService.getNextScheduledProcess();
        /*
                // Implementation: fetch next process from DB
        ScheduledProcessTO scheduledProcessTO = new ScheduledProcessTO(
                UUID.randomUUID(),
                "pluginInfo.getPluginId()",
                "profileId",
                "pluginInfo.getMainClass()",
                null,
                null,
                null);
        if (scheduledProcessTO != null) {
            return Response.ok(scheduledProcessTO).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

         */
  }
//
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/length/{name}")
//  public String getNameLength(@PathParam("name") String name, @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType) {
//    if (contentType == null) {
//    return "{\"length\":" + name.length() + "}";
//    } else {
//      return "{\"length\":" + name.length() + ", \"contentType\": \""+contentType+"\"}";
//    }
//  }
//
//  @HEAD
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/length/{name}")
//  public String getNameLengthHead(@PathParam("name") String name, @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType) {
//    return getNameLength(name, contentType);
//  }
//
//  @DELETE
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/length/{name}")
//  public String getNameLengthDelete(@PathParam("name") String name, @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType) {
//    return getNameLength(name, contentType);
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/basicAuth")
//  public Response basicAuthGet(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth) throws UnsupportedEncodingException {
//    auth = auth.substring(auth.indexOf(" ") + 1);
//    System.out.println(auth);
//    if (StringUtilities.isNotEmpty(auth)) {
//      String[] creds = new String(Base64Utilities.decode(auth.getBytes()), "UTF-8").split(":");
//      if (StringUtilities.equals("user", creds[0]) &&
//              StringUtilities.equals("pass", creds[1])) {
//        return Response.ok(getEntities(-1, -1), MediaType.APPLICATION_JSON).build();
//      }
//    }
//    return Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic").build();
//  }
//
//  public static int loginForBearerCounter = 0;
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/loginForBearer")
//  public Response loginForBearer(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, AspireCredentials cred) {
//    loginForBearerCounter++;
//    if (loginForBearerCounter > 3) {
//      return Response.status(Response.Status.EXPECTATION_FAILED).build();
//    }
//    if (!("user".equals(cred.getUsername()) && "pass".equals(cred.getPassword()))) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//    System.out.println("***** loginForBearer called");
//    SecretSignatureConfiguration config = new SecretSignatureConfiguration(RandomStringUtils.randomAlphabetic(32), JWSAlgorithm.HS256);
//    JwtGenerator generator = new JwtGenerator(config);
//    generator.setExpirationTime(new Date(System.currentTimeMillis() + DateTimeUtilities.SECONDS(5)));
//    Map<String, Object> payload = new HashMap<>();
//    payload.put(JwtClaims.SUBJECT, cred.getUsername());
//    String accessToken = generator.generate(payload);
//
//    LoginResponse resp = new LoginResponse();
//    resp.setAccessToken(accessToken);
//    return Response.ok(resp).build();
//
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/getEntitiesForBearer")
//  public Response getEntitiesForBearer(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("offset") long offset,
//                                       @QueryParam("pageSize") long pageSize) {
//    System.out.println("***** entitiesForBearer called");
//    if (StringUtilities.isEmpty(auth)) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//    if (!auth.startsWith("Bearer")) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//    AspireObject response = new AspireObject("response");
//    int i = 0;
//    AspireObject entity = new AspireObject("entities");
//    entity.set("id", "" + i);
//    entity.push("idStructure");
//    entity.set("id", "" + i);
//    entity.pop();
//    entity.set("name", "name");
//    entity.set("last", "lastName");
//    entity.push("level1");
//    entity.add("fieldA", "valueA");
//    entity.push("level2");
//    entity.add("fieldB", "valueB");
//    entity.pop();
//    entity.pop();
//    response.add(entity);
//    response.add("totalentries", i);
//    return Response.ok(response.toJsonString()).build();
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/getAllPaged")
//  public String getEntities(@QueryParam("page") int page) {
//    int size = 10;
//    AspireObject response = new AspireObject("response");
//    if (page < 5)
//      for (int i = (page*size); i<((page*size)+size); i++) {
//        AspireObject entity = new AspireObject("entities");
//        entity.set("id", i);
//        entity.set("field", "This is the field for "+i);
//        response.add(entity);
//      }
//    return response.toJsonString(false);
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/getAll")
//  public String getEntities(String json) {
//    AspireObject query = AspireObject.createFromJSON("request", new StringReader(json));
//    return getEntities(query.getLong("offset"), query.getLong("pageSize"));
//  }
//
//  public static int loginForAzureCounter = 0;
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/loginForAzure")
//  public Response loginForAzure(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, AspireCredentials cred) {
//    loginForAzureCounter++;
//    if (loginForAzureCounter > 2) {
//      return Response.status(Response.Status.EXPECTATION_FAILED).build();
//    }
//    /*if (!("client_secret".equals(cred.getPassword()))) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }*/
//    System.out.println("***** loginForAzure called");
//    SecretSignatureConfiguration config = new SecretSignatureConfiguration(RandomStringUtils.randomAlphabetic(32), JWSAlgorithm.HS256);
//    JwtGenerator generator = new JwtGenerator(config);
//    generator.setExpirationTime(new Date(System.currentTimeMillis() + DateTimeUtilities.SECONDS(5)));
//    Map<String, Object> payload = new HashMap<>();
//    String accessToken = generator.generate(payload);
//
//    LoginResponse resp = new LoginResponse();
//    resp.setAccessToken(accessToken);
//    return Response.ok(resp).build();
//
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/getEntitiesForAzure")
//  public Response getEntitiesForAzure(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("offset") long offset,
//                                      @QueryParam("pageSize") long pageSize) {
//    System.out.println("***** entitiesForAzure called");
//    if (StringUtilities.isEmpty(auth)) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//    if (!auth.startsWith("Azure")) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//    AspireObject response = new AspireObject("response");
//    int i = 0;
//    AspireObject entity = new AspireObject("entities");
//    entity.set("id", "" + i);
//    entity.push("idStructure");
//    entity.set("id", "" + i);
//    entity.pop();
//    entity.set("name", "name");
//    entity.set("last", "lastName");
//    entity.push("level1");
//    entity.add("fieldAzureA", "valueAzureA");
//    entity.push("level2");
//    entity.add("fieldAzureB", "valueAzureB");
//    entity.pop();
//    entity.pop();
//    response.add(entity);
//    response.add("totalentries", i);
//    return Response.ok(response.toJsonString()).build();
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/getEntitiesAll")
//  public String getEntitiesAll(@QueryParam("offset") long offset,
//                               @QueryParam("pageSize") long pageSize) {
//    String response = getEntities(offset, pageSize, false);
//    if (offset >= 323)
//      response = "[]";
//    else
//      response = response.substring(24, response.length() - 21);
//    System.out.println(response);
//    return response;
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/testParams")
//  public String testParams(String json) {
//    AspireObject query = AspireObject.createFromJSON(null, new StringReader(json));
//    return query.toJsonString(false);
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/length")
//  public String getNameLengthPost(String body, @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType) {
//    return getNameLength(body, contentType);
//  }
//
//  @PUT
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/length")
//  public String getNameLengthPut(String body, @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType) {
//    return getNameLength(body, contentType);
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_OCTET_STREAM)
//  @Path("/fetch/{id}")
//  public byte[] testFetcher(@PathParam("id") String id) {
//    StringBuilder sb = new StringBuilder();
//    for (int i = 0; i < 100; i++) {
//      sb.append(id);
//      sb.append("\n");
//    }
//    byte[] content = sb.toString().getBytes();
//    System.out.println(content.length);
//    return content;
//  }
//
//  public String getEntities(long offset,
//                            long pageSize, boolean generateNextLink) {
//    try {
//      String[] names = new String[]{"Liam", "Noah", "Oliver", "William", "Olivia", "Emma", "Ava", "Sophia", "Laura", "Mario",
//              "Michaella", "Delilah", "Redna", "Esoj", "Kered", "Odraude", "Luap"};
//      String[] lastNames = new String[]{"Smith", "Johnson", "Brown", "Williams", "Jones", "Garcia", "Miller", "Davis", "Roberts",
//              "Shoeman", "Truman", "Jackson", "Obama", "Trump", "Vargas", "Towers", "Blackberry", "Cartago", "Campeon"};
//      AspireObject response = new AspireObject("response");
//
//      int i = 0;
//      int pageTotal = 0;
//      for (String name : names) {
//        for (String lastName : lastNames) {
//          i++;
//
//          if (offset != -1 && i - 1 < offset)
//            continue;
//          pageTotal++;
//          if (pageSize != -1 && pageTotal > pageSize)
//            continue;
//
//          AspireObject entity = new AspireObject("entities");
//          entity.set("id", "" + i);
//          entity.push("idStructure");
//          entity.set("id", "" + i);
//          entity.pop();
//          entity.set("name", name);
//          entity.set("last", lastName);
//          entity.push("level1");
//          entity.add("fieldA", "valueA");
//          entity.push("level2");
//          entity.add("fieldB", "valueB");
//          entity.pop();
//          entity.pop();
//          response.add(entity);
//        }
//      }
//      if (response.get("entities") != null && response.getAll("entities").size() > 0 && generateNextLink) {
//        response.add("nextPage", "http://localhost:9998/api/getEntities?offset=" + (offset + pageSize) + "&pageSize=" + pageSize);
//      }
//      response.add("totalentries", i);
//      return response.toJsonString(false);
//    } catch (RuntimeException e) {
//      throw new AspireException("TestRestEndpoint-getEntities ", e);
//    }
//  }
//
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/getEntities")
//  public String getEntities(@QueryParam("offset") long offset,
//                            @QueryParam("pageSize") long pageSize) {
//    return getEntities(offset, pageSize, true);
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/minerva-task-queue/_search")
//  public String getFirstTaskFromQueue(String json) {
//    System.out.println("Minerva task-queue first task query:" + json);
//    String resp = "{" +
//            "    \"hits\": {" +
//            "      \"total\": {" +
//            "      }," +
//            "      \"hits\": [" +
//            "        {" +
//            "          \"_index\": \"minerva-task-queue\"," +
//            "          \"_type\": \"_doc\"," +
//            "          \"_id\": \"CFCD208495D565EF66E7DFF9F98764DA\"," +
//            "          \"_score\": 0.0," +
//            "          \"_source\": {" +
//            "              \"query\": \"{    \\\"match_all\\\": {}  }\"," +
//            "              \"indexName\": \"aspire-base_junit\"," +
//            "              \"type\": \"simple\"," +
//            "              \"properties\": {\"tag\": \"tagValue\"}" +
//            "          }" +
//            "        }" +
//            "      ]" +
//            "    }" +
//            "}";
//
//    AspireObject response = AspireObject.createFromJSON("",resp, false);
//    return response.toJsonString(AspireObject.CONTENT_ONLY);
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/aspire-base_junit/_search")
//  public String getEntitiesScroll_firstPage(@QueryParam("scroll") String cntx, String json) {
//    System.out.println("getEntitiesScroll_firstPage:" + json);
//    String resp = "{" +
//            "    \"" + ElasticLikeScrollPagination.ELASTIC_RESPONSE_SCROLL_ID_FIELD + "\": \"" + TestRestRAP.SCROLL_ID + "\"," +
//            "    \"hits\": {" +
//            "      \"total\": {" +
//            "      }," +
//            "      \"hits\": [" +
//            "        {" +
//            "          \"_index\": \"aspire-base_junit\"," +
//            "          \"_type\": \"_doc\"," +
//            "          \"_id\": \"CFCD208495D565EF66E7DFF9F98764DA\"," +
//            "          \"_score\": 0.0," +
//            "          \"_source\": {" +
//            "            \"doc\": {" +
//            "              \"id\": 0," +
//            "              \"someField\": \"someValue\"" +
//            "            }" +
//            "          }" +
//            "        }," +
//            "        {" +
//            "          \"_index\": \"aspire-base_junit\"," +
//            "          \"_type\": \"_doc\"," +
//            "          \"_id\": \"CFCD208495D565EF66E7DFF9F98764DB\"," +
//            "          \"_score\": 0.0," +
//            "          \"_source\": {" +
//            "            \"doc\": {" +
//            "              \"id\": 1," +
//            "              \"someField\": \"someValue1\"" +
//            "            }" +
//            "          }" +
//            "        }" +
//            "      ]" +
//            "    }" +
//            "}";
//
//    AspireObject response = AspireObject.createFromJSON("",resp, false);
//    return response.toJsonString(AspireObject.CONTENT_ONLY);
//  }
//
//  @POST
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/_search/scroll")
//  public String getEntitiesScroll_nextPage(String json) {
//    System.out.println("getEntitiesScroll_nextPage:" + json);
//    AspireObject body = AspireObject.createFromJSON(null, new StringReader(json));
//    assertEquals(TestRestRAP.SCROLL_ID, body.getText(ElasticLikeScrollPagination.ELASTIC_SCROLL_ID_FIELD));
//
//    String resp = "{" +
//            "    \"" + ElasticLikeScrollPagination.ELASTIC_RESPONSE_SCROLL_ID_FIELD + "\": \"\"," +
//            "    \"hits\": {" +
//            "      \"total\": {" +
//            "      }," +
//            "      \"hits\": [" +
//            "        {" +
//            "          \"_index\": \"aspire-base_junit\"," +
//            "          \"_type\": \"_doc\"," +
//            "          \"_id\": \"CFCD208495D565EF66E7DFF9F98764DA\"," +
//            "          \"_score\": 0.0," +
//            "          \"_source\": {" +
//            "            \"doc\": {" +
//            "              \"id\": 5," +
//            "              \"someField\": \"someValue\"" +
//            "            }" +
//            "          }" +
//            "        }" +
//            "      ]" +
//            "    }" +
//            "}";
//    AspireObject response = AspireObject.createFromJSON("",resp, false);
//    return response.toJsonString(AspireObject.CONTENT_ONLY);
//  }
//
//  public static boolean scrollDelete = false;
//  public static boolean deleteFails = false;
//  @DELETE
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  @Path("/_search/scroll")
//  public String getEntitiesScroll_deleteContext(String json) {
//    scrollDelete = true;
//    System.out.println("getEntitiesScroll_deleteContext:" + json);
//    AspireObject body = AspireObject.createFromJSON(null, new StringReader(json));
//    assertEquals(TestRestRAP.SCROLL_ID, body.getContent());
//    AspireObject response = AspireObject.createFromJSON("","{}", false);
//
//    if(deleteFails)
//      throw new AspireException("Response.error","Error deleting %s",TestRestRAP.SCROLL_ID);
//
//    return response.toJsonString(AspireObject.CONTENT_ONLY);
//
//  }
//
//  @DELETE
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/_search/scroll/{scroll_id}")
//  public String getEntitiesScroll_deleteContext2(@PathParam("scroll_id") String scrollId) {
//    return "{\"scrollId\":" + scrollId + "}";
//  }
//
}
