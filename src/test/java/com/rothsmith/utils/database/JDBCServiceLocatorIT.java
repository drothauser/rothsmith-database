/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import net.rothsmith.utils.database.JDBCServiceLocator;
import net.rothsmith.utils.database.JndiDatasourceBootstrapper;
import net.rothsmith.utils.database.jboss.JBossDatasourceParser;

/**
 * Tests for {@link JDBCServiceLocator}.
 * 
 * @version $Id: JDBCServiceLocatorIT.java 1705 2015-09-17 13:25:53Z drarch $
 * 
 * @author drothauser
 * 
 */
public class JDBCServiceLocatorIT {

	/**
	 * Bootstrap JNDI using a JBoss datasource file.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		URL url = ClassLoader.getSystemResource("datasources.xml");
		File datasourceFile = new File(url.getFile());

		JndiDatasourceBootstrapper bootstrapper =
		    new JndiDatasourceBootstrapper(new JBossDatasourceParser());
		bootstrapper.bind(datasourceFile);

	}

	/**
	 * Test method for {@link JDBCServiceLocator#getDataSource(String)}.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testGetDataSourceAS4() throws NamingException, SQLException {

		Connection conn = null;

		try {
			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			String iseriesDS = "java:comp/env/jdbc/WcAS400DS";
			DataSource dataSource = jdbcServiceLocator.getDataSource(iseriesDS);

			assertNotNull(dataSource);

			conn = dataSource.getConnection();

			assertNotNull(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * Test method for {@link JDBCServiceLocator#getDataSource(String)} using an
	 * Oracle datasource.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testGetDataSourceOracle() throws NamingException, SQLException {

		Connection conn = null;

		try {
			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			String oracleDS = "Pph1PhoenixOracleDS";
			DataSource dataSource = jdbcServiceLocator.getDataSource(oracleDS);

			assertNotNull(dataSource);

			conn = dataSource.getConnection();

			assertNotNull(conn);

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	/**
	 * Test method for {@link JDBCServiceLocator#getDataSource(String)} using an
	 * Imageright (SQL Server) datasource.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testGetDataSourceIR() throws NamingException, SQLException {

		Connection conn = null;

		try {
			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			String oracleDS = "java:/jdbc/ImageRightMsSqlDS";
			DataSource dataSource = jdbcServiceLocator.getDataSource(oracleDS);

			assertNotNull(dataSource);

			conn = dataSource.getConnection();

			assertNotNull(conn);

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	/**
	 * Test method for {@link JDBCServiceLocator#getDataSource(String)} to
	 * retrieve a cached datasource.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testCachedDatasource() throws NamingException, SQLException {

		Connection conn = null;

		try {

			JDBCServiceLocator jdbcServiceLocator =
			    JDBCServiceLocator.getInstance();

			String sqlDS = "comp/env/jdbc/ImageRightMsSqlDS";

			for (int i = 0; i < 2; i++) {
				DataSource dataSource = jdbcServiceLocator.getDataSource(sqlDS);
				assertNotNull(dataSource);
				conn = dataSource.getConnection();
				assertNotNull(conn);
			}

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

}
