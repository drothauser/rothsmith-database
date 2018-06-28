/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.rothsmith.utils.database.JndiDatasourceBootstrapperException;
import com.rothsmith.utils.database.bootstrap.JNDIBootstrapper;

/**
 * Tests for {@link JNDIBootstrapper}.
 * 
 * @author drothauser
 * 
 */
public class JNDIBootstrapperTest {

	/**
	 * Deletes JNDI .bindings file from the %TEMP% directory.
	 * 
	 * @throws IOException
	 *             Possible I/O Error.
	 * 
	 */
	@Before
	public void setUp() throws IOException {

		// Delete .bindings file.
		String tmpdir = System.getProperty("java.io.tmpdir");
		File bindingsFile = new File(tmpdir + File.separatorChar + ".bindings");
		FileUtils.deleteQuietly(bindingsFile);
	}

	/**
	 * Test bootstrapping a JBoss datasource file using
	 * {@link com.rothsmith.utils.database.bootstrap.JNDIBootstrapper#bootstrapDS(String)}
	 * .
	 */
	@Test
	public void testBootstrapJBossDS() {
		try {
			JNDIBootstrapper.JBOSS.bootstrapDS("datasources.xml");
		} catch (JndiDatasourceBootstrapperException e) {
			fail(e.toString());
		}
	}

	/**
	 * Test bootstrapping a Tomcat datasource file using
	 * {@link com.rothsmith.utils.database.bootstrap.JNDIBootstrapper#bootstrapDS(String)}
	 * .
	 */
	@Test
	public void testBootstrapTomcatDS() {
		try {
			JNDIBootstrapper.TOMCAT.bootstrapDS("context.xml");
		} catch (JndiDatasourceBootstrapperException e) {
			fail(e.toString());
		}
	}

	/**
	 * Test bootstrapping a Claim Center datasource file using
	 * {@link com.rothsmith.utils.database.bootstrap.JNDIBootstrapper#bootstrapDS(String)}
	 * .
	 */
	@Test
	public void testBootstrapClaimCenterDS() {
		try {
			JNDIBootstrapper.CC.bootstrapDS("CC-DatabaseAuthentication.xml");
		} catch (JndiDatasourceBootstrapperException e) {
			fail(e.toString());
		}
	}

	/**
	 * Test
	 * {@link com.rothsmith.utils.database.bootstrap.JNDIBootstrapper#bootstrapDS(String)}
	 * using a file that doesn't exist.
	 * 
	 * @throws JndiDatasourceBootstrapperException
	 *             expects bootstrap error
	 */
	@Test(expected = JndiDatasourceBootstrapperException.class)
	public void testBootstrapBadFile()
	        throws JndiDatasourceBootstrapperException {

		JNDIBootstrapper.JBOSS.bootstrapDS("bogus.xml");

	}

}
