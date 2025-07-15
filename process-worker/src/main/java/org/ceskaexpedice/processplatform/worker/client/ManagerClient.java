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
import org.ceskaexpedice.processplatform.common.RemoteAgentException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.worker.config.EffectiveWorkerConfiguration;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class ManagerClient {

    public static final Logger LOGGER = Logger.getLogger(ManagerClient.class.getName());

    private final WorkerConfiguration workerConfiguration;
    private final CloseableHttpClient closeableHttpClient;
    ObjectMapper mapper = new ObjectMapper();

    ManagerClient(WorkerConfiguration workerConfiguration) {

        //int connectTimeout = KConfiguration.getInstance().getConfiguration().getInt("cdk.forward.apache.client.connect_timeout", CONNECT_TIMEOUT);
        //int responseTimeout = KConfiguration.getInstance().getConfiguration().getInt("cdk.forward.apache.client.response_timeout", RESPONSE_TIMEOUT);
        PoolingHttpClientConnectionManager poolConnectionManager = new PoolingHttpClientConnectionManager();
        //poolConnectionManager.setMaxTotal(maxConnections);
        //poolConnectionManager.setDefaultMaxPerRoute(maxRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                // .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                //  .setConnectTimeout(Timeout.ofSeconds(connectTimeout))
                //  .setResponseTimeout(Timeout.ofSeconds(responseTimeout))
                .build();

        this.closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolConnectionManager)
                .disableAuthCaching()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .build();


        this.workerConfiguration = workerConfiguration;
    }

    public void registerNode(Node node) {
        String managerUrl = new EffectiveWorkerConfiguration((workerConfiguration)).getManagerBaseUrl();
        String url = managerUrl + "agent/register-node";
        LOGGER.info("Registering node at " + url);

        HttpPost post = new HttpPost(url);
        String json;
        try {
            json = mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteAgentException("Failed to register node", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }

    public void registerPlugin(PluginInfo pluginInfo) {
        String managerUrl = new EffectiveWorkerConfiguration((workerConfiguration)).getManagerBaseUrl();
        String url = managerUrl + "agent/register-plugin";
        LOGGER.info("Registering plugin at " + url);

        HttpPost post = new HttpPost(url);
        String json;
        try {
            json = mapper.writeValueAsString(pluginInfo);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteAgentException("Failed to register plugin", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }

    public void scheduleSubProcess(ScheduleSubProcess scheduleSubProcess) {
        String url = getManageBaseUrl() + "agent/schedule-sub-process";
        LOGGER.info(String.format("Scheduling sub-process at %s", url));
        HttpPost post = new HttpPost(url);

        String json;
        try {
            json = mapper.writeValueAsString(scheduleSubProcess);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(post)) {
            statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 204) {
                throw new RemoteAgentException("Failed to register plugin", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }

    private String getManageBaseUrl() {
        return new EffectiveWorkerConfiguration(workerConfiguration).getManagerBaseUrl();
    }

    public ScheduledProcess getNextProcess() {
        URIBuilder uriBuilder;
        HttpGet get;
        try {
            uriBuilder = new URIBuilder(getManageBaseUrl() + "agent/next-process/" + workerConfiguration.get(WorkerConfiguration.WORKER_ID_KEY));
            URI uri = uriBuilder.build();
            get = new HttpGet(uri);
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.toString(), e);
        }
        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
            int code = response.getCode();
            if (code == 200 || code == 204) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return null;
                    //throw new IOException("Empty response body");
                }

                // Deserialize JSON to ScheduledProcessTO
                String json = EntityUtils.toString(entity);
                ScheduledProcess process = mapper.readValue(json, ScheduledProcess.class);

                return process;

            } else {
                // Handle non-200 status
                throw new RemoteAgentException("Unexpected response code", "manager", statusCode, null);
            }
        } catch (IOException | ParseException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }


//        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
//            int code = response.getCode();
//            if (code == 200) {
//                /*
//                long stop = System.currentTimeMillis();
//
//                if (granularTimeSnapshots != null) {
//                    granularTimeSnapshots.add(Triple.of(String.format("http/%s", this.getSource()), start,stop));
//                }*/
//
//               // LOGGER.log(Level.FINE, String.format(" -> code %d", code));
//                HttpEntity entity = response.getEntity();
//                long length = entity.getContentLength();
//                String responseMimeType = entity.getContentType();
//
//                //TODO: Jak kopirovat data
//                byte[] bytes = IOUtils.toByteArray(entity.getContent());
//                /*
//                if (dataConsumer != null) {
//                    dataConsumer.accept(bytes, responseMimeType);
//                }*/
//
//                StreamingOutput stream = new StreamingOutput() {
//                    public void write(OutputStream output) throws IOException, WebApplicationException {
//                        try {
//                            IOUtils.copy(new ByteArrayInputStream(bytes), output);
//                        } catch (Exception e) {
//                            throw new WebApplicationException(e);
//                        } finally {
//                            EntityUtils.consumeQuietly(entity);
//                        }
//                    }
//                };
//                Response.ResponseBuilder respEntity = null;
//                String mimetype = null;
//                if (mimetype != null) {
//                    respEntity = Response.status(200).entity(stream).type(mimetype);
//                } else if (responseMimeType != null) {
//                    respEntity = Response.status(200).entity(stream).type(responseMimeType);
//                } else {
//                    respEntity = Response.status(200).entity(stream);
//                }
//                long contentLength = entity.getContentLength();
//                if (contentLength >= 0) {
//                    respEntity.header("Content-Length", String.valueOf(contentLength));
//                }
//                Response build = respEntity.build();
//                return null;
//            } else {
//                // event for reharvest
//                if (code == 404) {
//                   // if (deleteTrigger && this.deleteTriggerSupport != null) {
//                     //   this.deleteTriggerSupport.executeDeleteTrigger(pid);
//                   // }
//                }
//                Response build = Response.status(code).build();
//                return null;
//            }
//        } catch (IOException e) {
//            //LOGGER.log(Level.SEVERE, e.getMessage(), e);
//            Response build = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//            return null;
//        }

        /*
        Response response = client
                .target(managerBaseUrl)
                .path("/processes/next")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(ScheduledProcessTO.class);
        } else if (response.getStatus() == 204) {
            return null;
        } else {
            throw new RuntimeException("Failed to get next process: " + response.getStatus());
        }*/
    }

    public void updateProcessPid(String processId, String pid) {
        String url = String.format("%sagent/pid/%s?pid=%s", workerConfiguration.get(WorkerConfiguration.MANAGER_BASE_URL_KEY), processId, pid);
        HttpPut httpPut = new HttpPut(url);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteAgentException("Failed to update PID", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }

    public void updateProcessName(String processId, String name) {
        String url = String.format("%sagent/name/%s?name=%s", workerConfiguration.get(WorkerConfiguration.MANAGER_BASE_URL_KEY), processId, name);
        HttpPut httpPut = new HttpPut(url);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteAgentException("Failed to update PID", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }

    public void updateProcessState(String processId, ProcessState state) {
        String url = String.format("%sagent/state/%s?state=%s", workerConfiguration.get(WorkerConfiguration.MANAGER_BASE_URL_KEY), processId, state);
        HttpPut httpPut = new HttpPut(url);

        int statusCode = -1;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPut)) {
            statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RemoteAgentException("Failed to update PID", "manager", statusCode, null);
            }
        } catch (IOException e) {
            throw new RemoteAgentException(e.getMessage(), "manager", statusCode, e);
        }
    }


    private HttpGet apacheGet(String url, boolean headers) {
        //LOGGER.fine(String.format("Requesting %s", url));
        HttpGet get = new HttpGet(url);
        /*
        get.setHeader("User-Agent", "CDK/1.0");
        if (headers && isAuthenticated() && isDnntUser()) {
            String header = prepareHeader(headers);
            get.setHeader("CDK_TOKEN_PARAMETERS", header);
        }*/
        return get;
    }

}
