/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.JDBCJndiNameNormalizer;
import net.rothsmith.utils.database.JndiNameNormalizer;

/**
 * Test {@link JDBCJndiNameNormalizer}.
 * 
 * @version $Revision: 1705 $
 * 
 * @author $Author: drarch $
 * 
 */
@RunWith(Parameterized.class)
public class JDBCJndiNameNormalizerTest {

	/**
	 * SLF4J Logger for JDBCJndiNameNormalizerTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JDBCJndiNameNormalizerTest.class);

	/**
	 * JNDI name to test.
	 */
	private final String jndiName;

	/**
	 * Base JNDI datasource name for testing.
	 */
	private static final String DSNAME = "Dv03OracleDS";

	/**
	 * Program for ensuring that a JNDI name is suitable to be lookup up in the
	 * &quot;java:/comp/env/jdbc/&quot; context.
	 */
	private static final JndiNameNormalizer NORMALIZER =
	    new JDBCJndiNameNormalizer();

	/**
	 * @return JNDI names to test. *
	 */
	@Parameterized.Parameters
	public static Collection<Object[]> createTestVals() {
		return Arrays
		    .asList(new Object[][] { { "java:/comp/env/jdbc/" + DSNAME },
		        { "comp/env/jdbc/" + DSNAME }, { "/comp/env/jdbc/" + DSNAME },
		        { "jdbc/" + DSNAME }, { "/jdbc/" + DSNAME },
		        { "java:/jdbc/" + DSNAME }, { DSNAME }, { "Java:" + DSNAME },
		        { "Java:/" + DSNAME }, { null }, { "" }, { " " } });
	}

	/**
	 * Constructor to setup the test.
	 * 
	 * @param jndiName
	 *            JNDI name to test
	 */
	public JDBCJndiNameNormalizerTest(final String jndiName) {

		this.jndiName = jndiName;
	}

	/**
	 * Test method for {@link JDBCJndiNameNormalizer#normalize(String)} .
	 */
	@Test
	public void testNormalize() {

		if (StringUtils.isBlank(jndiName)) {

			try {
				NORMALIZER.normalize(jndiName);
			} catch (IllegalArgumentException e) {
				assertEquals("jndiName argument is NULL or blank.",
				    e.getMessage());
			}

		} else {

			String normalized = NORMALIZER.normalize(jndiName);

			LOGGER.info("raw JNDI name: " + jndiName);
			LOGGER.info("normalized JNDI name: " + normalized);

			assertEquals("java:/comp/env/jdbc/" + DSNAME, normalized);

		}

	}

}
