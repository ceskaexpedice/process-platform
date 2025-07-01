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
package org.ceskaexpedice.processplatform.manager.db;

import org.ceskaexpedice.processplatform.common.entity.PluginInfo;
import org.ceskaexpedice.processplatform.common.entity.PluginProfile;
import org.ceskaexpedice.processplatform.common.entity.ProcessState;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseProcessManager {


    public static final Logger LOGGER = Logger.getLogger(DatabaseProcessManager.class.getName());
    private final DbConnectionProvider dbConnectionProvider;


    private final Lock lock = new ReentrantLock();

    public DatabaseProcessManager(DbConnectionProvider dbConnectionProvider) {
        this.dbConnectionProvider = dbConnectionProvider;
    }

    public Connection getConnection() {
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

    public List<PluginInfo> getPlugins(String pluginId, List<PluginProfile> pluginProfiles) {
        lock.lock();
        try (Connection connection = getConnection()) {
            List<PluginInfo> processes = new JDBCQueryTemplate<PluginInfo>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginInfo> returnsList) throws SQLException {
                    PluginInfo pluginProfile = PluginMapper.mapPluginInfo(rs, pluginProfiles);
                    returnsList.add(pluginProfile);
                    return true;
                    /*
                    LRProcess lrProcess = processFromResultSet(rs);
                    if (lrProcess != null) {
                        returnsList.add(lrProcess);
                        return super.handleRow(rs, returnsList);
                    } else {
                        return true; //continue with other rows after ignoring this one
                    }*/
                }
            }.executeQuery("select " + "*" + " from PCP_PLUGIN p  where PLUGIN_ID = ?",
                    pluginId);
            return processes;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        } finally {
            lock.unlock();
        }
    }

    public List<PluginProfile> getProfiles(String profileId) {
        lock.lock();
        try (Connection connection = getConnection()) {
            List<PluginProfile> processes = new JDBCQueryTemplate<PluginProfile>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<PluginProfile> returnsList) throws SQLException {
                    PluginProfile pluginProfile = PluginMapper.mapPluginProfile(rs);
                    returnsList.add(pluginProfile);
                    return true;
                    /*
                    LRProcess lrProcess = processFromResultSet(rs);
                    if (lrProcess != null) {
                        returnsList.add(lrProcess);
                        return super.handleRow(rs, returnsList);
                    } else {
                        return true; //continue with other rows after ignoring this one
                    }*/
                }
            }.executeQuery("select " + "*" + " from PCP_PROFILE p  where PROFILE_ID = ?",
                    profileId);
            return processes;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        } finally {
            lock.unlock();
        }
    }

    public List<LRProcess> getLongRunningProcesses(ProcessState state) {
        lock.lock();
        try (Connection connection = getConnection()) {
            List<LRProcess> processes = new JDBCQueryTemplate<LRProcess>(connection) {
                @Override
                public boolean handleRow(ResultSet rs, List<LRProcess> returnsList) throws SQLException {
                    LRProcess lrProcess = processFromResultSet(rs);
                    if (lrProcess != null) {
                        returnsList.add(lrProcess);
                        return super.handleRow(rs, returnsList);
                    } else {
                        return true; //continue with other rows after ignoring this one
                    }
                }
            }.executeQuery("select " + ProcessDatabaseUtils.printQueryProcessColumns() + " from PROCESSES p  where STATUS = ?",
                    state.getVal());
            return processes;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        } finally {
            lock.unlock();
        }
    }

    private LRProcess processFromResultSet(ResultSet rs) throws SQLException {
        // CREATE TABLE PROCESSES(DEFID VARCHAR, UUID VARCHAR ,PID
        // VARCHAR,STARTED timestamp, STATUS int
        String definitionId = rs.getString("DEFID");
        String pid = rs.getString("PID");
        String uuid = rs.getString("UUID");
        int status = rs.getInt("STATUS");
        Timestamp planned = rs.getTimestamp("PLANNED");
        Timestamp started = rs.getTimestamp("STARTED");
        Timestamp finished = rs.getTimestamp("FINISHED");
        String name = rs.getString("PNAME");
        String params = rs.getString("PARAMS");
        String token = rs.getString("TOKEN");
        String authToken = rs.getString("AUTH_TOKEN");
        int startedBy = rs.getInt("STARTEDBY");
        String loginname = rs.getString("LOGINNAME");
        String firstname = rs.getString("FIRSTNAME");
        String surname = rs.getString("SURNAME");
        String userKey = rs.getString("USER_KEY");
        String paramsMapping = rs.getString("params_mapping");
        int batchStatus = rs.getInt("BATCH_STATUS");
        String ipAddr = rs.getString("IP_ADDR");


        /*
        LRProcessDefinition definition = this.lrpdm.getLongRunningProcessDefinition(definitionId);
        if (definition == null) {
            LOGGER.fine("cannot find definition '" + definitionId + "'");
            return null;
        }

        LRProcess process = definition.loadProcess(uuid, pid, planned != null ? planned.getTime() : 0, States.load(status), BatchStates.load(batchStatus), name);

        process.setGroupToken(token);
        process.setAuthToken(authToken);

        if (started != null)
            process.setStartTime(started.getTime());
        if (params != null) {
            String[] paramsArray = params.split(",");
            process.setParameters(Arrays.asList(paramsArray));
        }

        process.setFirstname(firstname);
        process.setSurname(surname);
        process.setLoginname(loginname);
        process.setLoggedUserKey(userKey);

        if (paramsMapping != null) {
            Properties props = PropertiesStoreUtils.loadProperties(paramsMapping);
            process.setParametersMapping(props);
        }

        if (finished != null) {
            process.setFinishedTime(finished.getTime());
        }
        if (ipAddr != null) {
            process.setPlannedIPAddress(ipAddr);
        }

        return process;

         */
        return null;
    }


}
