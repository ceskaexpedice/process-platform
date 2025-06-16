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
package org.ceskaexpedice.processplatform.worker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class ManagerClient {
    private final WorkerConfiguration workerConfiguration;
    private final CloseableHttpClient closeableHttpClient;
    ObjectMapper mapper = new ObjectMapper();

    public ManagerClient(WorkerConfiguration workerConfiguration) {

        //int connectTimeout = KConfiguration.getInstance().getConfiguration().getInt("cdk.forward.apache.client.connect_timeout", CONNECT_TIMEOUT);
        //int responseTimeout = KConfiguration.getInstance().getConfiguration().getInt("cdk.forward.apache.client.response_timeout", RESPONSE_TIMEOUT);
        PoolingHttpClientConnectionManager poolConnectionManager  = new PoolingHttpClientConnectionManager();
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

    public void registerPlugin(PluginInfoTO pluginInfoTO) {
        /*
        Response response = client
                .target(managerBaseUrl)
                .path("/plugins/register")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(pluginInfoTO));

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to register plugin: " + response.getStatus());
        }*/
    }

    public ScheduledProcessTO getNextProcess() {
        String url = "http://localhost:9998/process-manager/api/agent/next-process";
        HttpGet get = apacheGet(url, false);
        try (CloseableHttpResponse response = closeableHttpClient.execute(get)) {
            int code = response.getCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new IOException("Empty response body");
                }

                // Deserialize JSON to ScheduledProcessTO
                String json = EntityUtils.toString(entity);
                ScheduledProcessTO process = mapper.readValue(json, ScheduledProcessTO.class);

                return process;

            } else {
                // Handle non-200 status
                throw new IOException("Unexpected response code: " + code);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException("Failed to fetch next process", e);
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

    protected HttpGet apacheGet(String url, boolean headers) {
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

    // ManagerClient(WorkerConfiguration workerConfiguration) {
        /*
        services:
  process-worker:
    image: your-image-name
    environment:
      - MANAGER_BASE_URL=http://manager:8080/api
      - WORKER_ID=worker-1
      - MAX_TASKS=5
         */
    //String baseUrl = workerConfiguration.get("MANAGER_BASE_URL");
    //int maxTasks = workerConfiguration.getInt("MAX_TASKS", 1);

        /*
        for (Map.Entry<String, String> entry : env.entrySet()) {
    String key = entry.getKey().toLowerCase().replace('_', '.'); // e.g. MANAGER_BASE_URL → manager.base.url
    props.setProperty(key, entry.getValue());
}
config.get("manager.base.url")
         */
    //  }

    /*
    public List<Task> fetchTasks() {
        // Fake for demo. Replace with HTTP client call to manager REST endpoint.
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("import", "{ \"file\": \"data.csv\" }"));
        return tasks;
    }*/

    /*
    public Optional<TaskDto> fetchTask() {
        try {
            URL url = new URL("http://manager-service:8080/tasks/next");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                TaskDto task = mapper.readValue(conn.getInputStream(), TaskDto.class);
                return Optional.of(task);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

     */

    /*
    ProcessTask nextProcessTask() {
        try {
            String url = "http://manager-host:8080/api/get-process/" + "uuid";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new IOException("Failed to get process: HTTP " + conn.getResponseCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            try (InputStream is = conn.getInputStream()) {
                return mapper.readValue(is, ProcessTask.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

}
