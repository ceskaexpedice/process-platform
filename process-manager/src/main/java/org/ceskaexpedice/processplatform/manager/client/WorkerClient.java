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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.net.URIBuilder;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.RemoteNodeException;
import org.ceskaexpedice.processplatform.common.model.Node;
import org.ceskaexpedice.processplatform.common.model.NodeType;
import org.ceskaexpedice.processplatform.common.model.ProcessInfo;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class WorkerClient {

    private static final Logger LOGGER = Logger.getLogger(WorkerClient.class.getName());

    private final CloseableHttpClient closeableHttpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProcessService processService;
    private final NodeService nodeService;

    WorkerClient(ProcessService processService, NodeService nodeService) {
        PoolingHttpClientConnectionManager poolConnectionManager = new PoolingHttpClientConnectionManager();
        RequestConfig requestConfig = RequestConfig.custom().build();
        this.closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnectionManager)
                .disableAuthCaching()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .build();
        this.processService = processService;
        this.nodeService = nodeService;
    }

    public InputStream getProcessLog(String processId, boolean err) {
        String suffix = err ? "err" : "out";
        URIBuilder uriBuilder;
        HttpGet get;
        try {
            uriBuilder = new URIBuilder(getWorkerBaseUrl(processId) + "manager/" + processId + "/log/" +  suffix);
            URI uri = uriBuilder.build();
            get = new HttpGet(uri);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.toString(), e);
        }
        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
            int code = response.getCode();
            if (code == 200) {
                InputStream is = response.getEntity().getContent();
                return is;
            } else if(code == 404){
                return null;
            } else {
                throw new RemoteNodeException("Failed to get process log", NodeType.worker, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.manager, statusCode, e);
        }
    }

    private String mapToJson(Object to){
        String json;
        try {
            json = mapper.writeValueAsString(to);
            return json;
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

    private String getWorkerBaseUrl(String processId) {
        ProcessInfo processInfo = processService.getProcess(processId);
        Node node = nodeService.getNode(processInfo.getWorkerId());
        return node.getUrl();
    }

}
