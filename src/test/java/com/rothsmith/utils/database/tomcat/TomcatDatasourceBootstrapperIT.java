/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.tomcat;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.utils.database.DatasourceParserException;
import com.rothsmith.utils.database.JDBCServiceLocator;
import com.rothsmith.utils.database.JndiDatasourceBootstrapper;
import com.rothsmith.utils.database.tomcat.TomcatDatasourceParser;

/**
 * Test {@link JndiDatasourceBootstrapper} using a Tomcat context.xml file.
 * 
 * @author drothauser
 */
public class TomcatDatasourceBootstrapperIT {

	/**
	 * SLF4J Logger for TomcatDatasourceBootstrapperIT.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(TomcatDatasourceBootstrapperIT.class);

	/**
	 * Tomcat context.xml file that contains datasources.
	 */
	private File contextXmlFile;

	/**
	 * Tomcat server.xml file that contains datasources.
	 */
	private File serverXmlFile;

	/**
	 * <ul>
	 * <li>Finds context.xml in the classpath (in src/test/resources).</li>
	 * <li>Deletes JNDI .bindings file from the %TEMP% directory.
	 * </ul>
	 * 
	 * @throws IOException
	 *             Possible I/O Error.
	 * 
	 */
	@Before
	public void setUp() throws IOException {

		URL contextXmlUrl = ClassLoader.getSystemResource("context.xml");
		contextXmlFile = new File(contextXmlUrl.getFile());

		URL serverXmlUrl = ClassLoader.getSystemResource("server.xml");
		serverXmlFile = new File(serverXmlUrl.getFile());

	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} using a
	 * Tomcat server.xml file.
	 * 
	 * @throws NamingException
	 *             possible JNDI error.
	 * @throws SQLException
	 *             possible SQL error.
	 */
	@Test
	public void testBindTomcatServerXML() throws NamingException, SQLException {

		Connection conn = null;
		try {
			JndiDatasourceBootstrapper bootstrapper =
			    new JndiDatasourceBootstrapper(new TomcatDatasourceParser());
			bootstrapper.bind(serverXmlFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
			    .getDataSource("java:/comp/env/jdbc/OdsOracleDS");

			assertNotNull(dataSource);

			LOGGER.info(ReflectionToStringBuilder.toString(dataSource,
			    ToStringStyle.MULTI_LINE_STYLE));

			conn = dataSource.getConnection();

			assertNotNull(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} using a
	 * Tomcat datasource file.
	 * 
	 * @throws NamingException
	 *             possible JNDI error.
	 * @throws SQLException
	 *             possible SQL error.
	 */
	@Test
	public void testBindTomcatContext() throws NamingException, SQLException {

		Connection conn = null;
		try {
			JndiDatasourceBootstrapper bootstrapper =
			    new JndiDatasourceBootstrapper(new TomcatDatasourceParser());
			bootstrapper.bind(contextXmlFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
			    .getDataSource("java:/comp/env/jdbc/OdsOracleDS");

			assertNotNull(dataSource);

			LOGGER.info(ReflectionToStringBuilder.toString(dataSource,
			    ToStringStyle.MULTI_LINE_STYLE));

			conn = dataSource.getConnection();

			assertNotNull(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} that tests
	 * changed attributes in the datasources. It tests it by binding a
	 * datasource and then loading another one with the exact same jndi names
	 * but with different attributes.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testBindTomcatDataDSChanged()
	        throws NamingException, SQLException {

		Connection conn = null;
		try {
			URL url = ClassLoader.getSystemResource("context2.xml");
			File datasourceFile2 = new File(url.getFile());

			JndiDatasourceBootstrapper bootstrapper =
			    new JndiDatasourceBootstrapper(new TomcatDatasourceParser());
			bootstrapper.bind(datasourceFile2);

			bootstrapper.bind(contextXmlFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
			    .getDataSource("java:comp/env/jdbc/OdsOracleDS");

			assertNotNull(dataSource);

			LOGGER.info(ReflectionToStringBuilder.toString(dataSource,
			    ToStringStyle.MULTI_LINE_STYLE));

			conn = dataSource.getConnection();

			assertNotNull(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} with an
	 * invalid datasource file.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test(expected = DatasourceParserException.class)
	public void testBindBadDatasourceFile()
	        throws NamingException, SQLException {

		JndiDatasourceBootstrapper bootstrapper =
		    new JndiDatasourceBootstrapper(new TomcatDatasourceParser());
		bootstrapper.bind(new File("bogus.xml"));

	}

}
