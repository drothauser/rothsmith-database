/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.cc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbcp.BasicDataSource;

import com.rothsmith.utils.database.BasicDataSourceFactory;

/**
 * Factory to create an instance of {@link BasicDataSource} from a Claim Center
 * configuration file containing datasource resource information.
 * 
 * @author drothauser
 * 
 */
public enum CCBasicDataSourceFactory implements BasicDataSourceFactory {

	/**
	 * CCBasicDataSourceFactory enum instance.
	 */
	INSTANCE;

	/**
	 * Driver {@link Map} for determining driver class for a JDBC URL.
	 */
	private static final Map<String, String> DRIVER_MAP = initDriverMap();

	/**
	 * Initializes the {@link #DRIVER_MAP}.
	 * 
	 * @return {@link Map} of JDBC drivers
	 */
	private static Map<String, String> initDriverMap() {
		// CHECKSTYLE:OFF magic number ok here
		Map<String, String> driverMap = new HashMap<String, String>(3);
		// CHECKSTYLE:ON
		driverMap.put("jdbc:oracle:", "oracle.jdbc.driver.OracleDriver");
		driverMap.put("jdbc:as400:", "com.ibm.as400.access.AS400JDBCDriver");
		driverMap.put("jdbc:sqlserver:",
		    "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		return driverMap;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicDataSource createBasicDataSource(Map<String, String> dsProps) {

		BasicDataSource bds = new BasicDataSource();

		String dburl = dsProps.get("dburl");
		Pattern jdbcUrlPattern = Pattern.compile("(jdbc:\\w+:).*");
		Matcher m = jdbcUrlPattern.matcher(dburl);
		if (m.matches()) {
			String jdbcKey = m.group(1);
			bds.setDriverClassName(DRIVER_MAP.get(jdbcKey));
			bds.setUsername(dsProps.get("username"));
			bds.setPassword(dsProps.get("password"));
			bds.setUrl(dsProps.get("dburl"));
		} else {
			throw new IllegalArgumentException(
			    dburl + " does is not a supported JDBC URL");
		}

		return bds;

	}

}
