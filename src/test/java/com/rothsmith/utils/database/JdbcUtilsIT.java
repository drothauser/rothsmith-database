/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.rothsmith.utils.database.JDBCServiceLocator;
import com.rothsmith.utils.database.JdbcUtils;
import com.rothsmith.utils.database.JndiDatasourceBootstrapper;
import com.rothsmith.utils.database.jboss.JBossDatasourceParser;

/**
 * Tests for {@link JdbcUtils}.
 * 
 * @version $Revision: 729 $
 * 
 * @author drothauser
 * 
 */
public class JdbcUtilsIT {

	/**
	 * Bootstrap JNDI using a JBoss datasource file.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		URL url = ClassLoader.getSystemResource("test-ds.xml");
		File datasourceFile = new File(url.getFile());

		JndiDatasourceBootstrapper bootstrapper =
		    new JndiDatasourceBootstrapper(new JBossDatasourceParser());
		bootstrapper.bind(datasourceFile);

	}

	/**
	 * Test method for {@link JdbcUtils#rollbackQuietly(Connection)} .
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testRollbackQuietly() throws NamingException, SQLException {

		Connection conn = null;
		Statement statement = null;

		try {
			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			DataSource dataSource =
			    jdbcServiceLocator.getDataSource("JavaDbDS");

			assertNotNull(dataSource);

			conn = dataSource.getConnection();

			JdbcUtils.setAutoCommit(conn, false);

			statement = conn.createStatement();
			statement.execute("select current_timestamp from sysibm.sysdummy1");

			assertTrue(JdbcUtils.rollbackQuietly(conn));

		} finally {
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * Test method for {@link JdbcUtils#setAutoCommit(Connection, boolean)}.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testSetAutoCommit() throws NamingException, SQLException {

		Connection conn = null;

		try {
			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			DataSource dataSource =
			    jdbcServiceLocator.getDataSource("JavaDbDS");

			assertNotNull(dataSource);

			conn = dataSource.getConnection();

			boolean ok = JdbcUtils.setAutoCommit(conn, false);

			assertTrue(ok);
			assertFalse(conn.getAutoCommit());

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

}
