/*
 * Copyright (C) 2025 Inovatika
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ceskaexpedice.processplatform.manager.api;

import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        UUID id = UUID.randomUUID();

        String msg = prepareMsg(e, id);

        // Customize status + message for known types
        if (e instanceof IllegalArgumentException) {
            log.warn("Illegal argument error [{}]: {}", id, e.getMessage());
            return buildResponse(Response.Status.BAD_REQUEST, msg);
        }

        if (e instanceof NullPointerException) {
            log.error("Null pointer [{}]", id, e);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Null pointer exception - id " + id);
        }

        if (e instanceof DataAccessException) {
            log.error("Database error [{}]", id, e);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, msg);
        }

        // Default fallback — unexpected error
        log.error("Unexpected error [{}]", id, e);
        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Unexpected error, contact support – id " + id);
    }

    private String prepareMsg(Throwable e, UUID id) {
        return e.getMessage() != null
                ? String.format("%s - id %s", e.getMessage(), id)
                : String.format("Unexpected error - id %s", id);
    }

    private Response buildResponse(Response.Status status, String message) {
        String json = jsonError(message);  // reuse your method
        return Response.status(status).entity(json).type("application/json").build();
    }

    private String jsonError(String msg) {
        return String.format("{\"error\": \"%s\"}", escapeJson(msg));
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
