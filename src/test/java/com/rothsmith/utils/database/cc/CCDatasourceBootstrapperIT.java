/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.cc;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.DatasourceParserException;
import net.rothsmith.utils.database.JDBCServiceLocator;
import net.rothsmith.utils.database.JndiDatasourceBootstrapper;
import net.rothsmith.utils.database.cc.CCDatasourceParser;

/**
 * Test {@link JndiDatasourceBootstrapper} using a Claim Center datasource
 * configuration file.
 * 
 * @author drothauser
 */
@Ignore
public class CCDatasourceBootstrapperIT {

	/**
	 * SLF4J Logger for TomcatDatasourceBootstrapperIT.
	 */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(CCDatasourceBootstrapperIT.class);

	/**
	 * Claim Center datasource.xml file.
	 */
	private File datasourceFile;

	/**
	 * <ul>
	 * <li>Finds datasources.xml in the classpath (in
	 * src/test/resources).</li>
	 * <li>Deletes JNDI .bindings file from the %TEMP% directory.
	 * </ul>
	 * 
	 * @throws IOException
	 *             Possible I/O Error.
	 * 
	 */
	@Before
	public void setUp() throws IOException {

		URL url =
			ClassLoader.getSystemResource("CC-DatabaseAuthentication.xml");
		datasourceFile = new File(url.getFile());

		// Delete .bindings file.
		String tmpdir = System.getProperty("java.io.tmpdir");
		File bindingsFile =
			new File(tmpdir + File.separatorChar + ".bindings");
		FileUtils.deleteQuietly(bindingsFile);
	}

	/**
	 * Test method for {@link JndiDatasourceBootstrapper#bind(File)} using a
	 * Claim Center datasource file.
	 * 
	 * @throws NamingException
	 *             possible JNDI error.
	 * @throws SQLException
	 *             possible SQL error.
	 */
	@Test
	public void testBindCCDatasource() throws NamingException, SQLException {

		Connection conn = null;
		try {
			JndiDatasourceBootstrapper bootstrapper =
				new JndiDatasourceBootstrapper(new CCDatasourceParser());
			bootstrapper.bind(datasourceFile);

			DataSource dataSource = JDBCServiceLocator.getInstance()
				.getDataSource("java:comp/env/jdbc/ccdb");

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
			new JndiDatasourceBootstrapper(new CCDatasourceParser());
		bootstrapper.bind(new File("bogus.xml"));

	}

}
