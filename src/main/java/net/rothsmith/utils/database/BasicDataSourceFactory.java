/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Factory to create an instance of {@link BasicDataSource} from an application
 * server configuration file containing datasource resource information.
 * 
 * @author drothauser
 * 
 */
public interface BasicDataSourceFactory {

	/**
	 * This method creates an instance of {@link BasicDataSource} given a
	 * {@link Map} of key/value pairs derived from an application server
	 * configuration file containing datasource resource information.
	 * 
	 * @param dsProps
	 *            a {@link Map} of datasource key/value pairs.
	 * @return a new instance of {@link BasicDataSource}
	 */
	BasicDataSource createBasicDataSource(Map<String, String> dsProps);

}