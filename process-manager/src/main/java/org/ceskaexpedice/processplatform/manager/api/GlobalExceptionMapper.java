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

import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.BusinessLogicException;
import org.ceskaexpedice.processplatform.common.DataAccessException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        UUID id = UUID.randomUUID();

        String msg = prepareMsg(e, id);

        // Customize status + message for known types
        if (e instanceof BusinessLogicException) {
            LOGGER.warning(String.format("Business logic error [%s]: %s", id, e.getMessage()));
            return buildResponse(Response.Status.BAD_REQUEST, msg);
        }
        if (e instanceof DataAccessException) {
            LOGGER.severe(String.format("Database error [%s]: %s", id, e.getMessage()));
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, msg);
        }
        if (e instanceof ApplicationException) {
            LOGGER.severe(String.format("Application error [%s]: %s", id, e.getMessage()));
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, msg);
        }

        // Default fallback — unexpected error
        LOGGER.severe(String.format("Unexpected error [%s]: %s", id, e.getMessage()));
        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Unexpected error, contact support – id " + id);
    }

    private String prepareMsg(Throwable e, UUID id) {
        return e.getMessage() != null
                ? String.format("%s - id %s", e.getMessage(), id)
                : String.format("Unexpected error - id %s", id);
    }

    private Response buildResponse(Response.Status status, String message) {
        String json = jsonError(message);
        return Response.status(status).entity(json).type("application/json").build();
    }

    private String jsonError(String msg) {
        return String.format("{\"error\": \"%s\"}", escapeJson(msg));
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
