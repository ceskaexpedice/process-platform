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
package org.ceskaexpedice.processplatform.manager.api.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.entity.PayloadFieldSpec;
import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.common.entity.ScheduledProcess;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class ProcessMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static ScheduledProcess mapScheduledProcess(ResultSet rsProcess) {
        try {
            ScheduledProcess scheduledProcess = new ScheduledProcess();
            scheduledProcess.setProcessId(rsProcess.getString("process_id"));
            scheduledProcess.setProfileId(rsProcess.getString("profile_id"));
            scheduledProcess.setBatchId(rsProcess.getString("batch_id"));
            scheduledProcess.setOwnerId(rsProcess.getString("owner"));

            String json = rsProcess.getString("payload");
            if(json != null){
                Map<String, String> payloadMap = mapper.readValue(json, new TypeReference<>() {});
                scheduledProcess.setPayload(payloadMap);
            }
            return scheduledProcess;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
