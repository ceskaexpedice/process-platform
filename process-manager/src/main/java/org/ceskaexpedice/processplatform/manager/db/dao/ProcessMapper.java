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
package org.ceskaexpedice.processplatform.manager.db.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class ProcessMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static ProcessEntity mapProcess(ResultSet rsProcess) {
        try {
            ProcessEntity processEntity = new ProcessEntity();
            processEntity.setProcessId(rsProcess.getString("process_id"));
            processEntity.setProfileId(rsProcess.getString("profile_id"));
            processEntity.setBatchId(rsProcess.getString("batch_id"));
            processEntity.setOwnerId(rsProcess.getString("owner"));

            String json = rsProcess.getString("payload");
            if(json != null){
                Map<String, String> payloadMap = mapper.readValue(json, new TypeReference<>() {});
                processEntity.setPayload(payloadMap);
            }
            return processEntity;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
