package org.ceskaexpedice.processplatform.manager.db;

import com.zaxxer.hikari.HikariDataSource;
import org.ceskaexpedice.processplatform.manager.config.ManagerConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides connection to kramerius4 database
 *
 * @author pavels
 */
public class DbConnectionProvider {

    private static final Logger LOGGER = Logger.getLogger(DbConnectionProvider.class.getName());
//    private static DataSource dataSource = createDataSource();
    private DataSource dataSource;

    public DbConnectionProvider(ManagerConfiguration config) {
        dataSource = createDataSource(config);
    }

    private static DataSource createDataSource(ManagerConfiguration config) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
//        ds.setJdbcUrl(KConfiguration.getInstance().getJdbcUrl());
        ds.setJdbcUrl("jdbc:postgresql://localhost:15432/kramerius");
        ds.setUsername("fedoraAdmin");
//        ds.setUsername(KConfiguration.getInstance().getJdbcUserName());
        ds.setPassword("fedoraAdmin");
//        ds.setPassword(KConfiguration.getInstance().getJdbcUserPass());
//        ds.setLeakDetectionThreshold(KConfiguration.getInstance().getConfiguration().getInt("jdbcLeakDetectionThreshold"));
//        ds.setMaximumPoolSize(KConfiguration.getInstance().getConfiguration().getInt("jdbcMaximumPoolSize"));
//
//        ds.setConnectionTimeout(KConfiguration.getInstance().getConfiguration().getInt("jdbcConnectionTimeout"));
//
//        ds.setValidationTimeout(KConfiguration.getInstance().getConfiguration().getInt("jdbcValidationTimeout",30000));
//        ds.setIdleTimeout(KConfiguration.getInstance().getConfiguration().getInt("jdbcIdleTimeout",600000));
//        ds.setMaxLifetime(KConfiguration.getInstance().getConfiguration().getInt("jdbcMaxLifetime",1800000));
//
//        int datasourceSocketTimeout = KConfiguration.getInstance().getConfiguration().getInt("datasourceSocketTimeout",30);
//        ds.addDataSourceProperty("socketTimeout", datasourceSocketTimeout);
//        ds.setKeepaliveTime(120000);

        return ds;
    }

    public Connection get() {
        try {
            Connection connection = dataSource.getConnection();
//            connection.setTransactionIsolation(KConfiguration.getInstance().getConfiguration().getInt("jdbcDefaultTransactionIsolationLevel",Connection.TRANSACTION_READ_COMMITTED));  //reset the default level (Process Manager sets it to SERIALIZABLE)
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            return connection;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("Cannot get database connection from the pool.", e);
        }
    }

    public void close() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
