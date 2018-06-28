/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.pentaho;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import com.rothsmith.utils.database.BasicDataSourceFactory;

/**
 * Factory to create an instance of {@link BasicDataSource} from a Tomcat
 * configuration file containing datasource resource information.
 * 
 * @author drothauser
 * 
 */
public enum PentahoDataSourceFactory implements BasicDataSourceFactory {

	/**
	 * PentahoDataSourceFactory enum instance.
	 */
	INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicDataSource createBasicDataSource(Map<String, String> dsProps) {

		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(dsProps.get("driver"));
		bds.setUsername(dsProps.get("user"));
		bds.setPassword(dsProps.get("password"));
		bds.setUrl(dsProps.get("url"));

		return bds;
	}

}
