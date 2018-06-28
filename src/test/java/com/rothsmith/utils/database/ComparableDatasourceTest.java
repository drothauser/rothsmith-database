/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rothsmith.utils.database.ComparableDatasource;

/**
 * Test {@link ComparableDatasource}.
 * 
 * @version $Revision: 1705 $
 * @author $Author: drarch $
 * 
 */
@RunWith(Parameterized.class)
public class ComparableDatasourceTest {

	/**
	 * SLF4J Logger for ComparableDatasourceTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(ComparableDatasourceTest.class);

	/**
	 * {@link BasicDataSource} 1 to compare.
	 */
	private final BasicDataSource bds1;

	/**
	 * {@link BasicDataSource} 2 to compare.
	 */
	private final BasicDataSource bds2;

	/**
	 * Expected results of the comparison.
	 */
	private final boolean expectedOutcome;

	/**
	 * @return {@link BasicDataSource} objects to compare.
	 */
	// CHECKSTYLE:OFF
	@Parameterized.Parameters
	@SuppressWarnings("PMD")
	public static Collection<Object[]> createTestVals() {
		return Arrays.asList(new Object[][] {
		    { createDatasource("com.ibm.as400.access.AS400JDBCDriver", "test",
		        "secret", "jdbc:as400://gemini/", 0, 10, 5000, null),
		    createDatasource("com.ibm.as400.access.AS400JDBCDriver", "test",
		        "secret", "jdbc:as400://gemini/", 0, 10, 5000, null),
		    true }, {
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa02.fcci-group.com",
		            0, 10, 5000, null),
		        false },
		    { createDatasource("com.ibm.as400.access.AS400JDBCDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, 5000, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            1, 10, 5000, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 99, 5000, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test123",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, 5000, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret123",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, 5000, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, -1, null),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            0, 10, 5000, "SELECT 1 FROM DUAL"),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriverX", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 11, 9999, null),
		        createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		            "secret",
		            "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		            999, 10, 5000, "SELECT 1 FROM DUAL"),
		        false },
		    { createDatasource("oracle.jdbc.driver.OracleDriver", "test",
		        "secret",
		        "jdbc:oracle:thin:@oracledev2.fcci-group.com:1521/qa01.fcci-group.com",
		        0, 10, 5000, null), null, false }

		});
	}

	// CHECKSTYLE:ON

	/**
	 * Constructor to setup the test.
	 * 
	 * @param bds1
	 *            {@link BasicDataSource} comparison operand 1
	 * @param bds2
	 *            The {@link BasicDataSource} comparison operand 2
	 * @param expectedOutcome
	 *            expected result (boolean)
	 */
	public ComparableDatasourceTest(BasicDataSource bds1, BasicDataSource bds2,
	    boolean expectedOutcome) {
		this.bds1 = bds1;
		this.bds2 = bds2;
		this.expectedOutcome = expectedOutcome;
	}

	/**
	 * Test method for {@link ComparableDatasource#equals(Object)} .
	 */
	@Test
	@SuppressWarnings("PMD")
	public void testEqualsObject() {

		ComparableDatasource comparable1 = new ComparableDatasource(bds1);
		LOGGER.info("comparable datasource 1 = \n" + comparable1.toString());
		ComparableDatasource comparable2 =
		    (bds2 == null) ? null : new ComparableDatasource(bds2);
		assertTrue((comparable1.equals(
		    (comparable2 == null) ? null : comparable2) == expectedOutcome));
		// CHECKSTYLE:OFF
		if (comparable2 != null && expectedOutcome == true) {
			assertEquals(comparable1.hashCode(), comparable2.hashCode());
		}

	}

	/**
	 * Create a {@link BasicDataSource} stub.
	 * 
	 * @param driverClassName
	 *            Drive class name
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @param url
	 *            JDBC URL
	 * @param initialSize
	 *            initial pool size
	 * @param maxActive
	 *            maximum pool size
	 * @param maxWait
	 *            maximum wait
	 * @param validationQuery
	 *            validation query
	 * @return a new {@link BasicDataSource}
	 */
	// CHECKSTYLE:OFF More than 7 parameters ok here.
	private static BasicDataSource createDatasource(String driverClassName,
	    String username, String password, String url, int initialSize,
	    int maxActive, int maxWait, String validationQuery) {

		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driverClassName);
		bds.setUsername(username);
		bds.setPassword(password);
		bds.setUrl(url);
		bds.setInitialSize(initialSize);
		bds.setMaxActive(maxActive);
		bds.setMaxWait(maxWait);
		bds.setValidationQuery(validationQuery);

		return bds;
	}
	// CHECKSTYLE: ON

}
