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
package org.ceskaexpedice.processplatform.manager.db.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ceskaexpedice.processplatform.common.TechnicalException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.JDBCQueryTemplate;
import org.ceskaexpedice.processplatform.manager.db.dao.mapper.ProcessMapper;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.sql.*;
import java.util.List;

/**
 * ProcessDao
 * @author ppodsednik
 */
public class ProcessDao extends AbstractDao{

    public ProcessDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        super(dbConnectionProvider,managerConfiguration);
    }

    public ProcessEntity getProcess(String processId) {
        try (Connection connection = getConnection()) {
            List<ProcessEntity> processes = new JDBCQueryTemplate<ProcessEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<ProcessEntity> returnsList) throws SQLException {
                    ProcessEntity processEntity = ProcessMapper.mapProcess(rs);
                    returnsList.add(processEntity);
                    return false;
                }
            }.executeQuery("select " + "*" + " from PCP_PROCESS p  where PROCESS_ID = ?", processId);
            return processes.size() == 1 ? processes.get(0) : null;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<ProcessEntity> getProcesses(int processState) {
        try (Connection connection = getConnection()) {
            List<ProcessEntity> processEntities = new JDBCQueryTemplate<ProcessEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<ProcessEntity> returnsList) {
                    ProcessEntity processEntity = ProcessMapper.mapProcess(rs);
                    returnsList.add(processEntity);
                    return true;
                }
            }.executeQuery("select " + "*" + " from PCP_PROCESS p  where STATUS = ?", processState);
            return processEntities;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void createProcess(ProcessEntity processEntity) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO pcp_process(" +
                    "process_id," +
                    "description," +
                    "profile_id," +
                    "worker_id," +
                    "pid," +
                    "planned," +
                    "started," +
                    "finished," +
                    "status," +
                    "payload," +
                    "batch_id," +
                    "owner" +
                    ")" +
                    "values " +
                    "  (" +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?," +
                    "    ?::jsonb," +
                    "    ?," +
                    "    ?" +
                    "  )";
            try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
                ProcessMapper.mapProcess(insertStatement, processEntity);
                insertStatement.executeUpdate();
            } catch (JsonProcessingException e) {
                throw new TechnicalException(e.toString(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }

    }

    public void updateWorkerId(String processId, String workerId) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET worker_id = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, workerId);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No process found with ID: " + processId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void updateState(String processId, ProcessState processState) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET status = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, processState.getVal());
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No process found with ID: " + processId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public void updatePid(String processId, int pid) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET pid = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, pid);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No process found with ID: " + processId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }
    public void updateDescription(String processId, String description) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET description = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, description);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("No process found with ID: " + processId);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

}
