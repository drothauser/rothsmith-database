/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.cc;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.cc.CCDatasourceChecker;

/**
 * Test {@link CCDatasourceChecker}.
 * 
 * @author drothauser
 */
@Ignore
public class CCDatasourceCheckerIT {

	/**
	 * SLF4J Logger for CCDatasourceCheckerIT.
	 */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(CCDatasourceCheckerIT.class);

	/**
	 * Test method for {@link CCDatasourceChecker#main(String[])} with an
	 * Oracle datasource.
	 */
	@Test
	public void testCCDatasourceOracle() {
		try {
			URL url = ClassLoader
				.getSystemResource("CC-DatabaseAuthentication.xml");
			File datasourceFile = new File(url.getFile());
			CCDatasourceChecker
				.main(new String[] { datasourceFile.getAbsolutePath() });
		} catch (Exception e) {
			LOGGER.error("Caught exception: " + e, e);
			fail("Expected CCDatasourceChecker to complete successfully.");
		}
	}

	/**
	 * Test method for {@link CCDatasourceChecker#main(String[])} with an
	 * AS400 datasource.
	 */
	@Test
	public void testCCDatasourceAS4() {
		try {
			URL url = ClassLoader
				.getSystemResource("ODS-AS4-DatabaseAuthentication.xml");
			File datasourceFile = new File(url.getFile());
			CCDatasourceChecker
				.main(new String[] { datasourceFile.getAbsolutePath() });
		} catch (Exception e) {
			LOGGER.error("Caught exception: " + e, e);
			fail("Expected CCDatasourceChecker to complete successfully.");
		}
	}

	/**
	 * Test method for {@link CCDatasourceChecker#main(String[])} with an SQL
	 * Server datasource.
	 */
	@Test
	public void testCCDatasourceSQL() {
		try {
			URL url = ClassLoader.getSystemResource(
					"IMAGERIGHT-" + "Database-Authentication.xml");
			File datasourceFile = new File(url.getFile());
			CCDatasourceChecker
				.main(new String[] { datasourceFile.getAbsolutePath() });
		} catch (Exception e) {
			LOGGER.error("Caught exception: " + e, e);
			fail("Expected CCDatasourceChecker to complete successfully.");
		}
	}

	/**
	 * Test method for {@link CCDatasourceChecker#main(String[])} with no
	 * datasource file argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMainNoArg() {

		CCDatasourceChecker.main(new String[] {});

	}

}
