/***************************************************************************
 * (c) 2009 Rothsmith, LLC All Rights Reserved.
 ***************************************************************************/
package net.rothsmith.javaunderground.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for fetching the appropriate formatter a database implementation.
 * 
 * @author drothauser
 * 
 */
public final class FormatterFactory {

	/**
	 * SLF4J Logger for FormatterFactory.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(FormatterFactory.class);

	/**
	 * Constant for choosing the Oracle formatter.
	 */
	public static final String ORACLE_FORMATTER = "Oracle";

	/**
	 * Constant for choosing the DB2 formatter.
	 */
	public static final String DB2_FORMATTER = "DB2 UDB for AS/400";

	/**
	 * Constant for choosing the default formatter.
	 */
	public static final String DEFAULT_FORMATTER = "default";

	/**
	 * Singleton instance of the FormatterFactory.
	 */
	private static final FormatterFactory INSTANCE = new FormatterFactory();

	/**
	 * List of {@link SqlFormatter}s.
	 */
	private final Map<String, SqlFormatter> formatters =
	    new ConcurrentHashMap<String, SqlFormatter>();

	/**
	 * Private constructor - initializes all available DBMS formatters.
	 */
	private FormatterFactory() {
		BaseSqlFormatter baseSqlFormatter = new BaseSqlFormatter();
		formatters.put(DEFAULT_FORMATTER,
		    new DefaultSqlFormatter(baseSqlFormatter));
		formatters.put(ORACLE_FORMATTER,
		    new OracleSqlFormatter(baseSqlFormatter));
		formatters.put(DB2_FORMATTER, new DB2SqlFormatter(baseSqlFormatter));
	}

	/**
	 * Method to return the singleton instance of FormatterFactory.
	 * 
	 * @return {@link FormatterFactory} instance
	 */
	public static FormatterFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Factory method to return a {@link SqlFormatter} object based on the
	 * database product name specified in the JDBC {@link DatabaseMetaData}
	 * object from the {@link Connection}.
	 * 
	 * @param conn
	 *            JDBC Connection
	 * @return the {@link SqlFormatter} for the JDBC {@link Connection}.
	 */
	public SqlFormatter getFormatter(Connection conn) {

		SqlFormatter formatter = null;

		try {

			DatabaseMetaData metaData = conn.getMetaData();
			String dbProductName = metaData.getDatabaseProductName();
			formatter = formatters.get(dbProductName);
			if (formatter == null) {
				formatter = formatters.get(DEFAULT_FORMATTER);
			}
		} catch (SQLException e) {
			String errmsg = "SQLException caught in getFormatter(conn): " + e;
			LOGGER.error(errmsg, e);
		}

		return formatter;
	}

	/**
	 * Factory method to return a {@link SqlFormatter} object based on the
	 * database product name specified in the JDBC {@link DatabaseMetaData}
	 * object from the {@link Connection}.
	 * 
	 * @param dbProductName
	 *            Database product name
	 * @return {@link SqlFormatter} for the specified database product name.
	 */
	public SqlFormatter getFormatter(String dbProductName) {

		SqlFormatter formatter = null;
		formatter = formatters.get(dbProductName);
		if (formatter == null) {
			String errmsg = "Could not find formatter for " + dbProductName;
			LOGGER.error(errmsg);
		}

		return formatter;
	}

}
