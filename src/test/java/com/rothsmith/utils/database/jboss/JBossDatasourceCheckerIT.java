/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.jboss;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.utils.database.jboss.JBossDatasourceChecker;

/**
 * Test {@link JBossDatasourceChecker}.
 * 
 * @author drothauser
 */
public class JBossDatasourceCheckerIT {

	/**
	 * SLF4J Logger for JBossDatasourceCheckerIT.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JBossDatasourceCheckerIT.class);

	/**
	 * Test method for {@link JBossDatasourceChecker#main(String[])}.
	 */
	@Test
	public void testMain() {
		try {
			URL url = ClassLoader.getSystemResource("test-ds.xml");
			File datasourceFile = new File(url.getFile());
			JBossDatasourceChecker
			    .main(new String[] { datasourceFile.getAbsolutePath() });
		} catch (Exception e) {
			LOGGER.error("Caught exception: " + e, e);
			fail("Expected JBossDatasourceChecker to complete successfully.");
		}
	}

	/**
	 * Test method for {@link JBossDatasourceChecker#main(String[])} with no
	 * datasource file argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMainNoArg() {

		JBossDatasourceChecker.main(new String[] {});

	}

}
