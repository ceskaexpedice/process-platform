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
package org.ceskaexpedice.processplatform.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.to.PluginInfoTO;
import org.ceskaexpedice.processplatform.common.to.ScheduledProcessTO;
import org.ceskaexpedice.processplatform.worker.config.WorkerConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class ManagerClient {
    private final WorkerConfiguration workerConfiguration;
    private final Client client;

    public ManagerClient(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
        this.client = ClientBuilder.newClient();
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
        }*/ return null;
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
