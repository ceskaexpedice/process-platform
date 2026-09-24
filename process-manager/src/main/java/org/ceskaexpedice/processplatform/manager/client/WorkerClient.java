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
package org.ceskaexpedice.processplatform.manager.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.RemoteNodeException;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.NodeType;
import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.process.ProcessService;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.json.JSONObject;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ManagerClient
 *
 * @author ppodsednik
 */
public class WorkerClient {

    private static final Logger LOGGER = Logger.getLogger(WorkerClient.class.getName());

    private final CloseableHttpClient closeableHttpClient;
    private final PoolingHttpClientConnectionManager poolConnectionManager;
    private final WorkerClientConfiguration configuration;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProcessService processService;
    private final NodeService nodeService;

    WorkerClient(ProcessService processService, NodeService nodeService) {
        this(processService, nodeService, new ManagerConfiguration(Collections.emptyMap()));
    }

    WorkerClient(ProcessService processService, NodeService nodeService, ManagerConfiguration managerConfiguration) {
        this.configuration = WorkerClientConfiguration.from(managerConfiguration);
        LOGGER.info("Initializing WorkerClient with " + configuration);

        this.poolConnectionManager = new PoolingHttpClientConnectionManager();
        poolConnectionManager.setMaxTotal(configuration.getMaxConnections());
        poolConnectionManager.setDefaultMaxPerRoute(configuration.getMaxConnectionsPerRoute());
        poolConnectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(configuration.getConnectTimeoutMs()))
                .setSocketTimeout(Timeout.ofMilliseconds(configuration.getSocketTimeoutMs()))
                .setValidateAfterInactivity(TimeValue.ofMilliseconds(configuration.getValidateAfterInactivityMs()))
                .build());
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(configuration.getConnectionRequestTimeoutMs()))
                .setResponseTimeout(Timeout.ofMilliseconds(configuration.getResponseTimeoutMs()))
                .build();
        this.closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnectionManager)
                .disableAuthCaching()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMilliseconds(configuration.getEvictIdleConnectionsMs()))
                .build();
        this.processService = processService;
        this.nodeService = nodeService;
        logPoolStats("initialized");
    }

    public void deleteProcessWorkerDir(String processId) {
        String url = getWorkerBaseUrl(processId) + "manager/" + processId + "/directory";
        LOGGER.info("Delete process working dir at " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        int statusCode = -1;
        logPoolStats("before DELETE " + url);
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpDelete)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteNodeException("Failed to delete process worker dir", NodeType.WORKER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, statusCode, e);
        } finally {
            logPoolStats("after DELETE " + url + ", status=" + statusCode);
        }
    }

    public void killProcessJvm(String processId, String pid) {
        String url = getWorkerBaseUrl(processId) + "manager/" + pid + "/kill";
        LOGGER.info("Kill worker JVM process at " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        int statusCode = -1;
        logPoolStats("before DELETE " + url);
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpDelete)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 404) {
                throw new RemoteNodeException("Failed to kill process JVM", NodeType.WORKER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, statusCode, e);
        } finally {
            logPoolStats("after DELETE " + url + ", status=" + statusCode);
        }
    }

    public InputStream getProcessLog(String processId, boolean err) {
        String suffix = err ? "err" : "out";
        URIBuilder uriBuilder;
        HttpGet get;
        try {
            uriBuilder = new URIBuilder(getWorkerBaseUrl(processId) + "manager/" + processId + "/log/" + suffix);
            URI uri = uriBuilder.build();
            get = new HttpGet(uri);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.toString(), e);
        }
        int statusCode = -1;
        try {
            String requestUri = get.getUri().toString();
            LOGGER.info(String.format("Getting process log for processId: [%s]; url: [%s] ", processId, requestUri));
            logPoolStats("before GET " + requestUri);
            CloseableHttpResponse response = closeableHttpClient.execute(get);
            int code = response.getCode();
            statusCode = code;
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                int streamStatusCode = statusCode;
                logPoolStats("stream opened GET " + requestUri + ", status=" + streamStatusCode);
                return new ResponseClosingInputStream(is, response,
                        () -> logPoolStats("stream closed GET " + requestUri + ", status=" + streamStatusCode));
            } else if (code == 404) {
                response.close();
                return null;
            } else {
                response.close();
                throw new RemoteNodeException("Failed to get process log", NodeType.WORKER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, statusCode, e);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.toString(), e);
        } finally {
            logPoolStats("after GET process log, status=" + statusCode);
        }
    }

    public JSONObject getProcessLogLines(String processId, String offset, String limit, boolean err) {
        String suffix = err ? "err" : "out";
        int code = -1;
        try {
            URIBuilder uriBuilder = new URIBuilder(getWorkerBaseUrl(processId) + "manager/" + processId + "/log/" + suffix + "/lines");
            if (offset != null) uriBuilder.addParameter("offset", offset);
            if (limit != null) uriBuilder.addParameter("limit", limit);

            HttpGet get = new HttpGet(uriBuilder.build());
            logPoolStats("before GET " + get.getUri().toString());
            try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
                code = response.getCode();
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity) : "";
                if (code == 200) {
                    return new JSONObject(body);
                } else {
                    throw new RemoteNodeException("Failed to get process log lines", NodeType.WORKER, code);
                }
            }
        } catch (Exception e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, code, e);
        } finally {
            logPoolStats("after GET process log lines, status=" + code);
        }
    }

    public JSONObject getWorkerInfo(Node node) {
        String url = node.getUrl() + "manager/info";
        HttpGet get = new HttpGet(url);
        int code = -1;
        logPoolStats("before GET " + url);
        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
            code = response.getCode();
            HttpEntity entity = response.getEntity();
            String body = entity != null ? EntityUtils.toString(entity) : "";
            if (code == 200) {
                return new JSONObject(body);
            } else {
                throw new RemoteNodeException("Failed to get worker info", NodeType.WORKER, code);
            }
        } catch (Exception e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, code, e);
        } finally {
            logPoolStats("after GET " + url + ", status=" + code);
        }

    }

    public JSONObject getWorkerInfo(String processId) {
        Node workerNode = getWorkerFromProcessId(processId);
        if (workerNode != null) {
            return getWorkerInfo(workerNode);
        } else return null;

//        String url = getWorkerBaseUrl(processId) + "manager/info";
//        HttpGet get = new HttpGet(url);
//
//        int code = -1;
//        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
//            code = response.getCode();
//            HttpEntity entity = response.getEntity();
//            String body = entity != null ? EntityUtils.toString(entity) : "";
//            if (code == 200) {
//                return new JSONObject(body);
//            } else {
//                throw new RemoteNodeException("Failed to get worker info", NodeType.WORKER, code);
//            }
//        } catch (Exception e) {
//            throw new RemoteNodeException(e.getMessage(), NodeType.WORKER, code, e);
//        }
    }

    public JSONObject getPoolStats() {
        JSONObject result = new JSONObject();
        result.put("configuration", configuration.toJson());
        result.put("total", poolStatsToJson(poolConnectionManager.getTotalStats()));
        return result;
    }

    private String getWorkerBaseUrl(String processId) {
        ProcessInfo processInfo = processService.getProcess(processId);
        Node node = nodeService.getNode(processInfo.getWorkerId());
        return node.getUrl();
    }

    private Node getWorkerFromProcessId(String processId) {
        ProcessInfo processInfo = processService.getProcess(processId);
        Node node = nodeService.getNode(processInfo.getWorkerId());
        return node;
    }

    private void logPoolStats(String event) {
        if (!LOGGER.isLoggable(Level.FINE)) {
            return;
        }
        PoolStats totalStats = poolConnectionManager.getTotalStats();
        LOGGER.fine(String.format(
                "WorkerClient pool [%s]: leased=%d, pending=%d, available=%d, max=%d",
                event,
                totalStats.getLeased(),
                totalStats.getPending(),
                totalStats.getAvailable(),
                totalStats.getMax()
        ));
    }

    private JSONObject poolStatsToJson(PoolStats stats) {
        JSONObject json = new JSONObject();
        json.put("leased", stats.getLeased());
        json.put("pending", stats.getPending());
        json.put("available", stats.getAvailable());
        json.put("max", stats.getMax());
        return json;
    }

    private static final class ResponseClosingInputStream extends FilterInputStream {

        private final CloseableHttpResponse response;
        private final Runnable closeCallback;

        private ResponseClosingInputStream(InputStream in, CloseableHttpResponse response, Runnable closeCallback) {
            super(in);
            this.response = response;
            this.closeCallback = closeCallback;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                response.close();
                closeCallback.run();
            }
        }
    }

}
