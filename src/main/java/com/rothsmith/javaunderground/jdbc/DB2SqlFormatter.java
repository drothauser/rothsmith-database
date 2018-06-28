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

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * DB2SqlFormatter formats DB2 specific types. These include Calendar, Date,
 * Time, and TimeStamps. Generic types are handled by BaseSqlFormatter.
 * 
 * @author Troy Thompson, Bob Byron
 */
public class DB2SqlFormatter implements SqlFormatter {

	/**
	 * Java date format: 'yyyy-MM-dd HH:mm:ss'.
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Java timestamp format: 'yyyy-MM-dd HH:mm:ss'.
	 */
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Java times format: 'HH:mm:ss'.
	 */
	private static final String TIME_FORMAT = "HH:mm:ss";

	/**
	 * Base SQL formatter.
	 */
	private final SqlFormatter baseFormatter;

	/**
	 * Constructor setting the the BaseSqlFormatter field.
	 * 
	 * @param baseSqlFormatter
	 *            the {@link SqlFormatter} that this class will wrap.
	 */
	public DB2SqlFormatter(final SqlFormatter baseSqlFormatter) {
		this.baseFormatter = baseSqlFormatter;
	}

	/**
	 * Formats Calendar object into Oracle TO_DATE String.
	 * 
	 * @param cal
	 *            Calendar to be formatted
	 * @return formatted DATE function
	 */
	private String format(final Calendar cal) {
		String dateStr = DateFormatUtils.format(cal, DATE_FORMAT);
		return "DATE('" + dateStr + "')";
	}

	/**
	 * Formats Date object into Oracle TO_DATE String.
	 * 
	 * @param date
	 *            Date to be formatted
	 * @return formatted DATE function
	 */
	private String format(final Date date) {
		String dateStr = DateFormatUtils.format(date, DATE_FORMAT);
		return "DATE('" + dateStr + "')";
	}

	/**
	 * Formats Time object into Oracle TO_DATE String.
	 * 
	 * @param time
	 *            Time to be formatted
	 * @return formatted TIME function
	 */
	private String format(final Time time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date(time.getTime()));
		String timeStr = DateFormatUtils.format(cal.getTime(), TIME_FORMAT);
		return "TIME('" + timeStr + "')";
	}

	/**
	 * Formats Timestamp object into Oracle TO_DATE String.
	 * 
	 * @param timestamp
	 *            Timestamp to be formatted
	 * @return formatted TIMESTAMP function
	 */
	private String format(final Timestamp timestamp) {
		long time = timestamp.getTime();
		String dateStr = DateFormatUtils.format(time, TIMESTAMP_FORMAT);
		return "TIMESTAMP('" + dateStr + "')";
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
	@Override
	@SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity" })
	public final String format(final Object object) throws SQLException {
		if (object == null) {
			return "NULL";
		}
		if (object instanceof Calendar) {
			return format((Calendar) object);
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
		// if object not in one of our overridden methods, send to decorated
		// class:
		return baseFormatter.format(object);

	}
}
