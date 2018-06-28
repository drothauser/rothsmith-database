package com.rothsmith.javaunderground.jdbc;

import java.sql.SQLException;

/**
 * Interface for all SQL formatters.
 * 
 * @author drothauser
 * 
 */
public interface SqlFormatter {

	/**
	 * This method formats a SQL object.
	 * 
	 * @param object
	 *            Object to be formatted
	 * @return formatted String
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	String format(final Object object) throws SQLException;

}