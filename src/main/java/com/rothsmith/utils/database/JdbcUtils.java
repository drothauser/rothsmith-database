/**
 * 
 */
package com.rothsmith.utils.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of JDBC helper methods that don't exist in commons
 * <a href='http://commons.apache.org/dbutils'>DbUtils</a>.
 * 
 * @author drothauser
 * 
 */
public final class JdbcUtils {

	/**
	 * SLF4J Logger for JdbcUtils.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JdbcUtils.class);

	/**
	 * Null-safe operation for performing a rollback JDBC {@link Connection}. In
	 * addition for checking a null {@link Connection}, it checks whether
	 * autocommit has been set on or not.
	 * 
	 * @param conn
	 *            JDBC {@link Connection}
	 * @return true if the operation was successful, else false
	 */
	public static boolean rollbackQuietly(Connection conn) {
		boolean ok = false;
		if (conn != null) {
			try {
				if (!conn.getAutoCommit()) {
					conn.rollback();
					ok = true;
				}
			} catch (SQLException e) {
				LOGGER.error("Unable to rollback: " + e, e);
			}
		}
		return ok;
	}

	/**
	 * Null-safe operation for setting autocommit on or off on a JDBC
	 * {@link Connection}. This method will not throw an exception. Rather, it
	 * will return a true or false depending on whether the operation was
	 * successful or not.
	 * 
	 * @param conn
	 *            JDBC {@link Connection}
	 * @param autoCommit
	 *            boolean - true for on, false for off
	 * @return true if the operation was successful, else false
	 */
	public static boolean setAutoCommit(Connection conn, boolean autoCommit) {
		boolean ok = false;
		if (conn != null) {
			try {
				conn.setAutoCommit(autoCommit);
				ok = true;
			} catch (SQLException e) {
				LOGGER.error("Unable to set AutoCommit: " + e, e);
			}
		}
		return ok;
	}

	/**
	 * Private constructor to thwart instantiation.
	 */
	private JdbcUtils() {
	}
}
