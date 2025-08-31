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
package org.ceskaexpedice.processplatform.manager.db.dao.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

/**
 * ProcessMapper
 * @author ppodsednik
 */
public final class ProcessMapper {
    private ProcessMapper(){}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void mapProcess(PreparedStatement stmt, ProcessEntity processEntity) throws SQLException, JsonProcessingException {
        stmt.setString(1, processEntity.getProcessId());
        stmt.setString(2, processEntity.getDescription());
        stmt.setString(3, processEntity.getProfileId());
        stmt.setString(4, processEntity.getWorkerId());
        stmt.setInt(5, processEntity.getPid());
        if (processEntity.getPlanned() != null) {
            Timestamp timestamp = Timestamp.valueOf(processEntity.getPlanned());
            stmt.setTimestamp(6, timestamp);
        } else {
            stmt.setNull(6, java.sql.Types.TIMESTAMP);
        }
        if (processEntity.getStarted() != null) {
            Timestamp timestamp = Timestamp.valueOf(processEntity.getStarted());
            stmt.setTimestamp(7, timestamp);
        } else {
            stmt.setNull(7, java.sql.Types.TIMESTAMP);
        }
        if (processEntity.getFinished() != null) {
            Timestamp timestamp = Timestamp.valueOf(processEntity.getFinished());
            stmt.setTimestamp(8, timestamp);
        } else {
            stmt.setNull(8, java.sql.Types.TIMESTAMP);
        }
        stmt.setInt(9, processEntity.getStatus());
        String json = mapper.writeValueAsString(processEntity.getPayload());
        stmt.setString(10, json);
        stmt.setString(11, processEntity.getBatchId());
        stmt.setString(12, processEntity.getOwner());
    }

    public static ProcessEntity mapProcess(ResultSet rsProcess) {
        try {
            ProcessEntity processEntity = new ProcessEntity();
            processEntity.setProcessId(rsProcess.getString("process_id"));
            processEntity.setDescription(rsProcess.getString("description"));
            processEntity.setProfileId(rsProcess.getString("profile_id"));
            processEntity.setWorkerId(rsProcess.getString("worker_id"));
            processEntity.setPid(rsProcess.getInt("pid"));
            if(rsProcess.getTimestamp("planned") != null){
                processEntity.setPlanned(rsProcess.getTimestamp("planned").toLocalDateTime());
            }
            if(rsProcess.getTimestamp("started") != null){
                processEntity.setStarted(rsProcess.getTimestamp("started").toLocalDateTime());
            }
            if(rsProcess.getTimestamp("finished") != null){
                processEntity.setFinished(rsProcess.getTimestamp("finished").toLocalDateTime());
            }
            processEntity.setStatus(rsProcess.getInt("status"));
            String json = rsProcess.getString("payload");
            if(json != null){
                Map<String, String> payloadMap = mapper.readValue(json, new TypeReference<>() {});
                processEntity.setPayload(payloadMap);
            }
            processEntity.setBatchId(rsProcess.getString("batch_id"));
            processEntity.setOwner(rsProcess.getString("owner"));
            return processEntity;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.toString(), e);
        }
    }

}
