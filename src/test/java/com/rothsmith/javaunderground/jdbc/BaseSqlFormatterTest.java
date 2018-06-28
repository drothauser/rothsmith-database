/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.javaunderground.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.junit.Before;
import org.junit.Test;

import com.rothsmith.javaunderground.jdbc.BaseSqlFormatter;
import com.rothsmith.javaunderground.jdbc.FormatterFactory;
import com.rothsmith.javaunderground.jdbc.OracleSqlFormatter;
import com.rothsmith.javaunderground.jdbc.SqlFormatter;

/**
 * Junit test for the {@link BaseSqlFormatter} class.
 * 
 * @author drothauser
 * 
 */
@SuppressWarnings({ "PMD", "checkstyle:methodcount" })
public class BaseSqlFormatterTest {

	/**
	 * {@link BaseSqlFormatter}
	 */
	private BaseSqlFormatter baseSqlFormatter;

	/**
	 * Mock {@link Blob} field.
	 */
	private Blob blob;

	/**
	 * Mock {@link Clob} field.
	 */
	private Clob clob;

	/**
	 * Mock {@link java.sql.Array} field.
	 */
	private Array array;

	/**
	 * Mock {@link java.sql.Ref} field.
	 */
	private Ref ref;

	/**
	 * Test string field.
	 */
	private String testString;

	/**
	 * Set up the test fixture.
	 * 
	 * @throws Exception
	 *             possible error
	 */
	@Before
	public void setUp() throws Exception {

		baseSqlFormatter = new BaseSqlFormatter();

		this.clob = createMockClob();

		this.blob = createMockBlob();

		this.array = createMockArray();

		this.ref = createMockRef();

		this.testString = "Mock String";

	}

	/**
	 * @return a new {@link Clob} object.
	 */
	private Clob createMockClob() { // NOPMD - for mock
		Clob clob = new java.sql.Clob() {

			private String clobString = "Test Clob";

			@Override
			public void free() throws SQLException {
				// stub
			}

			@Override
			public InputStream getAsciiStream() throws SQLException {

				return null;
			}

			@Override
			public Reader getCharacterStream() throws SQLException {

				return null;
			}

			@Override
			public Reader getCharacterStream(long pos, long length)
			        throws SQLException {

				return null;
			}

			@Override
			public String getSubString(long pos, int length)
			        throws SQLException {

				return null;
			}

			@Override
			public long length() throws SQLException {
				return clobString.length();
			}

			@Override
			public long position(String searchstr, long start)
			        throws SQLException {

				return 0;
			}

			@Override
			public long position(Clob searchstr, long start)
			        throws SQLException {

				return 0;
			}

			@Override
			public OutputStream setAsciiStream(long pos) throws SQLException {

				return null;
			}

			@Override
			public Writer setCharacterStream(long pos) throws SQLException {

				return null;
			}

			@Override
			public int setString(long pos, String str) throws SQLException {

				return 0;
			}

			@Override
			public int setString(long pos, String str, int offset, int len)
			        throws SQLException {

				return 0;
			}

			@Override
			public void truncate(long len) throws SQLException {
				// stub
			}
		};

		return clob;
	}

	/**
	 * @return a new {@link Blob} object.
	 */
	private Blob createMockBlob() { // NOPMD - for mock
		Blob blob = new java.sql.Blob() {

			private byte[] bytes = "Test Blob".getBytes();

			@Override
			public int setBytes(long pos, byte[] bytes) throws SQLException {
				return 0;
			}

			@Override
			public void free() throws SQLException {
				// stub
			}

			@Override
			public InputStream getBinaryStream() throws SQLException {
				return null;
			}

			@Override
			public InputStream getBinaryStream(long pos, long length)
			        throws SQLException {
				return null;
			}

			@Override
			public byte[] getBytes(long pos, int length) throws SQLException {
				return bytes;
			}

			@Override
			public long length() throws SQLException {

				return bytes.length;
			}

			@Override
			public long position(byte[] pattern, long start)
			        throws SQLException {
				return 0;
			}

			@Override
			public long position(Blob pattern, long start) throws SQLException {
				return 0;
			}

			@Override
			public OutputStream setBinaryStream(long pos) throws SQLException {
				return null;
			}

			@Override
			public int setBytes(long pos, byte[] bytes, int offset, int len)
			        throws SQLException {
				return 0;
			}

			@Override
			public void truncate(long len) throws SQLException {
				// stub
			}
		};

		return blob;
	}

	/**
	 * Create a mock {@link java.sql.Ref} object.
	 * 
	 * @return {@link java.sql.Ref}
	 */
	private java.sql.Ref createMockRef() {

		java.sql.Ref ref = new java.sql.Ref() {

			@Override
			public String getBaseTypeName() throws SQLException {
				return java.sql.Date.class.getName();
			}

			@Override
			public Object getObject() throws SQLException {
				// stub
				return null;
			}

			@Override
			public Object getObject(Map<String, Class<?>> map)
			        throws SQLException {
				// stub
				return null;
			}

			@Override
			public void setObject(Object value) throws SQLException {
				// stub
			}

		};

		return ref;

	}

	/**
	 * @return a new mock {@link java.sql.Array} object.
	 */
	private Array createMockArray() { // NOPMD - for mock
		java.sql.Array array = new java.sql.Array() {

			@Override
			public void free() throws SQLException {
				// stub
			}

			@Override
			public Object getArray() throws SQLException {
				return null;
			}

			@Override
			public Object getArray(Map<String, Class<?>> map)
			        throws SQLException {
				return null;
			}

			@Override
			public Object getArray(long index, int count) throws SQLException {
				return null;
			}

			@Override
			public Object getArray(long index, int count,
			    Map<String, Class<?>> map) throws SQLException {
				return null;
			}

			@Override
			public int getBaseType() throws SQLException {
				return 0;
			}

			@Override
			public String getBaseTypeName() throws SQLException {
				return java.sql.Date.class.getName();
			}

			@Override
			public ResultSet getResultSet() throws SQLException {
				return null;
			}

			@Override
			public ResultSet getResultSet(Map<String, Class<?>> map)
			        throws SQLException {
				return null;
			}

			@Override
			public ResultSet getResultSet(long index, int count)
			        throws SQLException {
				return null;
			}

			@Override
			public ResultSet getResultSet(long index, int count,
			    Map<String, Class<?>> map) throws SQLException {
				return null;
			}

		};

		return array;
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.sql.Blob)} .
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatBlob() throws SQLException {

		String formattedBlob = baseSqlFormatter.format(blob);
		assertNotNull("formattedBlob should not be null", formattedBlob);
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.sql.Clob)} .
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatClob() throws SQLException {

		String formattedClob = baseSqlFormatter.format((Object) clob);
		assertNotNull("formattedClob should not be null", formattedClob);
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(Object)} with null.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatNull() throws SQLException {

		clob = null;
		String formattedClob = baseSqlFormatter.format((Object) clob);
		assertEquals("NULL", formattedClob);
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.sql.Array)} .
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatArray() throws SQLException {

		String formattedArray = baseSqlFormatter.format((Object) array);
		assertNotNull("formattedArray should not be null", formattedArray);
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.sql.Ref)} .
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatRef() throws SQLException {
		String formattedRef = baseSqlFormatter.format((Object) ref);
		assertNotNull("formattedRef should not be null", formattedRef);
	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.lang.String)}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatString() throws SQLException {
		String formattedString = baseSqlFormatter.format((Object) testString);
		assertNotNull("formattedString should not be null", formattedString);

	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(Object)} using Integer.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatInteger() throws SQLException {
		String formattedString =
		    baseSqlFormatter.format((Object) Integer.valueOf(1));
		assertNotNull("formattedString should not be null", formattedString);

	}

	/**
	 * Test method for {@link BaseSqlFormatter#format(java.lang.Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if a SQL error occurs.
	 */
	@Test
	public void testFormatObject() throws SQLException {

		Object blobObject = (Object) blob;

		String formattedBlob = baseSqlFormatter.format((Object) blobObject);
		assertNotNull("formattedBlob should not be null", formattedBlob);

	}

	/**
	 * Test method for {@link FormatterFactory#getFormatter(String)}.
	 * 
	 */
	@Test
	public void testGetFormatter() {

		SqlFormatter formatter = FormatterFactory.getInstance()
		    .getFormatter(FormatterFactory.ORACLE_FORMATTER);

		assertNotNull("formatter should not be null", formatter);

	}

	/**
	 * Test method for {@link FormatterFactory#getFormatter(String)} using a
	 * bogus formatter name.
	 */
	@Test
	public void testGetFormatterError() {

		SqlFormatter formatter =
		    FormatterFactory.getInstance().getFormatter("bogus");

		assertNull("formatter should be null", formatter);

	}

	/**
	 * Test method for {@link FormatterFactory#getFormatter(Connection)} using a
	 * live SQL Connection.
	 * 
	 * @throws SQLException
	 *             Possible SQL connection error.
	 */
	@Test
	public void testGetFormatterConn() throws SQLException {

		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		datasource.setUsername("reporting");
		datasource.setPassword("reporting");
		datasource.setUrl(
		    "jdbc:oracle:thin:@ldap://oradsqa.fcci-group.com:10389/DV01,"
		        + "cn=OracleContext,dc=fcci-group,dc=com");

		Connection conn = null; // NOPMD - Close by DbUtils

		try {

			conn = datasource.getConnection();
			SqlFormatter formatter =
			    FormatterFactory.getInstance().getFormatter(conn);

			assertNotNull("formatter should not be null", formatter);
			assertTrue(formatter instanceof OracleSqlFormatter);

		} finally {
			DbUtils.closeQuietly(conn);
		}

	}

	/**
	 * Test method for {@link FormatterFactory#getFormatter(Connection)} using a
	 * closed SQL Connection.
	 * 
	 * @throws SQLException
	 *             Possible SQL connection error.
	 */
	@Test
	public void testGetFormatterConnError() throws SQLException {

		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		datasource.setUsername("reporting");
		datasource.setPassword("reporting");
		datasource.setUrl("jdbc:derby:testdb;create=true");

		Connection conn = null; // NOPMD - Close by DbUtils

		try {

			conn = datasource.getConnection();
			conn.close();
			SqlFormatter formatter =
			    FormatterFactory.getInstance().getFormatter(conn);

			assertNull("formatter should be null", formatter);

		} finally {
			DbUtils.closeQuietly(conn);
		}

	}

	/**
	 * Test method for {@link FormatterFactory#getFormatter(Connection)} using a
	 * closed SQL Connection.
	 * 
	 * @throws SQLException
	 *             Possible SQL connection error.
	 */
	@Test
	public void testGetFormatterJavaDb() throws SQLException {

		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		datasource.setUsername("reporting");
		datasource.setPassword("reporting");
		datasource.setUrl("jdbc:derby:testdb;create=true");

		Connection conn = null; // NOPMD - Close by DbUtils

		try {

			conn = datasource.getConnection();
			SqlFormatter formatter =
			    FormatterFactory.getInstance().getFormatter(conn);

			assertNotNull("formatter should not be null", formatter);

		} finally {
			DbUtils.closeQuietly(conn);
		}

	}

}
