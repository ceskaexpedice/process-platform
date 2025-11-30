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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.RemoteNodeException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class ManagerClient {

    private static final Logger LOGGER = Logger.getLogger(ManagerClient.class.getName());

    private final WorkerConfiguration workerConfiguration;
    private final CloseableHttpClient closeableHttpClient;
    private ObjectMapper mapper = new ObjectMapper();

    ManagerClient(WorkerConfiguration workerConfiguration) {
        PoolingHttpClientConnectionManager poolConnectionManager = new PoolingHttpClientConnectionManager();
        RequestConfig requestConfig = RequestConfig.custom().build();
        this.closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnectionManager)
                .disableAuthCaching()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .build();
        this.workerConfiguration = workerConfiguration;
    }

    public void registerNode(Node node) {
        String url = workerConfiguration.getManagerBaseUrl() + "worker/register_node";
        LOGGER.info("Registering node at " + url);

        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(mapToJson(node), ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteNodeException("Failed to register node", NodeType.MANAGER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
        }
    }

    public void registerPlugin(PluginInfo pluginInfo) {
        String url = workerConfiguration.getManagerBaseUrl() + "worker/register_plugin";
        LOGGER.info("Registering plugin at " + url);

        String s = mapToJson(pluginInfo);
        LOGGER.info(String.format("Plugin info  %s" , s));


        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(mapToJson(pluginInfo), ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteNodeException("Failed to register plugin", NodeType.MANAGER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
        }
    }

    public ScheduledProcess getNextScheduledProcess() {
        URIBuilder uriBuilder;
        HttpGet get;
        try {
            uriBuilder = new URIBuilder(workerConfiguration.getManagerBaseUrl() + "worker/next_process/" + workerConfiguration.getWorkerId());
            URI uri = uriBuilder.build();
            LOGGER.info("Getting next scheduled process at " + uri);
            get = new HttpGet(uri);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.toString(), e);
        }
        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
            int code = response.getCode();
            LOGGER.info(String.format("Returning status code from process manager %d", code));
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                ScheduledProcess process = mapper.readValue(json, ScheduledProcess.class);
                return process;
            } else if(code == 404){
                LOGGER.warning(String.format("Not found %d", code));
                return null;
            } else {
                throw new RemoteNodeException("Failed to get next scheduled process", NodeType.MANAGER, statusCode);
            }
        } catch (IOException | ParseException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
        }
    }

    public void scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        String url = workerConfiguration.getManagerBaseUrl() + "worker/schedule_sub_process";
        LOGGER.info(String.format("Scheduling sub-process at %s", url));
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(mapToJson(scheduleSubProcess), ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteNodeException("Failed to schedule sub process", NodeType.MANAGER, statusCode);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
        }
    }

    public void updateProcessPid(String processId, String pid) {
        String url = String.format("%sworker/pid/%s?pid=%s", workerConfiguration.getManagerBaseUrl(), processId, pid);
        LOGGER.info(String.format("Update process pid at %s", url));
        HttpPut httpPut = new HttpPut(url);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteNodeException("Failed to update PID", NodeType.MANAGER, statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
        }
    }

    public void updateProcessDescription(String processId, String description) {
        try {
            String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8);
            String url = String.format(
                    "%sworker/description/%s?description=%s",
                    workerConfiguration.getManagerBaseUrl(),
                    processId,
                    encodedDescription
            );

            LOGGER.info(String.format("Update process description at %s", url));
            HttpPut httpPut = new HttpPut(url);

            int statusCode = -1;
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
                statusCode = response.getCode();
                if (statusCode != 200) {
                    throw new RemoteNodeException("Failed to update name", NodeType.MANAGER, statusCode, null);
                }
            } catch (IOException e) {
                throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode URL", e);
        }
    }

    public void updateProcessState(String processId, ProcessState state) {
        String url = String.format("%sworker/state/%s?state=%s", workerConfiguration.getManagerBaseUrl(), processId, state);
        LOGGER.info(String.format("Update process state at %s", url));
        HttpPut httpPut = new HttpPut(url);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteNodeException("Failed to update state", NodeType.MANAGER, statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteNodeException(e.getMessage(), NodeType.MANAGER, statusCode, e);
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

}
