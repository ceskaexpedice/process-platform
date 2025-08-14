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
package org.ceskaexpedice.processplatform.manager.client;


import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * ForManagerTestEndpoint
 *
 * @author ppodsednik
 */
@Path("/manager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForManagerTestEndpoint {
    static final String OUT_LOG_PART = "TestPlugin1.createFullName:name-Petr,surname-Harasil";
    static final String ERR_LOG_PART = "Connection refused";

    @GET
    @Path("{processId}/log/out")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogsOut(@PathParam("processId") String processId,
                                      @DefaultValue("out.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = new ByteArrayInputStream(OUT_LOG_PART.getBytes(StandardCharsets.UTF_8));
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + ".log\"")
                .build();
    }

    @GET
    @Path("{processId}/log/err")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProcessLogErr(@PathParam("processId") String processId,
                                     @DefaultValue("err.txt") @QueryParam("fileName") String fileName) {
        InputStream logStream = new ByteArrayInputStream(ERR_LOG_PART.getBytes(StandardCharsets.UTF_8));
        return Response.ok((StreamingOutput) output -> {
                    try (logStream) {
                        logStream.transferTo(output);
                    }
                }).header("Content-Disposition", "inline; filename=\"" + fileName + ".log\"")
                .build();
    }

    @DELETE
    @Path("{processId}/directory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProcessWorkingDirectory(@PathParam("processId") String processId) {
        return APIRestUtilities.ok("Process [%s] working directory deleted", processId);
    }

    @DELETE
    @Path("{pid}/kill")
    @Produces(MediaType.APPLICATION_JSON)
    public Response killProcessJvm(@PathParam("pid") String pid) {
        boolean killed = true;
        if(!killed){
            return APIRestUtilities.notFound("Process JVM not found [%s]", pid);
        }else{
            return APIRestUtilities.ok("Process JVM Killed [%s]", pid);
        }
    }
}
