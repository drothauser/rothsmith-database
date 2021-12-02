/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.tomcat;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.tomcat.TomcatDatasourceChecker;

/**
 * Test {@link TomcatDatasourceChecker}.
 * 
 * @author drothauser
 */
public class TomcatDatasourceCheckerIT {

	/**
	 * SLF4J Logger for TomcatDatasourceCheckerIT.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(TomcatDatasourceCheckerIT.class);

	/**
	 * Test method for {@link TomcatDatasourceChecker#main(String[])}.
	 */
	@Test
	public void testMain() {
		try {
			URL url = ClassLoader.getSystemResource("context.xml");
			File datasourceFile = new File(url.getFile());
			TomcatDatasourceChecker
			    .main(new String[] { datasourceFile.getAbsolutePath() });
		} catch (Exception e) {
			LOGGER.error("Caught exception: " + e, e);
			fail("Expected TomcatDatasourceChecker to complete successfully.");
		}
	}

	/**
	 * Test method for {@link TomcatDatasourceChecker#main(String[])} with no
	 * datasource file argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMainNoArg() {

		TomcatDatasourceChecker.main(new String[] {});

	}

}
