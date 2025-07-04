/**
 * Copyright ©2020 Accenture and/or its affiliates. All Rights Reserved.
 * <p>
 * Permission to any use, copy, modify, and distribute this software and
 * its documentation for any purpose is subject to a licensing agreement
 * duly entered into with the copyright owner or its affiliate.
 * <p>
 * All information contained herein is, and remains the property of Accenture
 * and/or its affiliates and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Accenture and/or
 * its affiliates and its suppliers and may be covered by one or more patents
 * or pending patent applications in one or more jurisdictions worldwide,
 * and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Accenture and/or its affiliates.
 */
package org.ceskaexpedice.processplatform.common.utils;

import org.ceskaexpedice.processplatform.common.DataAccessException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;
import java.util.logging.Logger;

public final class APIRestUtilities {
    private static final Logger LOGGER = Logger.getLogger(APIRestUtilities.class.getName());

    private APIRestUtilities() {
    }

    public static Response exceptionToErrorResponse(Exception e) {
        UUID id = UUID.randomUUID();
        if (e instanceof NullPointerException) {
            error(e, id);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(jsonError(String.format("Null pointer exception - id %s",id))).build();
        }
        String msg = prepareMsg(e, id);
        if (e instanceof IllegalArgumentException) {
            warn(e, id);
            return Response.status(Status.BAD_REQUEST).entity(jsonError(msg)).build();
        }
        if (e instanceof DataAccessException) {
            error(e, id);
            return Response.serverError().entity(jsonError(msg)).build();
        }

        // Generic, some other not expected error
        // We need to hide any specific information
        // global Message with id only generated
        String globalMsg = String.format("Unexpected error, contact support - id %s", id);
        error(e, id);
        return Response.serverError().entity(jsonError(globalMsg)).build();
    }

    public static String exceptionToErrorMsg(Exception e) {
        UUID id = UUID.randomUUID();
        if (e instanceof NullPointerException) {
            error(e, id);
            return String.format("Null pointer exception - id %s",id);
        }
        String msg = prepareMsg(e, id);
        if (e instanceof IllegalArgumentException) {
            warn(e, id);
            return msg;
        }
        /*
        if (e instanceof AspireException) {
            error(logger, e, id);
            return msg;
        }*/

        // Generic, some other not expected error
        // We need to hide any specific information
        // global Message with id only generated
        String globalMsg = String.format("Unexpected error, contact support - id %s", id);
        error(e, id);
        return globalMsg;
    }

    private static void error(Exception e, UUID id) {
        //log(logger, Severity.ERROR, e, id);
    }

    private static void warn(Exception e, UUID id) {
        //log(logger, Severity.WARN, e, id);
    }

    /*
    private static void log(ALogger logger, Severity sev, Exception e, UUID id) {
        if (logger != null)
            logger.log(sev, e, "Exception in REST handler: id: %s, message: %s", id, e.getMessage());
    }*/

    /**
     * Create a json style error message from the message on an exception
     *
     * @param e the exception
     * @return the json
     */
    private static String jsonError(Exception e) {
        return String.format("{\"error\":{\"message\": \"%s\"}}", e.toString());
    }

    /**
     * Create a json style error message
     *
     * @return the json
     */
    private static String jsonError(String fmt, Object... args) {
        String msg = args == null || args.length == 0 ? fmt : String.format(fmt, args);
        return String.format("{\"error\":{\"message\": \"%s\"}}", msg);
    }


    /**
     * Create a JAX response for an OK (200) message
     *
     * @param fmt
     * @param args
     * @return the JAX response
     */
    public static Response ok(String fmt, Object... args) {
        return Response.ok().entity(jsonMessage(fmt, args)).build();
    }

    private static String jsonMessage(String fmt, Object... args) {
        String msg = (args == null || args.length == 0) ? fmt : String.format(fmt, args);
        return String.format("{\"message\": \"%s\"}", msg);
    }

    /**
     * Create a JAX response for an Not Found (404) message
     *
     * @param fmt
     * @param args
     * @return the JAX response
     */
    public static Response notFound(String fmt, Object... args) {
        return Response.status(Status.NOT_FOUND).entity(jsonMessage(fmt, args)).build();
    }

    /**
     * Create a JAX response for an Forbidden (403) message
     *
     * @param fmt
     * @param args
     * @return the JAX response
     */
    public static Response forbidden(String fmt, Object... args) {
        return Response.status(Status.FORBIDDEN).entity(jsonMessage(fmt, args)).build();
    }

    /**
     * Create a JAX response for an Not Acceptable (406) message
     *
     * @param fmt
     * @param args
     * @return the JAX response
     */
    public static Response notAcceptable(String fmt, Object... args) {
        return Response.status(Status.NOT_ACCEPTABLE).entity(jsonMessage(fmt, args)).build();
    }

    /**
     * Create a JAX response for a Not Acceptable (406) message
     * @param e
     * @return
     */
    public static Response notAcceptable(Exception e) {
        UUID id = UUID.randomUUID();
        error(e,id);
        return Response.status(Status.NOT_ACCEPTABLE).entity(jsonMessage(prepareMsg(e,id))).build();
    }

    /**
     * Create a JAX response for a bad request (400) message
     *
     * @param fmt
     * @param args
     * @return the JAX response
     */
    public static Response badRequest(String fmt, Object... args) {
        return Response.status(Status.BAD_REQUEST).entity(jsonError(fmt, args)).build();
    }

    /**
     * Create an "ok" (200) response with a Json payload
     *
     * @param jsonPayload the payload
     * @return the JAX response
     */
    public static Response jsonPayload(String jsonPayload) {
        return Response.ok(jsonPayload, MediaType.APPLICATION_JSON).build();
    }


    /**
     * Avoiding to be java class name in message
     *
     * @param e
     * @return
     */

    public static String prepareMsg(Exception e, UUID id) {
        String msg = e.getMessage().replaceAll(String.format("%s:",e.getClass().getName()), "");
        if (id == null) {
            return msg;
        }
        return String.format("%s - id %s", msg, id);
    }


}