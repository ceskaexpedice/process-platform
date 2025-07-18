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
package org.ceskaexpedice.processplatform.common;

import org.ceskaexpedice.processplatform.common.model.NodeType;

public class RemoteNodeException extends RuntimeException {
    private final NodeType nodeType;
    private final int statusCode;

    public RemoteNodeException(String message, NodeType nodeType, int statusCode, Throwable cause) {
        super(message, cause);
        this.nodeType = nodeType;
        this.statusCode = statusCode;
    }

    public RemoteNodeException(String message, NodeType nodeType, int statusCode) {
        super(message);
        this.nodeType = nodeType;
        this.statusCode = statusCode;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
