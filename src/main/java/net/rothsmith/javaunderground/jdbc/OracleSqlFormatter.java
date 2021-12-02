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
package net.rothsmith.javaunderground.jdbc;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * OracleSqlFormatter formats Oracle specific types. These include Calendar,
 * Date, Time, and TimeStamps. Generic types are handled by BaseSqlFormatter.
 * 
 * @author Troy Thompson, Bob Byron
 */
public class OracleSqlFormatter implements SqlFormatter {

	/**
	 * Format of Oracle date: 'YYYY-MM-DD HH24:MI:SS'.
	 */
	private static final String YMD24 = "'YYYY-MM-DD HH24:MI:SS'";

	/**
	 * Java timestamp format: 'yyyy-MM-dd hh:mm:ss'.
	 */
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Base SQL formatter.
	 */
	private final SqlFormatter baseFormatter;

	/**
	 * Constructor setting the the BaseSqlFormatter field.
	 * 
	 * @param baseSqlFormatter
	 *            the {@link SqlFormatter} that this class decorates (i.e
	 *            wraps).
	 */
	public OracleSqlFormatter(SqlFormatter baseSqlFormatter) {
		this.baseFormatter = baseSqlFormatter;
	}

	/**
	 * Formats Date object into Oracle TO_DATE String.
	 * 
	 * @param date
	 *            Date to be formatted
	 * @return formatted TO_DATE function
	 */
	private String format(final Date date) {
		String dateStr = DateFormatUtils.format(date, TIMESTAMP_FORMAT);
		return "TO_DATE('" + dateStr + "'," + YMD24 + ")";
	}

	/**
	 * Formats Time object into Oracle TO_DATE String.
	 * 
	 * @param time
	 *            Time to be formatted
	 * @return formatted TO_DATE function
	 */
	private String format(final Time time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date(time.getTime()));
		return "TO_DATE('" + cal.get(Calendar.HOUR_OF_DAY) + ":"
		    + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)
		    + "','HH24:MI:SS')";
	}

	/**
	 * Formats Timestamp object into Oracle TO_DATE String.
	 * 
	 * @param timestamp
	 *            Timestamp to be formatted
	 * @return formatted TO_DATE function
	 */
	private String format(final Timestamp timestamp) {
		long time = timestamp.getTime();
		String dateStr = DateFormatUtils.format(time, TIMESTAMP_FORMAT);
		return "TO_DATE('" + dateStr + "'," + YMD24 + ")";
	}

	/**
	 * Formats object to an Oracle specific formatted function.
	 * 
	 * @param object
	 *            Object to be formatted.
	 * @return formatted Oracle function or "NULL" if o is null.
	 * @throws SQLException
	 *             thrown if problems formatting.
	 */
	@SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity" })
	public final String format(final Object object) throws SQLException {
		if (object == null) {
			return "NULL";
		}
		if (object instanceof Date) {
			return format((Date) object);
		}
		if (object instanceof Time) {
			return format((Time) object);
		}
		if (object instanceof Timestamp) {
			return format((Timestamp) object);
		}
		// if object not in one of our overridden methods, send to super class
		return baseFormatter.format(object);

	}
}