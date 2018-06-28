/**
 * Title:
 * <p>
 * Description:
 * <p>
 * Copyright: Copyright (c) Troy Thompson, Bob Byron
 * <p>
 * Company: JavaUnderground
 * <p>
 * 
 * @author Troy Thompson, Bob Byron
 * @version 1.1
 */
package com.rothsmith.javaunderground.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class provides methods to return a DebuggableStatement based on the
 * given parameters.
 * 
 * @author Troy Thompson, Bob Byron
 * 
 */
public final class StatementFactory {

	/**
	 * Default debug level.
	 */
	private static DebugLevel defaultDebug = DebugLevel.OFF;

	/**
	 * StatementFactory returns either a regular PreparedStatement or a
	 * DebuggableStatement class depending on the DebugLevel. If DebugLevel is
	 * OFF then a PreparedStatement is returned. If DebugLevel is ON or VERBOSE
	 * then a DebuggableStatement is returned. This minimizes overhead when
	 * debugging is not needed without effecting the code.
	 */
	private StatementFactory() {
	}

	/**
	 * Use this method if you want a class to override the global nature of a
	 * property file approach. This gives a class an option of a formatter and
	 * the debug value other than the global setting.
	 * 
	 * @param con
	 *            Connection to jdbc data source.
	 * @param stmt
	 *            sql statement that will be executed.
	 * @param formatter
	 *            BaseSqlFormatter that matches the database type (i.e.
	 *            OracleFormatter)
	 * @param debug
	 *            sets the debug level for this statement. DebugLevel can be
	 *            OFF, ON, VERBOSE
	 * @return PreparedStatement returns a DebuggableStatement if debug = ON or
	 *         VERBOSE. Returns a standard PreparedStatement if debug = OFF.
	 * @exception SQLException
	 *                thrown if problem with connection.
	 */
	public static PreparedStatement getStatement(final Connection con,
	    final String stmt, final SqlFormatter formatter, final DebugLevel debug)
	            throws SQLException {

		if (con == null) {
			throw new SQLException(
			    "Connection passed to StatementFactory is null");
		}

		PreparedStatement preparedStatement = null;

		if (debug == DebugLevel.OFF) {
			preparedStatement = con.prepareStatement(stmt);
		} else {
			preparedStatement =
			    new DebuggableStatement(con, stmt, formatter, debug);
		}

		return preparedStatement;

	}

	/**
	 * Use this if you want a class to override the global nature of a property
	 * file approach. This gives a class an option of a formatter other than the
	 * global setting.
	 * 
	 * @param con
	 *            Connection to jdbc data source.
	 * @param stmt
	 *            sql statement that will be executed.
	 * @param formatter
	 *            BaseSqlFormatter that matches the database type (i.e.
	 *            OracleFormatter)
	 * @return PreparedStatement returns a DebuggableStatement if debug = ON or
	 *         VERBOSE. Returns a standard PreparedStatement if debug = OFF.
	 * @exception SQLException
	 *                thrown if problem with connection.
	 */
	public static PreparedStatement getStatement(final Connection con,
	    final String stmt, final SqlFormatter formatter) throws SQLException {

		return StatementFactory.getStatement(con, stmt, formatter,
		    defaultDebug);

	}

	/**
	 * Use this if you want a class to override the global nature of a property
	 * file approach. This gives a class the option of turning debug code on or
	 * off no matter what the global value. This will not effect the global
	 * setting.
	 * 
	 * @param con
	 *            Connection to jdbc data source.
	 * @param stmt
	 *            sql statement that will be executed.
	 * @param debug
	 *            sets the debug level for this statement. DebugLevel can be
	 *            OFF, ON, VERBOSE
	 * @return PreparedStatement returns a DebuggableStatement if debug = ON or
	 *         VERBOSE. Returns a standard PreparedStatement if debug = OFF.
	 * @exception SQLException
	 *                thrown if problem with connection.
	 */
	public static PreparedStatement getStatement(final Connection con,
	    final String stmt, final DebugLevel debug) throws SQLException {

		SqlFormatter formatter =
		    FormatterFactory.getInstance().getFormatter(con);
		return StatementFactory.getStatement(con, stmt, formatter, debug);

	}

	/**
	 * this is the typical way to retrieve a statement. This method uses the
	 * static formatter and debug level.
	 * 
	 * @param con
	 *            Connection to jdbc data source.
	 * @param stmt
	 *            sql statement that will be executed.
	 * @return PreparedStatement returns a DebuggableStatement if debug = ON or
	 *         VERBOSE. Returns a standard PreparedStatement if debug = OFF.
	 * @exception SQLException
	 *                thrown if problem with connection.
	 */
	public static PreparedStatement getStatement(final Connection con,
	    final String stmt) throws SQLException {

		SqlFormatter formatter =
		    FormatterFactory.getInstance().getFormatter(con);
		return StatementFactory.getStatement(con, stmt, formatter,
		    defaultDebug);
	}

	/**
	 * typically set from property file so change is made in one place. default
	 * is to false which emulates a preparedstatement. This will change debug
	 * value in all places.
	 * 
	 * @param debug
	 *            sets the debug level for this statement. DebugLevel can be
	 *            OFF, ON, VERBOSE
	 */
	public static void setDefaultDebug(final DebugLevel debug) {
		defaultDebug = debug;
	}

}
