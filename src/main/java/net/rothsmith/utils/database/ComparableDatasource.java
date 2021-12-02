/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps {@link BasicDataSource} to make it easy to compare certain fields.
 * 
 * @version $Revison:$
 * 
 * @author drothauser
 * 
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class ComparableDatasource {

	/**
	 * SLF4J Logger for ComparableDatasource.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(ComparableDatasource.class);

	/**
	 * The fully qualified Java class name of the JDBC driver to be used.
	 */
	private final String driverClassName;

	/**
	 * The connection URL to be passed to our JDBC driver to establish a
	 * connection.
	 */
	private final String url;

	/**
	 * The connection username to be passed to our JDBC driver to establish a
	 * connection.
	 */
	private final String username;

	/**
	 * The connection password to be passed to our JDBC driver to establish a
	 * connection.
	 */
	private final String password;

	/**
	 * The initial number of connections that are created when the pool is
	 * started.
	 * 
	 */
	private final int initialSize;

	/**
	 * The maximum number of active connections that can be allocated from this
	 * pool at the same time, or negative for no limit.
	 */
	private final int maxActive;

	/**
	 * The maximum number of milliseconds that the pool will wait (when there
	 * are no available connections) for a connection to be returned before
	 * throwing an exception, or <= 0 to wait indefinitely.
	 */
	private final long maxWait;

	/**
	 * The SQL query that will be used to validate connections from this pool
	 * before returning them to the caller. If specified, this query
	 * <strong>MUST</strong> be an SQL SELECT statement that returns at least
	 * one row.
	 */
	private final String validationQuery;

	/**
	 * Formatter to display a field difference.
	 */
	private static final String DIFF_FORMATTER =
	    "current value = %s, new value = %s";

	/**
	 * Constructor that populates the fields to compare from the given
	 * {@link BasicDataSource} object.
	 * 
	 * @param bds
	 *            the {@link BasicDataSource} to compare
	 */
	public ComparableDatasource(BasicDataSource bds) {
		driverClassName = bds.getDriverClassName();
		url = bds.getUrl();
		initialSize = bds.getInitialSize();
		maxActive = bds.getMaxActive();
		maxWait = bds.getMaxWait();
		password = bds.getPassword();
		username = bds.getUsername();
		validationQuery = bds.getValidationQuery();
	}

	// CHECKSTYLE:OFF
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.NPathComplexity" })
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		boolean isEqual = true;
		Map<String, String> changedFields = new HashMap<String, String>();
		ComparableDatasource other = (ComparableDatasource) obj;

		if (!StringUtils.equals(driverClassName, other.driverClassName)) {
			isEqual = false;
			changedFields.put("driverClassName", String.format(DIFF_FORMATTER,
			    driverClassName, other.driverClassName));
		}
		if (initialSize != other.initialSize) {
			isEqual = false;
			changedFields.put("initialSize",
			    String.format(DIFF_FORMATTER, initialSize, other.initialSize));
		}
		if (maxActive != other.maxActive) {
			isEqual = false;
			changedFields.put("maxActive",
			    String.format(DIFF_FORMATTER, maxActive, other.maxActive));
		}
		if (maxWait != other.maxWait) {
			isEqual = false;
			changedFields.put("maxWait",
			    String.format(DIFF_FORMATTER, maxWait, other.maxWait));
		}
		if (!StringUtils.equals(password, other.password)) {
			isEqual = false;
			changedFields.put("password",
			    String.format(
			        DIFF_FORMATTER, StringUtils.repeat("*",
			            StringUtils.length(password)),
			    StringUtils.repeat("*", StringUtils.length(other.password))));
		}
		if (!StringUtils.equals(url, other.url)) {
			isEqual = false;
			changedFields.put("url",
			    String.format(DIFF_FORMATTER, url, other.url));
		}
		if (!StringUtils.equals(username, other.username)) {
			isEqual = false;
			changedFields.put("username",
			    String.format(DIFF_FORMATTER, username, other.username));
		}

		if (!StringUtils.equals(validationQuery, other.validationQuery)) {
			isEqual = false;
			changedFields.put("validationQuery", String.format(DIFF_FORMATTER,
			    validationQuery, other.validationQuery));
		}

		if (!isEqual) {
			LOGGER.info("Datasources are different:");
			for (Map.Entry<String, String> field : changedFields.entrySet()) {
				LOGGER.info(field.getKey() + ": " + field.getValue());
			}
		}

		return isEqual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		// CHECKSTYLE:OFF magic numbers ok here
		return new HashCodeBuilder(17, 37).append(driverClassName)
		    .append(initialSize).append(maxActive).append(maxWait)
		    .append(password).append(url).append(username)
		    .append(validationQuery).toHashCode();
		// CHECKSTYLE:ON
	}

	// CHECKSTYLE:ON

	/**
	 * Returns the string representation of this object. The string will contain
	 * of each field and value of the object.
	 * 
	 * @return String representation of the MailDTO instance.
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
		    ToStringStyle.MULTI_LINE_STYLE);
	}

}
