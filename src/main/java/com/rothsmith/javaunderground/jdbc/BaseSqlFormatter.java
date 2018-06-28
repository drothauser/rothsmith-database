/**
 * Title:
 * <p>
 * Description:
 * <p>
 * Copyright: Copyright (c) Troy Thompson, Bob Byron
 * <p>
 * Company: JavaUnderground
 * <p>
 * 
 * @author Troy Thompson, Bob Byron
 * @version 1.1
 */
package com.rothsmith.javaunderground.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLException;

/**
 * Base class for all database Formatters such as OracleFormatter.
 * 
 * @author Troy Thompson, Bob Byron
 */
public class BaseSqlFormatter implements SqlFormatter {

	/**
	 * String used for representing null objects. Value = 'NULL'.
	 */
	private static final String NULL = "NULL";

	/**
	 * Formats a blob to the following String "'&lt;Blob length = " +
	 * blob.length()+"&gt;'" This method's output will not translate directly
	 * into the database. It is informational only.
	 * 
	 * @param blob
	 *            The blob to be translated
	 * @return The String representation of the blob
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	protected final String format(Blob blob) throws SQLException {
		return "'<Blob length = " + blob.length() + ">'";
	}

	/**
	 * Formats a clob to the following String "'&lt;Clob length = " +
	 * clob.length()+"&gt;'" This method's output will not translate directly
	 * into the database. It is informational only.
	 * 
	 * @param clob
	 *            The clob to be translated
	 * @return The String representation of the clob
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	protected final String format(Clob clob) throws SQLException {
		return "'<Clob length = " + clob.length() + ">'";
	}

	/**
	 * Formats an Array to the following String "array.getBaseTypeName()" This
	 * method's output will not translate directly into the database. It is
	 * informational only.
	 * 
	 * @param array
	 *            The array to be translated
	 * @return The base name of the array
	 * @throws SQLException
	 *             thrown if problems formatting.
	 * 
	 */
	protected final String format(final Array array) throws SQLException {
		return array.getBaseTypeName();
	}

	/**
	 * Formats a Ref to the following String "ref.getBaseTypeName()" This
	 * method's output will not translate directly into the database. It is
	 * informational only.
	 * 
	 * @param ref
	 *            The ref to be translated
	 * @return The base name of the ref
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	protected final String format(final Ref ref) throws SQLException {
		return ref.getBaseTypeName();
	}

	/**
	 * Checks the String for null and returns "'" + string + "'".
	 * 
	 * @param string
	 *            String to be formatted
	 * @return formatted String (null returns "NULL")
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	protected final String format(final java.lang.String string)
	        throws SQLException {
		return "'" + string + "'";
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity" })
	public String format(final Object object) throws SQLException {
		if (object == null) {
			return NULL;
		}
		if (object instanceof Blob) {
			return format((Blob) object);
		}
		if (object instanceof Clob) {
			return format((Clob) object);
		}
		if (object instanceof Array) {
			return format((Array) object);
		}
		if (object instanceof Ref) {
			return format((Ref) object);
		}
		if (object instanceof String) {
			return format((String) object);
		}
		return object.toString();
	}
}