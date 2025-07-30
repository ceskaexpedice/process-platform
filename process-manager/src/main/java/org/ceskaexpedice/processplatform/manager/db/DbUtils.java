package org.ceskaexpedice.processplatform.manager.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DbUtils
 * @author petrp
 */
public class DbUtils {

    private static final Logger LOGGER = Logger.getLogger(DbUtils.class.getName());

    public static boolean tableExists(Connection con, String tableName) throws SQLException {
        String[] types = {"TABLE"};
        ResultSet rs = con.getMetaData().getTables(null, null, "%", types);
        try {
            while (rs.next()) {
                if (tableName.toUpperCase().equals(rs.getString("TABLE_NAME").toUpperCase())) {
                    return true;
                }
            }
            return false;
        } finally {
            tryClose(rs);
        }
    }

    public static void tryClose(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static void tryClose(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void tryClose(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
