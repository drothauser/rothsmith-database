/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.javaunderground.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.javaunderground.jdbc.BaseSqlFormatter;
import net.rothsmith.javaunderground.jdbc.DB2SqlFormatter;
import net.rothsmith.javaunderground.jdbc.OracleSqlFormatter;
import net.rothsmith.javaunderground.jdbc.SqlFormatter;

/**
 * Junit test for the {@link OracleSqlFormatterTest} class.
 * 
 * @author drothauser
 * 
 */
public class OracleSqlFormatterTest {

	/**
	 * SLF4J Logger for OracleSqlFormatterTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(OracleSqlFormatterTest.class);

	/**
	 * {@link DB2SqlFormatter}.
	 */
	private OracleSqlFormatter oracleFormatter;

	/**
	 * Set up the test fixture.
	 * 
	 * @throws Exception
	 *             possible error
	 */
	@Before
	public void setUp() throws Exception {
		BaseSqlFormatter baseSqlFormatter = new BaseSqlFormatter();
		oracleFormatter = new OracleSqlFormatter(baseSqlFormatter);
	}

	/**
	 * Test method for
	 * {@link OracleSqlFormatter#OracleSqlFormatter(SqlFormatter)}.
	 */
	@Test
	public void testOracleSqlFormatter() {
		try {
			BaseSqlFormatter baseSqlFormatter = new BaseSqlFormatter();
			oracleFormatter = new OracleSqlFormatter(baseSqlFormatter);
		} catch (Exception e) {
			String errmsg = "Exception caught in testDefaultSqlFormatter: " + e;
			LOGGER.error(errmsg, e);
			fail(errmsg);
		}
	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}
	 * using String.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	@SuppressWarnings("PMD")
	public void testFormatString() throws SQLException {

		String formattedString = oracleFormatter.format("Oracle Test");
		assertFalse("formattedString is null", "NULL".equals(formattedString));

	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	public void testFormatDate() throws SQLException {

		String formattedDate = oracleFormatter
		    .format(new java.sql.Date(System.currentTimeMillis()));
		assertFalse("formattedDate is null", "NULL".equals(formattedDate));

	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	public void testFormatCalendar() throws SQLException {

		String formattedCal =
		    oracleFormatter.format(GregorianCalendar.getInstance());
		assertFalse("formattedCalendar is null", "NULL".equals(formattedCal));

	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}using
	 * {@link Time}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	public void testFormatTime() throws SQLException {

		String formattedTime =
		    oracleFormatter.format(new Time(System.currentTimeMillis()));
		assertFalse("formattedCalendar is null", "NULL".equals(formattedTime));

	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}
	 * using {@link Timestamp}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	public void testFormatTimeStamp() throws SQLException {

		String formattedTimeStamp =
		    oracleFormatter.format(new Timestamp(System.currentTimeMillis()));
		assertFalse("formattedCalendar is null",
		    "NULL".equals(formattedTimeStamp));

	}

	/**
	 * Test method for
	 * {@link net.rothsmith.javaunderground.jdbc.DB2SqlFormatter#format(Object)}
	 * using null.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL is occurs.
	 */
	@Test
	public void testFormatNull() throws SQLException {

		String nullString = oracleFormatter.format(null);
		assertEquals("NULL", nullString);

	}
}
