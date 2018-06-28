/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import java.io.File;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Interface for datasource file parsers.
 * 
 * @author drothauser
 */
public interface DatasourceParser {

	/**
	 * Parse the datasource file and return a {@link Map} as:
	 * <ul>
	 * <li>key = logical datasource name</li>
	 * <li>value = {@link BasicDataSource} object that contains datasource
	 * connection properties.</li>
	 * </ul>
	 * 
	 * @param datasourceFile
	 *            datasource file
	 * @return {@link Map} as key = JNDI name, value = {@link BasicDataSource}
	 *         object
	 */
	Map<String, BasicDataSource> parse(final File datasourceFile);

}