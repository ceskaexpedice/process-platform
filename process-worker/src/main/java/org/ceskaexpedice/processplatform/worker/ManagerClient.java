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
import org.ceskaexpedice.processplatform.common.dto.ProcessTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManagerClient {

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

    public ProcessTask nextProcess() {
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
    }
}
