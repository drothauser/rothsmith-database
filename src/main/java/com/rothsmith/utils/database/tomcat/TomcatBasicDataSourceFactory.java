/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.tomcat;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.rothsmith.utils.database.BasicDataSourceFactory;

/**
 * Factory to create an instance of {@link BasicDataSource} from a Tomcat
 * configuration file containing datasource resource information.
 * 
 * @author drothauser
 * 
 */
public enum TomcatBasicDataSourceFactory implements BasicDataSourceFactory {

	/**
	 * TomcatBasicDataSourceFactory enum instance.
	 */
	INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BasicDataSource createBasicDataSource(Map<String, String> dsProps) {

		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(dsProps.get("driverClassName"));
		bds.setUsername(dsProps.get("username"));
		bds.setPassword(dsProps.get("password"));
		bds.setUrl(dsProps.get("url"));
		bds.setInitialSize(Integer.parseInt(
		    StringUtils.defaultIfEmpty(dsProps.get("initialSize"), "1")));
		String maxActive = dsProps.get("maxActive");
		String maxTotal = dsProps.get("maxTotal");
		String max = StringUtils.isEmpty(maxTotal) ? maxActive : maxTotal;
		bds.setMaxActive(
		    Integer.parseInt(StringUtils.defaultIfEmpty(max, "1")));
		bds.setMaxIdle(Integer
		    .parseInt(StringUtils.defaultIfEmpty(dsProps.get("maxIdle"), "1")));
		bds.setMaxWait(Integer.parseInt(
		    StringUtils.defaultIfEmpty(dsProps.get("maxWait"), "5000")));
		bds.setTestOnBorrow(BooleanUtils.toBoolean(
		    StringUtils.defaultIfEmpty(dsProps.get("testOnBorrow"), "false")));
		bds.setValidationQuery(dsProps.get("validationQuery"));
		bds.setRemoveAbandoned(BooleanUtils.toBoolean(StringUtils
		    .defaultIfEmpty(dsProps.get("removeAbandoned"), "false")));
		bds.setRemoveAbandonedTimeout(Integer.parseInt(StringUtils
		    .defaultIfEmpty(dsProps.get("removeAbandonedTimeout"), "1")));
		bds.setLogAbandoned(BooleanUtils.toBoolean(
		    StringUtils.defaultIfEmpty(dsProps.get("logAbandoned"), "false")));

		return bds;
	}

}
