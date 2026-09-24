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
package org.ceskaexpedice.processplatform.worker.client;

import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;
import org.json.JSONObject;

/**
 * Effective HTTP client configuration used by the worker when calling the manager.
 */
final class ManagerClientConfiguration {

    private final int maxConnections;
    private final int maxConnectionsPerRoute;
    private final long connectTimeoutMs;
    private final long socketTimeoutMs;
    private final long connectionRequestTimeoutMs;
    private final long responseTimeoutMs;
    private final long validateAfterInactivityMs;
    private final long evictIdleConnectionsMs;

    private ManagerClientConfiguration(WorkerConfiguration workerConfiguration) {
        this.maxConnections = workerConfiguration.getHttpClientMaxConnections();
        this.maxConnectionsPerRoute = workerConfiguration.getHttpClientMaxConnectionsPerRoute();
        this.connectTimeoutMs = workerConfiguration.getHttpClientConnectTimeoutMs();
        this.socketTimeoutMs = workerConfiguration.getHttpClientSocketTimeoutMs();
        this.connectionRequestTimeoutMs = workerConfiguration.getHttpClientConnectionRequestTimeoutMs();
        this.responseTimeoutMs = workerConfiguration.getHttpClientResponseTimeoutMs();
        this.validateAfterInactivityMs = workerConfiguration.getHttpClientValidateAfterInactivityMs();
        this.evictIdleConnectionsMs = workerConfiguration.getHttpClientEvictIdleConnectionsMs();
    }

    static ManagerClientConfiguration from(WorkerConfiguration workerConfiguration) {
        return new ManagerClientConfiguration(workerConfiguration);
    }

    int getMaxConnections() {
        return maxConnections;
    }

    int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    long getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    long getConnectionRequestTimeoutMs() {
        return connectionRequestTimeoutMs;
    }

    long getResponseTimeoutMs() {
        return responseTimeoutMs;
    }

    long getValidateAfterInactivityMs() {
        return validateAfterInactivityMs;
    }

    long getEvictIdleConnectionsMs() {
        return evictIdleConnectionsMs;
    }

    JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("maxConnections", maxConnections);
        json.put("maxConnectionsPerRoute", maxConnectionsPerRoute);
        json.put("connectTimeoutMs", connectTimeoutMs);
        json.put("socketTimeoutMs", socketTimeoutMs);
        json.put("connectionRequestTimeoutMs", connectionRequestTimeoutMs);
        json.put("responseTimeoutMs", responseTimeoutMs);
        json.put("validateAfterInactivityMs", validateAfterInactivityMs);
        json.put("evictIdleConnectionsMs", evictIdleConnectionsMs);
        return json;
    }

    @Override
    public String toString() {
        return "ManagerClientConfiguration{" +
                "maxConnections=" + maxConnections +
                ", maxConnectionsPerRoute=" + maxConnectionsPerRoute +
                ", connectTimeoutMs=" + connectTimeoutMs +
                ", socketTimeoutMs=" + socketTimeoutMs +
                ", connectionRequestTimeoutMs=" + connectionRequestTimeoutMs +
                ", responseTimeoutMs=" + responseTimeoutMs +
                ", validateAfterInactivityMs=" + validateAfterInactivityMs +
                ", evictIdleConnectionsMs=" + evictIdleConnectionsMs +
                '}';
    }
}
