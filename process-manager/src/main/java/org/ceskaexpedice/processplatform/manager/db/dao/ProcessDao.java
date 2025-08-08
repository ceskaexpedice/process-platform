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
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.common.DataAccessException;
import org.ceskaexpedice.processplatform.common.model.BatchFilter;
import org.ceskaexpedice.processplatform.common.model.ProcessState;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;
import org.ceskaexpedice.processplatform.manager.db.JDBCQueryTemplate;
import org.ceskaexpedice.processplatform.manager.db.dao.mapper.ProcessMapper;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.sql.*;
import java.util.ArrayList;
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
                public boolean handleRow(ResultSet rs, List<ProcessEntity> returnsList) {
                    ProcessEntity processEntity = ProcessMapper.mapProcess(rs);
                    returnsList.add(processEntity);
                    return false;
                }
            }.executeQuery("select " + "*" + " from pcp_process p  where process_id = ?", processId);
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
            }.executeQuery("select " + "*" + " from pcp_process p  where status = ?", processState);
            return processEntities;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<ProcessEntity> getBatchHeaders(BatchFilter batchFilter, int offset, int limit) {
        QueryAndParams qp = buildBatchHeadersFilterQuery(batchFilter);

        String sql = qp.sql.replace("/*SELECT_PART*/", "b.*") +
                "ORDER BY b.planned DESC\nLIMIT ? OFFSET ?";

        List<Object> params = new ArrayList<>(qp.params);
        params.add(limit);
        params.add(offset);

        try (Connection connection = getConnection()) {
            return new JDBCQueryTemplate<ProcessEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<ProcessEntity> returnsList) {
                    returnsList.add(ProcessMapper.mapProcess(rs));
                    return true;
                }
            }.executeQuery(sql, params.toArray());
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public int countBatchHeaders(BatchFilter batchFilter) {
        QueryAndParams qp = buildBatchHeadersFilterQuery(batchFilter);

        String sql = qp.sql.replace("/*SELECT_PART*/", "COUNT(*)");

        try (Connection connection = getConnection()) {
            return new JDBCQueryTemplate<Integer>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<Integer> returnsList) throws SQLException {
                    returnsList.add(rs.getInt(1));
                    return false; // only one row
                }
            }.executeQuery(sql, qp.params.toArray())
                    .get(0);
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public List<ProcessEntity> getBatch(String batchId) {
        try (Connection connection = getConnection()) {
            List<ProcessEntity> processEntities = new JDBCQueryTemplate<ProcessEntity>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<ProcessEntity> returnsList) {
                    ProcessEntity processEntity = ProcessMapper.mapProcess(rs);
                    returnsList.add(processEntity);
                    return true;
                }
            }.executeQuery("select " + "*" + " from pcp_process p  where batch_id = ? ORDER BY planned", batchId);
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
                throw new ApplicationException(e.toString(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }

    }

    public boolean updateWorkerId(String processId, String workerId) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET worker_id = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, workerId);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public boolean updateState(String processId, ProcessState processState) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET status = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, processState.getVal());
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public boolean updatePid(String processId, int pid) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET pid = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, pid);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    public boolean updateDescription(String processId, String description) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE pcp_process SET description = ? WHERE process_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, description);
                stmt.setString(2, processId);
                int updated = stmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString(), e);
        }
    }

    private QueryAndParams buildBatchHeadersFilterQuery(BatchFilter batchFilter) {
        if(batchFilter == null) {
            batchFilter = new BatchFilter();
        };
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        boolean filterByState = batchFilter.getProcessState() != null;

        // Step 1: WITH clause only if needed
        if (filterByState) {
            sql.append("""
            WITH batch_states AS (
                SELECT
                    b.process_id AS main_process_id,
                    MAX(p.status) AS aggregated_state
                FROM pcp_process b
                JOIN pcp_process p
                  ON p.batch_id = b.process_id
                WHERE b.process_id = b.batch_id
                GROUP BY b.process_id
            )
            SELECT /*SELECT_PART*/
            FROM pcp_process b
            JOIN batch_states bs ON bs.main_process_id = b.process_id
            """);
        } else {
            sql.append("SELECT /*SELECT_PART*/ FROM pcp_process b\n");
        }

        // Step 2: Always filter for main processes
        sql.append("WHERE b.process_id = b.batch_id\n");

        // Step 3: Optional filters
        if (batchFilter.getOwner() != null) {
            sql.append("  AND b.owner = ?\n");
            params.add(batchFilter.getOwner());
        }
        if (batchFilter.getFrom() != null) {
            sql.append("  AND b.planned >= ?\n");
            params.add(Timestamp.valueOf(batchFilter.getFrom()));
        }
        if (batchFilter.getTo() != null) {
            sql.append("  AND b.finished <= ?\n");
            params.add(Timestamp.valueOf(batchFilter.getTo()));
        }
        if (filterByState) {
            sql.append("  AND bs.aggregated_state = ?\n");
            params.add(batchFilter.getProcessState().getVal()); // assuming enum -> int
        }

        QueryAndParams result = new QueryAndParams();
        result.sql = sql.toString();
        result.params = params;
        return result;
    }

    private static class QueryAndParams {
        String sql;            // SQL with /*SELECT_PART*/ placeholder
        List<Object> params;   // Parameters in correct order
    }
}
