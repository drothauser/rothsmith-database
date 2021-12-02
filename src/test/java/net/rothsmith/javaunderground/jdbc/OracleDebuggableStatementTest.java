/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.javaunderground.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.javaunderground.jdbc.DebugLevel;
import net.rothsmith.javaunderground.jdbc.DebuggableStatement;
import net.rothsmith.javaunderground.jdbc.FormatterFactory;
import net.rothsmith.javaunderground.jdbc.ParameterIndexOutOfBoundsException;
import net.rothsmith.javaunderground.jdbc.SqlFormatter;
import net.rothsmith.javaunderground.jdbc.StatementFactory;

/**
 * Junit tests for {@link DebuggableStatement}.
 * 
 * @author drothauser
 * 
 */
@Ignore
public class OracleDebuggableStatementTest {

	/**
	 * SLF4J Logger for OracleDebuggableStatementTest.
	 */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(OracleDebuggableStatementTest.class);

	/**
	 * JDBC {@link BasicDataSource}.
	 */
	private BasicDataSource datasource;

	/**
	 * Set up the test fixture.
	 * 
	 * @throws Exception
	 *             possible error
	 */
	@Before
	public void setUp() throws Exception {

		datasource = new BasicDataSource();
		datasource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		datasource.setUsername("reporting");
		datasource.setPassword("reporting");
		datasource.setUrl(
				"jdbc:oracle:thin:@ldap://oradsqa.fcci-group.com:10389/DV01,"
					+ "cn=OracleContext,dc=fcci-group,dc=com");

	}

	/**
	 * Test {@link DebuggableStatement} constructor.
	 */
	@Test
	public void testDebuggableStatement() {

		Connection conn = null; // NOPMD - Close by DbUtils
		DebuggableStatement debuggableStmt = null;
		try {

			conn = datasource.getConnection();
			String sqlStatement = "SELECT 1 FROM DUAL";
			SqlFormatter formatter =
				FormatterFactory.getInstance().getFormatter(conn);
			debuggableStmt = new DebuggableStatement(conn, sqlStatement,
					formatter, DebugLevel.ON);
			LOGGER.info(debuggableStmt.toString());
		} catch (Exception e) {
			String errmsg =
				"Exception caught in testDebuggableStatement: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(debuggableStmt);
			DbUtils.closeQuietly(conn);
		}
	}

	/**
	 * Test {@link StatementFactory} with {@link SqlFormatter}.
	 */
	@Test
	public void testDebuggableStatementFactoryFormatter() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - see DbUtils.closeQuietly(rs);
		try {

			conn = datasource.getConnection();

			SqlFormatter formatter =
				FormatterFactory.getInstance().getFormatter(conn);

			StatementFactory.setDefaultDebug(DebugLevel.ON);
			ps = StatementFactory.getStatement(conn,
					"SELECT DUMMY FROM DUAL WHERE DUMMY = ?", formatter);

			ps.setString(1, "X");

			String debuggableSQL = ps.toString();
			LOGGER.debug("SQL Statement:\n" + debuggableSQL); // NOPMD ignore
															  // same literals

			assertEquals("SELECT DUMMY FROM DUAL WHERE DUMMY = 'X'",
					debuggableSQL);

			rs = ps.executeQuery();

			if (rs.next()) {
				assertEquals("X", rs.getString(1));
			} else {
				fail("Expected query to return  \"X\""); // NOPMD ignore same
														 // literals
			}

		} catch (SQLException e) {
			String errmsg = "Caught SQLException: " + e; // NOPMD ignore same
														 // literals
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(conn, ps, rs);
		}
	}

	/**
	 * Test {@link StatementFactory} with DebugLevel ON.
	 */
	@Test
	public void testDebuggableStatementFactoryOn() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - see DbUtils.closeQuietly(rs);
		try {

			conn = datasource.getConnection();

			ps = StatementFactory.getStatement(conn,
					"SELECT * FROM DUAL WHERE DUMMY = ?", DebugLevel.ON);

			ps.setString(1, "X");

			String debuggableSQL = ps.toString();
			LOGGER.debug("SQL Statement:\n" + debuggableSQL);

			assertEquals("SELECT * FROM DUAL WHERE DUMMY = 'X'",
					debuggableSQL);

			rs = ps.executeQuery();

			if (rs.next()) {
				assertEquals("X", rs.getString(1));
			} else {
				fail("Expected query to return  \"X\"");
			}

		} catch (SQLException e) {
			String errmsg = "Caught SQLException: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(conn, ps, rs);
		}
	}

	/**
	 * Test {@link StatementFactory} with default DebugLevel (OFF).
	 */
	@Test
	public void testDebuggableStatementFactoryDefault() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - see DbUtils.closeQuietly(rs);
		try {

			conn = datasource.getConnection();

			ps = StatementFactory.getStatement(conn,
					"SELECT * FROM DUAL WHERE DUMMY = ?");

			ps.setString(1, "X");

			String debuggableSQL = ps.toString();
			LOGGER.debug("SQL Statement:\n" + debuggableSQL);

			assertNotSame("SELECT * FROM DUAL " + "WHERE DUMMY = 'X'",
					debuggableSQL);

			rs = ps.executeQuery();

			if (rs.next()) {
				assertEquals("X", rs.getString(1));
			} else {
				fail("Expected query to return  \"X\"");
			}

		} catch (SQLException e) {
			String errmsg = "Caught SQLException: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(conn, ps, rs);
		}
	}

	/**
	 * Test {@link StatementFactory} with DebugLevel OFF.
	 */
	@Test
	public void testDebuggableStatementFactoryOff() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - see DbUtils.closeQuietly(rs);
		try {

			conn = datasource.getConnection();

			ps = StatementFactory.getStatement(conn,
					"SELECT * FROM DUAL WHERE DUMMY = ?", DebugLevel.OFF);

			ps.setString(1, "X");

			String debuggableSQL = ps.toString();
			LOGGER.debug("SQL Statement:\n" + debuggableSQL);

			assertNotSame("SELECT * FROM DUAL " + "WHERE DUMMY = 'X'",
					debuggableSQL);

			rs = ps.executeQuery();

			if (rs.next()) {
				assertEquals("X", rs.getString(1));
			} else {
				fail("Expected query to return  \"X\"");
			}

		} catch (SQLException e) {
			String errmsg = "Caught SQLException: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(conn, ps, rs);
		}
	}

	/**
	 * Test {@link StatementFactory} with
	 * {@link ParameterIndexOutOfBoundsException}.
	 * 
	 * @throws SQLException
	 *             {@link ParameterIndexOutOfBoundsException} subclass of
	 *             SQLException
	 */
	@Test(expected = ParameterIndexOutOfBoundsException.class)
	@Ignore
	public void testDebuggableStatementFactoryParamOOB()
			throws SQLException {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		try {

			conn = datasource.getConnection();

			ps = StatementFactory.getStatement(conn,
					"SELECT DUMMY FROM DUAL WHERE DUMMY = ?", DebugLevel.ON);

			ps.setString(2, "X");

		} finally {
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(conn);
		}
	}

	/**
	 * Test {@link StatementFactory} to cover {@link DebuggableStatement}.
	 */
	@Test
	public void testDebuggableStatementFactoryParams() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - see DbUtils.closeQuietly(rs);
		try {

			conn = datasource.getConnection();

			ps = StatementFactory.getStatement(conn,
					"SELECT * " + "FROM DUAL WHERE DUMMY = ?",
					DebugLevel.ON);

			ps.setString(1, "X");

			String debuggableSQL = ps.toString();
			LOGGER.debug("SQL Statement:\n" + debuggableSQL);

			Method[] methods = DebuggableStatement.class.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (method.getParameterTypes().length == 0
					&& !void.class.equals(method.getReturnType())) {
					LOGGER.info("method: " + methodName);
					try {
						method.invoke(ps, new Object[] {});
					} catch (Exception e) {
						LOGGER.warn(
								"Could not invoke " + methodName + ": " + e);
					}
				}
			}

			rs = ps.executeQuery();

			if (rs.next()) {
				assertEquals("X", rs.getString(1));
			} else {
				fail("Expected query to return  \"X\"");
			}

		} catch (SQLException e) {
			String errmsg = "Caught SQLException: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(conn, ps, rs);
		}
	}

	/**
	 * Test {@link DebuggableStatement#toString()}.
	 * 
	 */
	@Test
	public void testToString() {

		Connection conn = null; // NOPMD - Close by DbUtils
		PreparedStatement ps = null;
		ResultSet rs = null; // NOPMD - Close by DbUtils

		try {
			conn = datasource.getConnection();
			String sqlStatement =
				"SELECT SYSDATE FROM DUAL WHERE TO_TIMESTAMP"
					+ "('2009-03-15 12:52:03','YYYY-MM-DD HH24:MI:SS')"
					+ " <= ?";
			SqlFormatter formatter =
				FormatterFactory.getInstance().getFormatter(conn);
			ps = new DebuggableStatement(conn, sqlStatement, formatter,
					DebugLevel.ON);
			java.sql.Timestamp paramTS =
				new java.sql.Timestamp(System.currentTimeMillis());
			ps.setTimestamp(1, paramTS);
			String sql = ps.toString();
			LOGGER.info("test sql:" + System.getProperty("line.separator")
				+ sql);

			rs = ps.executeQuery();

			assertTrue("Expected SQL statement",
					StringUtils.contains(sql, "SELECT SYSDATE"));

			if (!rs.next()) {
				fail(sql + " did not return any results.");
			}

		} catch (Exception e) {
			DbUtils.closeQuietly(conn, ps, rs);
			String errmsg = "Exception caught running test: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(conn);
		}

	}

}
