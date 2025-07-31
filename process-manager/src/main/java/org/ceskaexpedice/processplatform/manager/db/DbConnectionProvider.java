package org.ceskaexpedice.processplatform.manager.db;

import com.zaxxer.hikari.HikariDataSource;
import org.ceskaexpedice.processplatform.common.ApplicationException;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * DbConnectionProvider
 *
 * @author pavels
 */
public class DbConnectionProvider {

    private static final Logger LOGGER = Logger.getLogger(DbConnectionProvider.class.getName());
    private DataSource dataSource;

    public DbConnectionProvider(ManagerConfiguration config) {
        dataSource = createDataSource(config);
    }

    private static DataSource createDataSource(ManagerConfiguration config) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setJdbcUrl(config.getJdbcUrl());
        ds.setUsername(config.getJdbcUsername());
        ds.setPassword(config.getJdbcPassword());
        return ds;
    }

    public Connection get() {
        try {
            Connection connection = dataSource.getConnection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            return connection;
        } catch (Exception e) {
            throw new ApplicationException("Cannot get database connection from the pool.", e);
        }
    }

    public void close() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
