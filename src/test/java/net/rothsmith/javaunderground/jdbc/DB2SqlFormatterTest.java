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
import net.rothsmith.javaunderground.jdbc.SqlFormatter;

/**
 * Junit test for the {@link DB2SqlFormatter} class.
 * 
 * @author drothauser
 * 
 */
public class DB2SqlFormatterTest {

	/**
	 * SLF4J Logger for DB2SqlFormatterTest.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(DB2SqlFormatterTest.class);

	/**
	 * {@link DB2SqlFormatter}.
	 */
	private DB2SqlFormatter db2Formatter;

	/**
	 * Initial the test fixture.
	 * 
	 * @throws Exception
	 *             possible error
	 */
	@Before
	public void setUp() throws Exception {
		BaseSqlFormatter baseSqlFormatter = new BaseSqlFormatter();
		db2Formatter = new DB2SqlFormatter(baseSqlFormatter);
	}

	/**
	 * Test method for {@link DB2SqlFormatter#DB2SqlFormatter(SqlFormatter)}.
	 */
	@Test
	public void testDB2SqlFormatter() {
		try {
			BaseSqlFormatter baseSqlFormatter = new BaseSqlFormatter();
			db2Formatter = new DB2SqlFormatter(baseSqlFormatter);
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

		String formattedString = db2Formatter.format("Oracle Test");
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

		String formattedDate =
		    db2Formatter.format(new java.sql.Date(System.currentTimeMillis()));
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
		    db2Formatter.format(GregorianCalendar.getInstance());
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
		    db2Formatter.format(new Time(System.currentTimeMillis()));
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
		    db2Formatter.format(new Timestamp(System.currentTimeMillis()));
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

		String nullString = db2Formatter.format(null);
		assertEquals("NULL", nullString);

	}
}
