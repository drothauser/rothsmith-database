/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.jboss;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import net.rothsmith.utils.database.BasicDataSourceFactory;

/**
 * Factory to create an instance of {@link BasicDataSource} from a JBoss from a
 * JBoss datasource configuration file.
 * 
 * @author drothauser
 * 
 */
public enum JBossBasicDataSourceFactory implements BasicDataSourceFactory {

	/**
	 * JBossBasicDataSourceFactory enum instance.
	 */
	INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicDataSource createBasicDataSource(Map<String, String> dsProps) {

		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(dsProps.get("driver-class"));
		bds.setUsername(dsProps.get("user-name"));
		bds.setPassword(dsProps.get("password"));
		bds.setUrl(dsProps.get("connection-url"));
		bds.setInitialSize(Integer.parseInt(
		    StringUtils.defaultIfEmpty(dsProps.get("min-pool-size"), "1")));
		bds.setMaxActive(Integer.parseInt(
		    StringUtils.defaultIfEmpty(dsProps.get("max-pool-size"), "1")));
		bds.setMaxWait(Integer.parseInt(StringUtils
		    .defaultIfEmpty(dsProps.get("blocking-timeout-millis"), "5000")));
		bds.setValidationQuery(dsProps.get("check-valid-connection-sql"));
		bds.setDefaultAutoCommit(BooleanUtils.toBoolean(StringUtils
		    .defaultIfEmpty(dsProps.get("defaultAutoCommit"), "true")));

		return bds;

	}

}
