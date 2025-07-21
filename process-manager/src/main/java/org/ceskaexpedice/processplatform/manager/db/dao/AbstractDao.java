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

import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;
import org.ceskaexpedice.processplatform.manager.db.DbConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * AbstractDao
 * @author petrp
 */
abstract class AbstractDao {
    private final DbConnectionProvider dbConnectionProvider;
    private final ManagerConfiguration managerConfiguration;

    AbstractDao(DbConnectionProvider dbConnectionProvider, ManagerConfiguration managerConfiguration) {
        this.dbConnectionProvider = dbConnectionProvider;
        this.managerConfiguration = managerConfiguration;
    }


    // TODO
    protected Connection getConnection() {
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
