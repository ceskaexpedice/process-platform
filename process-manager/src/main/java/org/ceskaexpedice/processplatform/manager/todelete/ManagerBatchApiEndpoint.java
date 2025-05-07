/**
 * Copyright ©2020 Accenture and/or its affiliates. All Rights Reserved.
 *
 * Permission to any use, copy, modify, and distribute this software and
 * its documentation for any purpose is subject to a licensing agreement
 * duly entered into with the copyright owner or its affiliate.
 *
 * All information contained herein is, and remains the property of Accenture
 * and/or its affiliates and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Accenture and/or
 * its affiliates and its suppliers and may be covered by one or more patents
 * or pending patent applications in one or more jurisdictions worldwide,
 * and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Accenture and/or its affiliates.
 */
package org.ceskaexpedice.processplatform.manager.todelete;

import com.accenture.aspire.connector.services.batching.WorkerBatch;
import com.accenture.aspire.connector.services.utilities.APIRestUtilities;
import com.accenture.aspire.framework.utilities.StringUtilities;
import com.accenture.aspire.services.logging.ALogger;
import com.accenture.aspire.services.security.RolesAllowed;
import com.accenture.aspire.services.storage.QueueSAO;
import com.accenture.aspire.services.storage.QueueSAO.QueueEntryType;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.accenture.aspire.connector.services.utilities.NodeUtilities.WORKER_ROLE;
import static com.accenture.aspire.services.security.SecurityRoles.ADMINISTRATOR;

@Path("/")
@RolesAllowed({WORKER_ROLE, ADMINISTRATOR})
public class ManagerBatchApiEndpoint {
  private ManagerActionHandler mrh;
  private ALogger logger;

  public ManagerBatchApiEndpoint(ManagerActionHandler mrh, ALogger logger) {
    this.mrh = mrh;
    this.logger = logger;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response fetchBatchGet(@Context UriInfo uriInfo) {
    try {
      // Get the worker
      String worker = uriInfo.getQueryParameters().getFirst("worker");
      if (StringUtilities.isEmpty(worker)) {
        throw new IllegalArgumentException("Missing worker parameter");
      }

      // Get the type of batch required
      QueueEntryType type = getType(uriInfo);

      // Get the number of required items from the query parameters
      Integer items = APIRestUtilities.parseInteger(uriInfo.getQueryParameters(), "items", Integer.MAX_VALUE, 0, null);

      // Get the tags
      List<String> tagList = uriInfo.getQueryParameters().get("tags");

      // Get a batch
      List<WorkerBatch> wb = mrh.requestBatch(type, worker, tagList, items);
      return APIRestUtilities.jsonPayload(wb, "batch", null);
    }
    catch (RuntimeException e) {
      this.logger.error(e, "Error building response for fetch batch request" );
      return APIRestUtilities.exceptionToErrorResponse(e, null);
    }
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("ack/{id}")
  public Response acknowledgeBatchPut(@Context UriInfo uriInfo, @PathParam("id") long id) {
    try {
      // Get the type of batch required
      QueueSAO.QueueEntryType type = getType(uriInfo);

      if (!mrh.acknowledgeBatch(type, id)) {
        String notFoundMessage = String.format("Batch id not found: %d", id);
        logger.warn(notFoundMessage);
        return APIRestUtilities.notFound(notFoundMessage);
      }
      return APIRestUtilities.ok("Acknowledged batch: %d", id);
    }
    catch (RuntimeException e) {
      logger.error(e,"Error processing ack/%s", id);
      return APIRestUtilities.exceptionToErrorResponse(e, null);
    }
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("ack")
  public Response acknowledgeBatch(@Context UriInfo uriInfo, String idsBody) {
    try {
      // Get the type of batch required
      QueueSAO.QueueEntryType type = getType(uriInfo);

      List<Integer> ids = APIRestUtilities.getIntegerIds(idsBody);
      if (ids.isEmpty())
        return APIRestUtilities.badRequest("No batch ids were provided");

      boolean success = true;
      List<Integer> nf = new ArrayList<Integer>();
      for (Integer id:ids) {
        if (!mrh.acknowledgeBatch(type, id)) {
          // This worker not found
          nf.add(id);
          success = false;
        }
      }
      if (success)
        return APIRestUtilities.ok("Acknowledged batch(es): %s", join(",", ids));
      String notFoundMessage = String.format("Acknowledged batch(es) - some not found: %s (%s)", join(",", ids), join(",", nf));
      logger.warn(notFoundMessage);
      return APIRestUtilities.notFound(notFoundMessage);
    }
    catch (RuntimeException e) {
      logger.error(e, "Error processing ack");
      return APIRestUtilities.exceptionToErrorResponse(e, null);
    }
  }

  private String join(String sep, Collection<Integer> l) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Integer i:l) {
      if (!first)
        sb.append(sep);
      else
        first = false;
      sb.append(i);
    }
    return sb.toString();
  }

  private QueueSAO.QueueEntryType getType(UriInfo uriInfo) {
    String type = uriInfo.getQueryParameters().getFirst("type");
    if (StringUtilities.isEmpty(type)) {
      throw new IllegalArgumentException("Missing type parameter");
    }
    try {
      return QueueSAO.QueueEntryType.valueOf(type);
    }
    catch (IllegalArgumentException iae) {
      throw new IllegalArgumentException(String.format("Bad type parameter: %s", type));
    }
  }
}
