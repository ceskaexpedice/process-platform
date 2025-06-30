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
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;

/**
 * ManagerClient
 * @author ppodsednik
 */
public class WorkerClient {
    private final ManagerConfiguration managerConfiguration;
    private final CloseableHttpClient closeableHttpClient;
    ObjectMapper mapper = new ObjectMapper();

    WorkerClient(ManagerConfiguration managerConfiguration) {

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


        this.managerConfiguration = managerConfiguration;
    }


}
