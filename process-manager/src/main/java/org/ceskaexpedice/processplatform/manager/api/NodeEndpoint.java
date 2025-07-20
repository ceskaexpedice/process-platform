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

import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.utils.APIRestUtilities;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeEndpoint {

    private final NodeService nodeService;

    public NodeEndpoint(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GET
    @Path("/{nodeId}")
    public Response getNode(@PathParam("nodeId") String nodeId) {
        Node node = nodeService.getNode(nodeId);
        if (node == null) {
            return APIRestUtilities.notFound("Node not found: %s", nodeId);
        }
        return Response.ok(node).build();
    }

    @GET
    public Response getNodes() {
        List<Node> nodes = nodeService.getNodes();
        return Response.ok(nodes).build();
    }

}
