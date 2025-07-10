/*
 * Copyright (C) 2012 Pavel Stastny
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.entity.*;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.DbUtils;
import org.ceskaexpedice.processplatform.manager.db.JDBCQueryTemplate;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ProcessDao {

    private static final Logger LOGGER = Logger.getLogger(ProcessDao.class.getName());
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;
    private static final ObjectMapper mapper = new ObjectMapper();

    public ProcessDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        this.dbConnectionProvider = dbConnectionProvider;
        this.managerConfiguration = managerConfiguration;
    }

    public List<ScheduledProcess> getScheduledProcesses() {
        try (Connection connection = getConnection()) {
            List<ScheduledProcess> processes = new JDBCQueryTemplate<ScheduledProcess>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<ScheduledProcess> returnsList) throws SQLException {
                    ScheduledProcess scheduledProcess = ProcessMapper.mapScheduledProcess(rs);
                    returnsList.add(scheduledProcess);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROCESS p  where STATUS = ?", ProcessState.PLANNED.getVal());
            return processes;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void createProcess(ScheduleMainProcess scheduleMainProcess) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_process(" +
                    "process_id," +
                    "profile_id," +
                    "planned," +
                    "status," +
                    "batch_id," +
                    "payload," +
                    "owner" +
                    ")" +
                    "values " +
                    "  (" +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?::jsonb," +
                    "    ?" +
                    "  )";
            try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
                insertStatement.setString(1, UUID.randomUUID().toString());
                insertStatement.setString(2, scheduleMainProcess.getProfileId());
                insertStatement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
                insertStatement.setInt(4, ProcessState.PLANNED.getVal());
                insertStatement.setString(5, UUID.randomUUID().toString());
                String json = mapper.writeValueAsString(scheduleMainProcess.getPayload());
                insertStatement.setString(6, json);
                insertStatement.setString(7, scheduleMainProcess.getOwnerId());
                insertStatement.executeUpdate();
            } catch (JsonProcessingException e) {
                throw new ApplicationException(e.toString(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }

    }

    // TODO move this to AbstractDao
    private Connection getConnection() {
        Connection connection = dbConnectionProvider.get();
        if (connection == null) {
            //throw new NotReadyException("connection not ready");
        }
        try {
//            connection.setTransactionIsolation(KConfiguration.getInstance().getConfiguration().getInt("jdbcProcessTransactionIsolationLevel", Connection.TRANSACTION_READ_COMMITTED));
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            //throw new NotReadyException("connection not ready - " + e);
        }
        return connection;
    }

}
