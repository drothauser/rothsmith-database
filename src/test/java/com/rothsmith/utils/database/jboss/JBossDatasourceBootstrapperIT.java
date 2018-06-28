/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.jboss;

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
import com.rothsmith.utils.database.jboss.JBossDatasourceParser;

/**
 * Test {@link JndiDatasourceBootstrapper}.
 * 
 * @author drothauser
 */
public class JBossDatasourceBootstrapperIT {

	/**
	 * SLF4J Logger for JBossDatasourceBootstrapperIT.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JBossDatasourceBootstrapperIT.class);

	/**
	 * JBoss datasource.xml file.
	 */
	private File datasourceFile;

	/**
	 * <ul>
	 * <li>Finds datasources.xml in the classpath (in src/test/resources).</li>
	 * <li>Deletes JNDI .bindings file from the %TEMP% directory.
	 * </ul>
	 * 
	 * @throws IOException
	 *             Possible I/O Error.
	 * 
	 */
	@Before
	public void setUp() throws IOException {

		URL url = ClassLoader.getSystemResource("datasources.xml");
		datasourceFile = new File(url.getFile());

	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} using a
	 * JBoss datasource file.
	 * 
	 * @throws NamingException
	 *             possible JNDI error.
	 * @throws SQLException
	 *             possible SQL error.
	 */
	@Test
	public void testBindJbossDatasourceFile()
	        throws NamingException, SQLException {

		Connection conn = null;
		try {
			JndiDatasourceBootstrapper bootstrapper =
			    new JndiDatasourceBootstrapper(new JBossDatasourceParser());
			bootstrapper.bind(datasourceFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
			    .getDataSource("java:comp/env/jdbc/Pph1PhoenixOracleDS");

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
	 * a changed attributes in the datasources. It tests it by binding a
	 * datasource and then loading another one with the exact same jndi names
	 * but with different attributes.
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 * @throws SQLException
	 *             possible SQL error
	 */
	@Test
	public void testBindJbossDataDSChanged()
	        throws NamingException, SQLException {

		Connection conn = null;
		try {
			URL url = ClassLoader.getSystemResource("datasources2.xml");
			File datasourceFile2 = new File(url.getFile());

			JndiDatasourceBootstrapper bootstrapper =
			    new JndiDatasourceBootstrapper(new JBossDatasourceParser());
			bootstrapper.bind(datasourceFile2);

			bootstrapper.bind(datasourceFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
			    .getDataSource("java:comp/env/jdbc/Ew3OracleDS");

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
		    new JndiDatasourceBootstrapper(new JBossDatasourceParser());
		bootstrapper.bind(new File("bogus.xml"));

	}

}
