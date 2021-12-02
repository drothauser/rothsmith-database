/**
 * Title:
 * <p>
 * Description:
 * <p>
 * Copyright: Copyright (c) Troy Thompson Bob Byron
 * <p>
 * Company: JavaUnderground
 * <p>
 * 
 * @author Troy Thompson Bob Byron
 * @version 1.1
 */
package net.rothsmith.javaunderground.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PreparedStatements have no way to retrieve the statement that was executed on
 * the database. This is due to the nature of prepared statements, which are
 * database driver specific. This class proxies for a PreparedStatement and
 * creates the SQL string that is created from the sets done on the
 * PreparedStatement.
 * <p>
 * Some of the objects such as blob, clob, and Ref are only represented as
 * Strings and are not the actual objects populating the database. Array is
 * represented by the object type within the array.
 * 
 * Example code:
 * 
 * <pre>
 * int payPeriod = 1; 
 * String name = &quot;Troy Thompson&quot;; 
 * ArrayList employeePay = new ArrayList(); 
 * ResultSet rs = null; 
 * PreparedStatement ps = null; 
 * Connection con = null; 
 * try {
 *    Class.forName(&quot;sun.jdbc.odbc.JdbcOdbcDriver&quot;); 
 *    String url = jdbc:odbc:Employee&quot;; 
 *    con = DriverManager.getConnection(url); 
 *    String sql = &quot;SELECT e.name,e.employee_number,e.pay_rate,&quot;
 *    + &quot; e.type,e.hire_date,h.pay_period,h.hours,h.commissions&quot;
 *    + &quot; FROM Employee_tbl e,hours_tbl h &quot;
 *    + &quot; WHERE h.pay_period = ?&quot;
 *    + &quot; AND e.name = ?&quot;
 *    + &quot; AND h.employee_number = e.employee_number&quot;;
 *    // &lt;-- insert this to debug 
 *    ps = StatementFactory.getStatement(con,sql);  
 *    //ps = con.prepareStatement(sql); 
 *    ps.setInt(1,payPeriod); 
 *    ps.setString(2,name); 
 *    if (LOG.isDebugEnable()) {    
 *       LOG.debug(&quot; debuggable statement= &quot; 
 *          + ps.toString());
 *    }       
 *    rs = ps.executeQuery(); 
 * } catch (SQLException e) {
 *    e.printStackTrace(); 
 * } catch (ClassNotFoundException ce) {
 *    ce.printStackTrace();
 * } finally { 
 *    try { 
 *       if (rs != null) {
 *           rs.close();
 *       } 
 *       if (ps != null) {
 *          ps.close();
 *       }
 *       if (!con.isClosed()) {
 *          con.close(); 
 *       }
 *    catch (SQLException e) { 
 *       e.printStackTrace();
 *    } 
 * }
 * </pre>
 * 
 * </p>
 * ***** notes *****<br>
 * One of the main differences between databases is how they handle dates/times.
 * Since we use Oracle, the debug string for Dates, Times, Timestamps are using
 * an Oracle specific BaseSqlFormatter called OracleSqlFormatter.
 * 
 * The following is in our debug class:
 * 
 * <pre>
 * static {
 *     StatementFactory.setDefaultDebug(DebugLevel.ON);
 *     StatementFactory.setDefaultFormatter(new OracleSqlFormatter());
 * }
 * </pre>
 * 
 * @author Troy Thompson, Bob Byron
 * 
 */
@SuppressWarnings("checkstyle:methodcount")
public class DebuggableStatement implements PreparedStatement {

	/**
	 * SLF4J Logger for DebuggableStatement.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(DebuggableStatement.class);

	/**
	 * Initial JDBC prepared statment length = 512.
	 */
	private static final int INITIAL_STMT_LEN = 512;

	/**
	 * Concrete String constant to replace null objects.
	 */
	private static final String NULL = "NULL";

	/**
	 * DebugObject class.
	 * 
	 */
	private static class DebugObject {

		/**
		 * debugObject.
		 */
		private Object debugObject;

		/**
		 * value assigned.
		 */
		private boolean valueAssigned;

		/**
		 * Construct a new {@link DebugObject}.
		 * 
		 * @param debugObject
		 *            object to be debugged.
		 */
		public DebugObject(final Object debugObject) {
			this.debugObject = debugObject;
			valueAssigned = true;
		}

		/**
		 * @return debugObject
		 */
		public Object getDebugObject() {
			return debugObject;
		}

		/**
		 * @return valueAssigned boolean
		 */
		public boolean isValueAssigned() {
			return valueAssigned;
		}
	}

	/**
	 * level of debug
	 */
	private final DebugLevel debugLevel;

	/**
	 * time elapsed while executing statement
	 */
	private long executeTime;

	/**
	 * statement filtered for rogue '?' that are not bind variables.
	 */
	private final String filteredSql;

	/**
	 * format for dates
	 */
	private SqlFormatter formatter;

	/**
	 * preparedStatement being proxied for.
	 */
	private final PreparedStatement ps;

	/**
	 * original statement going to database.
	 */
	private final String sql;

	/**
	 * time that statement began execution
	 */
	private long startTime;

	/**
	 * array of bind variables
	 */
	private DebugObject[] variables;

	/**
	 * Construct new DebugableStatement. Uses the BaseSqlFormatter to format
	 * date, time, timestamp outputs.
	 * 
	 * @param con
	 *            Connection to be used to construct PreparedStatement
	 * @param sqlStatement
	 *            sql statement to be sent to database.
	 * @param formatter
	 *            a {@link SqlFormatter} object
	 * @param debugLevel
	 *            DebugLevel can be ON, OFF, VERBOSE.
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	protected DebuggableStatement(final Connection con,
	    final String sqlStatement, final SqlFormatter formatter,
	    final DebugLevel debugLevel) throws SQLException {
		// set values for member variables
		if (con == null) {
			throw new SQLException("Connection object is null");
		}
		this.ps = con.prepareStatement(sqlStatement);
		this.sql = sqlStatement;
		this.debugLevel = debugLevel;
		this.formatter = formatter;

		// see if there are any '?' in the statement that are not bind
		// variables
		// and filter them out.
		boolean isString = false;
		char[] sqlString = sqlStatement.toCharArray();
		for (int i = 0; i < sqlString.length; i++) {
			if (sqlString[i] == '\'') {
				isString = !isString;
			}
			// substitute the ? with an unprintable character if the ? is in a
			// string.
			if (sqlString[i] == '?' && isString) {
				sqlString[i] = '\u0007';
			}
		}
		filteredSql = new String(sqlString);

		// find out how many variables are present in statement.
		int count = 0;
		int index = -1;
		while ((index = filteredSql.indexOf("?", index + 1)) != -1) {
			count++;
		}

		// show how many bind variables found
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("count= " + count);
		}

		// create array for bind variables
		variables = new DebugObject[count];

	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void addBatch() throws SQLException {
		ps.addBatch();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @param sql
	 *            the SQL
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void addBatch(final String sql) throws SQLException {
		ps.addBatch(sql);
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void cancel() throws SQLException {
		ps.cancel();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void clearBatch() throws SQLException {
		ps.clearBatch();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void clearParameters() throws SQLException {
		ps.clearParameters();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void clearWarnings() throws SQLException {
		ps.clearWarnings();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void close() throws SQLException {
		ps.close();
	}

	/**
	 * Set ending time.
	 */
	private void end() {
		executeTime = System.currentTimeMillis() - startTime;
	}

	/**
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 * @return true is query succeeds, else false
	 */
	public final boolean execute() throws SQLException {
		// execute query
		Boolean results = null;
		try {
			results = (Boolean) executeVerboseQuery("execute", null);
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage());
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}
		return results.booleanValue();
	}

	/**
	 * This method is only here for convenience. If a different sql string is
	 * executed than was passed into Debuggable, unknown results will occur.
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @param sql
	 *            should be same string that was passed into Debuggable
	 * @return true is query succeeds, else false
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final boolean execute(final String sql) throws SQLException {
		// execute query
		Boolean results = null;
		try {
			results = (Boolean) executeVerboseQuery("execute",
			    new Class[] { sql.getClass() });
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage());
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}
		return results.booleanValue();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean execute(final String sql, final int autoGeneratedKeys)
	        throws SQLException {
		return ps.execute(sql, autoGeneratedKeys);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean execute(final String sql, final int[] columnIndexes)
	        throws SQLException {
		return ps.execute(sql, columnIndexes);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean execute(final String sql, final String[] columnIndexes)
	        throws SQLException {
		return ps.execute(sql, columnIndexes);
	}

	/**
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @return query counts
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final int[] executeBatch() throws SQLException {
		// execute query
		int[] results = null;
		try {
			results = (int[]) executeVerboseQuery("executeBatch", null);
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage(),
			    e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}
		return results;
	}

	/**
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @return results of query
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final ResultSet executeQuery() throws SQLException {
		// execute query
		ResultSet results = null;
		try {

			results = (ResultSet) executeVerboseQuery("executeQuery", null);

		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage(),
			    e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}

		return results;
	}

	/**
	 * This method is only here for convenience. If a different sql string is
	 * executed than was passed into Debuggable, unknown results will occur.
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @param sql
	 *            should be same string that was passed into Debuggable
	 * @return results of query
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final ResultSet executeQuery(final String sql) throws SQLException {
		// execute query
		ResultSet results = null;
		try {
			results = (ResultSet) executeVerboseQuery("executeQuery",
			    new Class[] { sql.getClass() });
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage(),
			    e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}

		return results;
	}

	/**
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @return results of query
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final int executeUpdate() throws SQLException {
		// execute query
		Integer results = null;
		try {
			results = (Integer) executeVerboseQuery("executeUpdate", null);
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage(),
			    e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}
		return results.intValue();
	}

	/**
	 * This method is only here for convenience. If a different sql string is
	 * executed than was passed into Debuggable, unknown results will occur.
	 * Executes query and Calculates query execution time if DebugLevel =
	 * VERBOSE.
	 * 
	 * @param sql
	 *            should be same string that was passed into Debuggable
	 * @return results of query
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final int executeUpdate(final String sql) throws SQLException {
		// execute query
		Integer results = null;
		try {
			results = (Integer) executeVerboseQuery("executeUpdate",
			    new Class[] { sql.getClass() });
		} catch (InvocationTargetException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: "
			        + e.getTargetException().getMessage(),
			    e);
		} catch (NoSuchMethodException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		} catch (IllegalAccessException e) {
			throw new SQLException(
			    "Could not execute sql command - Original message: " + e);
		}
		return results.intValue();
	}

	/** {@inheritDoc} */
	@Override
	public final int executeUpdate(final String sql, int autoGeneratedKeys)
	        throws SQLException {
		return ps.executeUpdate(sql, autoGeneratedKeys);
	}

	/** {@inheritDoc} */
	@Override
	public final int executeUpdate(final String arg0, final int[] arg1)
	        throws SQLException {
		return ps.executeUpdate(arg0, arg1);
	}

	/** {@inheritDoc} */
	@Override
	public final int executeUpdate(final String sql, final String[] columnNames)
	        throws SQLException {
		return ps.executeUpdate(sql, columnNames);
	}

	/**
	 * This method executes a parameterized SQL statement that will replace
	 * parameter placeholders (?) with their formatted values.
	 * 
	 * @param methodName
	 *            {@link PreparedStatement} method name
	 * @param parameters
	 *            query parameters
	 * @return the {@link Object} returned from the JDBC
	 *         {@link java.sql.Statement}.
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 * @throws NoSuchMethodException
	 *             thrown if the method isn't defined in the
	 *             {@link java.sql.Statement} class.
	 * @throws InvocationTargetException
	 *             thrown if the {@link java.sql.Statement} object doesn't
	 *             exist.
	 * @throws IllegalAccessException
	 *             thrown if the method is not accessible
	 * 
	 */
	private Object executeVerboseQuery(final String methodName,
	    final Class<?>[] parameters) throws SQLException, NoSuchMethodException,
	            InvocationTargetException, IllegalAccessException {
		// determine which method we have
		Method m = ps.getClass().getDeclaredMethod(methodName, parameters);

		// debug is set to on, so no times are calculated
		if (debugLevel == DebugLevel.ON) {
			return m.invoke(ps, (Object[]) parameters);
		}

		// calculate execution time for verbose debugging
		start();
		Object returnObject = m.invoke(ps, (Object[]) parameters);
		end();

		// return the executions return type
		return returnObject;
	}

	/** {@inheritDoc} */
	public final Connection getConnection() throws SQLException {
		return ps.getConnection();
	}

	/** {@inheritDoc} */
	public final int getFetchDirection() throws SQLException {
		return ps.getFetchDirection();
	}

	/**
	 * Facade for PreparedStatement.
	 * 
	 * @return fetch size
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final int getFetchSize() throws SQLException {
		return ps.getFetchSize();
	}

	/** {@inheritDoc} */
	@Override
	public final ResultSet getGeneratedKeys() throws SQLException {
		return ps.getGeneratedKeys();
	}

	/** {@inheritDoc} */
	public final int getMaxFieldSize() throws SQLException {
		return ps.getMaxFieldSize();
	}

	/** {@inheritDoc} */
	public final int getMaxRows() throws SQLException {
		return ps.getMaxRows();
	}

	/** {@inheritDoc} */
	public final ResultSetMetaData getMetaData() throws SQLException {
		return ps.getMetaData();
	}

	/** {@inheritDoc} */
	public final boolean getMoreResults() throws SQLException {
		return ps.getMoreResults();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean getMoreResults(final int current) throws SQLException {
		return ps.getMoreResults(current);
	}

	/** {@inheritDoc} */
	@Override
	public final ParameterMetaData getParameterMetaData() throws SQLException {
		return ps.getParameterMetaData();
	}

	/** {@inheritDoc} */
	public final int getQueryTimeout() throws SQLException {
		return ps.getQueryTimeout();
	}

	/** {@inheritDoc} */
	public final ResultSet getResultSet() throws SQLException {
		return ps.getResultSet();
	}

	/** {@inheritDoc} */
	public final int getResultSetConcurrency() throws SQLException {
		return ps.getResultSetConcurrency();
	}

	/** {@inheritDoc} */
	@Override
	public final int getResultSetHoldability() throws SQLException {
		return ps.getResultSetHoldability();
	}

	/** {@inheritDoc} */
	public final int getResultSetType() throws SQLException {
		return ps.getResultSetType();
	}

	/**
	 * @return SQL statement
	 */
	public final String getStatement() {
		return sql;
	}

	/** {@inheritDoc} */
	public final int getUpdateCount() throws SQLException {
		return ps.getUpdateCount();
	}

	/** {@inheritDoc} */
	public final SQLWarning getWarnings() throws SQLException {
		return ps.getWarnings();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isClosed() throws SQLException {
		return ps.isClosed();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPoolable() throws SQLException {
		return ps.isPoolable();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ps.isWrapperFor(iface);
	}

	/**
	 * Tests Object o for parameterIndex (which parameter is being set) and
	 * places object in array of variables.
	 * 
	 * @param parameterIndex
	 *            which PreparedStatement parameter is being set. Sequence
	 *            begins at 1.
	 * @param object
	 *            Object being stored as parameter
	 * @throws ParameterIndexOutOfBoundsException
	 *             Thrown if index exceeds number of variables.
	 */
	private void saveObject(final int parameterIndex, final Object object)
	        throws ParameterIndexOutOfBoundsException {
		if (parameterIndex > variables.length) {
			throw new ParameterIndexOutOfBoundsException(
			    "Parameter index of " + parameterIndex
			        + " exceeds actual parameter count of " + variables.length);
		}

		variables[parameterIndex - 1] = new DebugObject(object);
	}

	/**
	 * Adds name of the Array's internal class type(by using
	 * x.getBaseTypeName()) to the debug String. If x is null, NULL is added to
	 * debug String.
	 * 
	 * @param inx
	 *            index of parameter
	 * @param array
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a problem setting the Array.
	 */
	public final void setArray(final int inx, final java.sql.Array array)
	        throws SQLException {
		saveObject(inx, array);
		ps.setArray(inx, array);
	}

	/** {@inheritDoc} */
	@Override
	public final void setAsciiStream(final int parameterIndex,
	    final InputStream is) throws SQLException {
		ps.setAsciiStream(parameterIndex, is);
	}

	/**
	 * Debug string prints NULL if InputStream is null, or adds
	 * "stream length = " + length.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param is
	 *            the Java input stream that contains the ASCII parameter value
	 * @param length
	 *            the number of bytes in the stream
	 * @throws SQLException
	 *             thrown if a problem setting the {@link InputStream}.
	 */
	public final void setAsciiStream(final int parameterIndex,
	    final InputStream is, final int length) throws SQLException {
		saveObject(parameterIndex,
		    (is == null ? NULL : "<stream length= " + length + ">"));
		ps.setAsciiStream(parameterIndex, is, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setAsciiStream(final int parameterIndex,
	    final InputStream is, final long length) throws SQLException {
		ps.setAsciiStream(parameterIndex, is, length);
	}

	/**
	 * Adds BigDecimal to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param num
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setBigDecimal(int parameterIndex, BigDecimal num)
	        throws SQLException {
		saveObject(parameterIndex, num);
		ps.setBigDecimal(parameterIndex, num);
	}

	/** {@inheritDoc} */
	@Override
	public final void setBinaryStream(final int parameterIndex,
	    final InputStream is) throws SQLException {
		ps.setBinaryStream(parameterIndex, is);
	}

	/**
	 * Debug string prints NULL if InputStream is null, or adds
	 * "stream length= " + length.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param is
	 *            parameter Object
	 * @param length
	 *            length of InputStream
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setBinaryStream(final int parameterIndex,
	    final InputStream is, final int length) throws SQLException {
		saveObject(parameterIndex,
		    (is == null ? NULL : "<stream length= " + length + ">"));
		ps.setBinaryStream(parameterIndex, is, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setBinaryStream(final int parameterIndex,
	    final InputStream is, final long length) throws SQLException {
		ps.setBinaryStream(parameterIndex, is, length);
	}

	/**
	 * Adds name of the object's class type(Blob) to the debug String. If object
	 * is null, NULL is added to debug String.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param blob
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setBlob(final int parameterIndex, final Blob blob)
	        throws SQLException {
		saveObject(parameterIndex, blob);
		ps.setBlob(parameterIndex, blob);
	}

	/** {@inheritDoc} */
	@Override
	public final void setBlob(final int parameterIndex,
	    final InputStream inputStream) throws SQLException {
		ps.setBlob(parameterIndex, inputStream);
	}

	/** {@inheritDoc} */
	@Override
	public final void setBlob(final int parameterIndex,
	    final InputStream inputStream, final long length) throws SQLException {
		ps.setBlob(parameterIndex, inputStream, length);
	}

	/**
	 * Adds boolean to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param bool
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a problem setting the Boolean value.
	 */
	public final void setBoolean(final int parameterIndex, final boolean bool)
	        throws SQLException {
		saveObject(parameterIndex, Boolean.valueOf(bool));
		ps.setBoolean(parameterIndex, bool);
	}

	/**
	 * Adds byte to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param theByte
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setByte(final int parameterIndex, final byte theByte)
	        throws SQLException {
		saveObject(parameterIndex, Byte.valueOf(theByte));
		ps.setByte(parameterIndex, theByte);
	}

	/**
	 * Adds byte[] to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param byteArray
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setBytes(final int parameterIndex, final byte[] byteArray)
	        throws SQLException {
		saveObject(parameterIndex,
		    (byteArray == null ? NULL : "byte[] length=" + byteArray.length));
		ps.setBytes(parameterIndex, byteArray);
	}

	/** {@inheritDoc} */
	@Override
	public final void setCharacterStream(final int parameterIndex,
	    final Reader reader) throws SQLException {
		ps.setCharacterStream(parameterIndex, reader);
	}

	/**
	 * Debug string prints NULL if reader is null, or adds "stream length= " +
	 * length.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            the <code>java.io.Reader</code> object that contains the
	 *            Unicode data
	 * @param length
	 *            the number of characters in the stream
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setCharacterStream(final int parameterIndex,
	    final Reader reader, final int length) throws SQLException {
		saveObject(parameterIndex,
		    (reader == null ? NULL : "<stream length= " + length + ">"));
		ps.setCharacterStream(parameterIndex, reader, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setCharacterStream(final int parameterIndex,
	    final Reader reader, final long length) throws SQLException {
		ps.setCharacterStream(parameterIndex, reader);
	}

	/**
	 * Adds name of the object's class type(Clob) to the debug String. If object
	 * is null, NULL is added to debug String.
	 * 
	 * @param inx
	 *            the first parameter is 1, the second is 2, ...
	 * @param clob
	 *            a <code>Clob</code> object that maps an SQL <code>CLOB</code>
	 *            value
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setClob(final int inx, final Clob clob)
	        throws SQLException {
		saveObject(inx, clob);
		ps.setClob(inx, clob);
	}

	/** {@inheritDoc} */
	@Override
	public final void setClob(final int parameterIndex, final Reader reader)
	        throws SQLException {
		ps.setClob(parameterIndex, reader);
	}

	/** {@inheritDoc} */
	@Override
	public final void setClob(final int parameterIndex, final Reader reader,
	    final long length) throws SQLException {
		ps.setClob(parameterIndex, reader, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setCursorName(final String name) throws SQLException {
		ps.setCursorName(name);
	}

	/**
	 * Debug string displays date in YYYY-MM-DD HH24:MI:SS format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param date
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setDate(final int parameterIndex,
	    final java.sql.Date date) throws SQLException {
		saveObject(parameterIndex, date);
		ps.setDate(parameterIndex, date);
	}

	/**
	 * this implementation assumes that the Date has the date, and the calendar
	 * has the local info. For the debug string, the cal date is set to the date
	 * of x. Debug string displays date in YYYY-MM-DD HH24:MI:SS format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param date
	 *            parameter Object
	 * @param cal
	 *            uses x to set time
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setDate(final int parameterIndex,
	    final java.sql.Date date, final Calendar cal) throws SQLException {
		cal.setTime(new java.util.Date(date.getTime()));
		saveObject(parameterIndex, cal);
		ps.setDate(parameterIndex, date, cal);
	}

	/**
	 * Adds double to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	// CHECKSTYLE:OFF - Need to support doubles even though they're
	// problematic in Oracle.
	public final void setDouble(final int parameterIndex, final double x)
	        throws SQLException {
		saveObject(parameterIndex, new Double(x));
		ps.setDouble(parameterIndex, x);
	}

	// CHECKSTYLE:ON

	/** {@inheritDoc} */
	@Override
	public final void setEscapeProcessing(final boolean enable)
	        throws SQLException {
		ps.setEscapeProcessing(enable);
	}

	/** {@inheritDoc} */
	@Override
	public final void setFetchDirection(final int direction)
	        throws SQLException {
		ps.setFetchDirection(direction);
	}

	/** {@inheritDoc} */
	@Override
	public final void setFetchSize(final int rows) throws SQLException {
		ps.setFetchSize(rows);
	}

	/**
	 * Adds float to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	// CHECKSTYLE:OFF - Need to support doubles even though they're
	// problematic in Oracle.
	public final void setFloat(final int parameterIndex, final float x)
	        throws SQLException {
		saveObject(parameterIndex, new Float(x));
		ps.setFloat(parameterIndex, x);
	}

	// CHECKSTYLE: ON

	/**
	 * Sets the {@link SqlFormatter} used to format this statement.
	 * 
	 * @param formatter
	 *            One of the {@link SqlFormatter} implementations.
	 */
	public final void setFormatter(final SqlFormatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Adds int to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setInt(final int parameterIndex, final int x)
	        throws SQLException {
		saveObject(parameterIndex, Integer.valueOf(x));
		ps.setInt(parameterIndex, x);
	}

	/**
	 * Adds long to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setLong(final int parameterIndex, final long x)
	        throws SQLException {
		saveObject(parameterIndex, Long.valueOf(x));
		ps.setLong(parameterIndex, x);
	}

	/** {@inheritDoc} */
	@Override
	public final void setMaxFieldSize(final int max) throws SQLException {
		ps.setMaxFieldSize(max);
	}

	/** {@inheritDoc} */
	@Override
	public final void setMaxRows(final int max) throws SQLException {
		ps.setMaxRows(max);
	}

	/** {@inheritDoc} */
	@Override
	public final void setNCharacterStream(final int parameterIndex,
	    final Reader value) throws SQLException {
		ps.setNCharacterStream(parameterIndex, value);
	}

	/** {@inheritDoc} */
	@Override
	public final void setNCharacterStream(final int parameterIndex,
	    final Reader value, final long length) throws SQLException {
		ps.setNCharacterStream(parameterIndex, value);
	}

	/** {@inheritDoc} */
	@Override
	public final void setNClob(final int parameterIndex, final NClob value)
	        throws SQLException {
		ps.setNClob(parameterIndex, value);
	}

	/** {@inheritDoc} */
	@Override
	public final void setNClob(final int parameterIndex, final Reader reader)
	        throws SQLException {
		ps.setNClob(parameterIndex, reader);

	}

	/** {@inheritDoc} */
	@Override
	public final void setNClob(final int parameterIndex, final Reader reader,
	    final long length) throws SQLException {
		ps.setNClob(parameterIndex, reader, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setNString(final int parameterIndex, final String value)
	        throws SQLException {
		ps.setNString(parameterIndex, value);
	}

	/**
	 * Adds a NULL to the debug String.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            the SQL type code defined in <code>java.sql.Types</code>
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setNull(final int parameterIndex, final int sqlType)
	        throws SQLException {
		saveObject(parameterIndex, NULL);
		ps.setNull(parameterIndex, sqlType);
	}

	/**
	 * Adds a NULL to the debug String.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            a value from <code>java.sql.Types</code>
	 * @param typeName
	 *            the fully-qualified name of an SQL user-defined type; ignored
	 *            if the parameter is not a user-defined type or REF
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setNull(final int parameterIndex, final int sqlType,
	    final String typeName) throws SQLException {
		saveObject(parameterIndex, NULL);
		ps.setNull(parameterIndex, sqlType, typeName);
	}

	/**
	 * Adds name of the object's class type to the debug String. If object is
	 * null, NULL is added to debug String.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setObject(final int parameterIndex, final Object x)
	        throws SQLException {
		saveObject(parameterIndex, (x == null ? NULL : x.getClass().getName()));
		ps.setObject(parameterIndex, x);
	}

	/**
	 * Adds name of the object's class type to the debug String. If object is
	 * null, NULL is added to debug String.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @param targetSqlType
	 *            database type
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setObject(final int parameterIndex, final Object x,
	    final int targetSqlType) throws SQLException {
		saveObject(parameterIndex, (x == null ? NULL : x.getClass().getName()));
		ps.setObject(parameterIndex, x, targetSqlType);
	}

	/**
	 * Adds name of the object's class type to the debug String. If object is
	 * null, NULL is added to debug String.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @param targetSqlType
	 *            database type
	 * @param scale
	 *            see PreparedStatement
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setObject(final int parameterIndex, final Object x,
	    final int targetSqlType, final int scale) throws SQLException {
		saveObject(parameterIndex, (x == null ? NULL : x.getClass().getName()));
		ps.setObject(parameterIndex, x, targetSqlType, scale);
	}

	/** {@inheritDoc} */
	@Override
	public final void setPoolable(final boolean poolable) throws SQLException {
		ps.setPoolable(poolable);
	}

	/** {@inheritDoc} */
	public final void setQueryTimeout(final int seconds) throws SQLException {
		ps.setQueryTimeout(seconds);
	}

	/**
	 * From the javadocs: A reference to an SQL structured type value in the
	 * database. A Ref can be saved to persistent storage. The output from this
	 * method call in DebuggableStatement is a string representation of the Ref
	 * object by calling the Ref object's getBaseTypeName() method. Again, this
	 * will only be a String representation of the actual object being stored in
	 * the database.
	 * 
	 * @param i
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */

	public final void setRef(final int i, final Ref x) throws SQLException {
		saveObject(i, x);
		ps.setRef(i, x);
	}

	/** {@inheritDoc} */
	@Override
	public final void setRowId(final int parameterIndex, final RowId x)
	        throws SQLException {
		ps.setRowId(parameterIndex, x);
	}

	/**
	 * Adds short to debug string in parameterIndex position.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param value
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setShort(final int parameterIndex, final short value)
	        throws SQLException {
		saveObject(parameterIndex, Short.valueOf(value));
		ps.setShort(parameterIndex, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSQLXML(final int parameterIndex,
	    final SQLXML xmlObject) throws SQLException {
		ps.setSQLXML(parameterIndex, xmlObject);
	}

	/**
	 * Adds String to debug string in parameterIndex position. If String is null
	 * "NULL" is inserted in debug string. ***note**** In situations where a
	 * single ' is in the string being inserted in the database. The debug
	 * string will need to be modified to reflect this when running the debug
	 * statement in the database.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setString(final int parameterIndex, final String x)
	        throws SQLException {
		saveObject(parameterIndex, x);
		ps.setString(parameterIndex, x);
	}

	/**
	 * Debug string displays Time in HH24:MI:SS format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setTime(final int parameterIndex, final Time x)
	        throws SQLException {
		saveObject(parameterIndex, x);
		ps.setTime(parameterIndex, x);
	}

	/**
	 * This implementation assumes that the Time object has the time and
	 * Calendar has the locale info. For the debug string, the cal time is set
	 * to the value of x. Debug string displays time in HH24:MI:SS format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @param cal
	 *            sets time based on x
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setTime(final int parameterIndex, final Time x,
	    final Calendar cal) throws SQLException {
		cal.setTime(new java.util.Date(x.getTime()));
		saveObject(parameterIndex, cal);
		ps.setTime(parameterIndex, x, cal);
	}

	/**
	 * Debug string displays timestamp in YYYY-MM-DD HH24:MI:SS format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setTimestamp(final int parameterIndex, final Timestamp x)
	        throws SQLException {
		saveObject(parameterIndex, x);
		ps.setTimestamp(parameterIndex, x);
	}

	/**
	 * This implementation assumes that the Timestamp has the date/time and
	 * Calendar has the locale info. For the debug string, the cal date/time is
	 * set to the default value of Timestamp which is YYYY-MM-DD HH24:MI:SS.
	 * Debug string displays timestamp in DateFormat.LONG format.
	 * 
	 * @param parameterIndex
	 *            index of parameter
	 * @param x
	 *            parameter Object
	 * @param cal
	 *            sets time based on x
	 * @throws SQLException
	 *             thrown if a SQL error in encountered.
	 */
	public final void setTimestamp(final int parameterIndex, final Timestamp x,
	    final Calendar cal) throws SQLException {
		cal.setTime(new java.util.Date(x.getTime()));
		saveObject(parameterIndex, cal);
		ps.setTimestamp(parameterIndex, x, cal);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	public void setUnicodeStream(final int parameterIndex, final InputStream x,
	    final int length) throws SQLException {
		ps.setUnicodeStream(parameterIndex, x, length);
	}

	/** {@inheritDoc} */
	@Override
	public final void setURL(final int parameterIndex, final URL x)
	        throws SQLException {
		ps.setURL(parameterIndex, x);

	}

	/**
	 * Set starting time.
	 */
	private void start() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * this toString is overidden to return a String representation of the sql
	 * statement being sent to the database. If a bind variable is missing then
	 * the String contains a ? + (missing variable #)
	 * 
	 * @return the above string representation
	 */
	public final String toString() {
		StringTokenizer st = new StringTokenizer(filteredSql, "?");
		int count = 1;
		StringBuffer statement = new StringBuffer(INITIAL_STMT_LEN);
		while (st.hasMoreTokens()) {
			statement.append(st.nextToken());
			if (count <= variables.length) {
				if (variables[count - 1] != null
				    && variables[count - 1].isValueAssigned()) {
					try {
						statement.append(formatter
						    .format(variables[count - 1].getDebugObject()));
					} catch (SQLException e) {
						statement.append("SQLException: ");
						statement.append(e);
					}
				} else {
					statement.append("? (missing variable # ");
					statement.append(count);
					statement.append(" ) ");
				}
			}
			count++;
		}
		// unfilter the string in case there where rogue '?' in query string.
		char[] unfilterSql = statement.toString().toCharArray();
		for (int i = 0; i < unfilterSql.length; i++) {
			if (unfilterSql[i] == '\u0007') {
				unfilterSql[i] = '?';
			}
		}

		// return execute time
		if (LOGGER.isDebugEnabled()) {
			return new String(unfilterSql);
		} else {
			return new String(unfilterSql)
			    + System.getProperty("line.separator")
			    + System.getProperty("line.separator") + "query executed in "
			    + executeTime + " milliseconds"
			    + System.getProperty("line.separator");
		}

	}

	/** {@inheritDoc} */
	@Override
	public final <T> T unwrap(final Class<T> iface) throws SQLException {
		return ps.unwrap(iface);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeOnCompletion() throws SQLException {
		ps.closeOnCompletion();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return ps.isCloseOnCompletion();
	}
}
