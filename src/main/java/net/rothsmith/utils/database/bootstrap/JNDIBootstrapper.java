/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.bootstrap;

import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.JndiDatasourceBootstrapper;
import net.rothsmith.utils.database.JndiDatasourceBootstrapperException;
import net.rothsmith.utils.database.cc.CCDatasourceParser;
import net.rothsmith.utils.database.jboss.JBossDatasourceParser;
import net.rothsmith.utils.database.tomcat.TomcatDatasourceParser;

/**
 * Utility class to bootstrap datasource files for access via JNDI.
 * 
 * @author drothauser
 * 
 */
public enum JNDIBootstrapper {

	/**
	 * JBoss Datasource Bootstrapper.
	 */
	JBOSS(new JBossDatasourceParser()),

	/**
	 * Tomcat Datasource Bootstrapper.
	 */
	TOMCAT(new TomcatDatasourceParser()),

	/**
	 * Claim Center Datasource Bootstrapper.
	 */
	CC(new CCDatasourceParser());

	/**
	 * SLF4J Logger for JNDIBootstrapper.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JNDIBootstrapper.class);

	/**
	 * {@link JndiDatasourceBootstrapper} for the particular enum.
	 */
	private JndiDatasourceBootstrapper bootstrapper;

	/**
	 * Constructor that creates an instance of
	 * {@link JndiDatasourceBootstrapper} for an application server specific
	 * implementation of {@link DatasourceParser}.
	 * 
	 * @param datasourceParser
	 *            A {@link DatasourceParser} implementation
	 */
	JNDIBootstrapper(DatasourceParser datasourceParser) {
		bootstrapper = new JndiDatasourceBootstrapper(datasourceParser);
	}

	/**
	 * This method bootstraps JNDI/JDBC objects using the given JNDI datasource
	 * file.
	 * 
	 * @param dsFile
	 *            The datasource file to bootstrap. It's expected to be on the
	 *            classpath.
	 * @throws JndiDatasourceBootstrapperException
	 *             possible {@link JndiDatasourceBootstrapperException}
	 */
	/**
	 * @param dsFile
	 *            JNDI datasource file expected to be on the classpath.
	 * @throws JndiDatasourceBootstrapperException
	 *             possible bootstrap error
	 */
	public void bootstrapDS(String dsFile)
	        throws JndiDatasourceBootstrapperException {

		LOGGER.info("Bootstrapping JNDI datasources using "
		    + "JNDI/JDBC configuration file: " + dsFile);

		URL url = ClassLoader.getSystemResource(dsFile);

		if (url == null) {

			String errmsg =
			    "Datasource file: " + dsFile + " could not be found.";

			LOGGER.error(errmsg);

			throw new JndiDatasourceBootstrapperException(errmsg);

		} else {

			bootstrapper.bind(FileUtils.toFile(url));
		}
	}

}
