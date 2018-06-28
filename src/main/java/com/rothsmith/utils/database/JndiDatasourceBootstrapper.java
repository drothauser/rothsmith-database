/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import java.io.File;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class constructs and binds JDBC datasources to a JNDI tree provided by
 * the Sun Filesystem JNDI service provider. It is meant to be used in
 * stand-alone applications.
 * 
 * <p>
 * The following code snippet shows its basic usage. Note that the
 * <code>bind</code> method argument is the name of a datasource XML file that's
 * expected to exist in the Classpath.
 * 
 * <pre>
 * JndiDatasourceBootstrapper bootstrapper = new JndiDatasourceBootstrapper();
 * bootstrapper.bind(&quot;datasources.xml&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author drothauser
 * 
 */
public class JndiDatasourceBootstrapper {

	/**
	 * SLF4J Logger for JndiDatasourceBootstrapper.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JndiDatasourceBootstrapper.class);

	/**
	 * Separator character for JNDI URIs.
	 * <ul>
	 * <li>WINDOWS = '/'</li>
	 * <li>*NIX = '_'</li>
	 * </ul>
	 */
	public static final String JNDI_SEP_CHAR =
	    (SystemUtils.IS_OS_WINDOWS) ? "/" : "_";

	/**
	 * JNDI/JDBC subcontext.
	 */
	private static final String JDBC_SUBCONTEXT =
	    String.format("java:%scomp%senv%sjdbc%s", JNDI_SEP_CHAR, JNDI_SEP_CHAR,
	        JNDI_SEP_CHAR, JNDI_SEP_CHAR);

	/**
	 * Naming context for <code>comp/env/jdbc</code>.
	 */
	private static final Context CTX = initContext();

	/**
	 * {@link DatasourceParser} instance for parsing a datasource file.
	 */
	private DatasourceParser datasourceParser;

	/**
	 * Default constructor - Creates the java:comp/env/jdbc/ context.
	 * 
	 * @param datasourceParser
	 *            {@link DatasourceParser} instance for parsing a datasource
	 *            file
	 */
	public JndiDatasourceBootstrapper(DatasourceParser datasourceParser) {

		this.datasourceParser = datasourceParser;
	}

	/**
	 * This method parses a datasource XML file (taken from JBoss), creates JDBC
	 * DataSource objects and binds them to the JNDI context. It's expected that
	 * the datasource file exists in the Classpath.
	 * 
	 * @param datasourceFile
	 *            Datasource XML file. It is expected to exist in the classpath.
	 */
	public void bind(File datasourceFile) {

		Map<String, BasicDataSource> dsMap =
		    datasourceParser.parse(datasourceFile);

		try {
			for (Map.Entry<String, BasicDataSource> entry : dsMap.entrySet()) {
				String jndiName = entry.getKey();
				BasicDataSource basicDataSource = entry.getValue();
				bindDatasource(jndiName, basicDataSource);
			}
		} catch (NamingException e) {
			String errmsg = String.format("Error processing %s file: %s",
			    datasourceFile, e.toString());
			LOGGER.error(errmsg, e);
			throw new DatasourceParserException(errmsg, e);
		}

	}

	/**
	 * This method creates a JDBC datasource using DBCP and binds it to JNDI.
	 * 
	 * @param jndiName
	 *            JNDI name for the datasource
	 * @param bds
	 *            {@link BasicDataSource} object
	 * 
	 * @throws NamingException
	 *             possible JNDI error
	 */
	private void bindDatasource(String jndiName, BasicDataSource bds)
	        throws NamingException {

		LOGGER.info("Binding " + jndiName);

		CTX.rebind(JDBC_SUBCONTEXT + jndiName, bds);

	}

	/**
	 * Initialize the JNDI context creating a subcontext of
	 * &quot;java:/comp/env/jdbc&quot;.
	 * 
	 * @return {@link InitialContext} instance
	 */
	private static Context initContext() {

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
		    "org.osjava.sj.memory.MemoryContextFactory");
		System.setProperty("org.osjava.sj.jndi.shared", "true");

		try {
			InitialContext ctx = new InitialContext();
			ctx.createSubcontext(JDBC_SUBCONTEXT);
			return ctx;
		} catch (NamingException e) {
			throw new DatasourceParserException("Unable to initialize JNDI", e);
		}
	}

	/**
	 * @return the JNDI context
	 */
	public Context getCtx() {
		return CTX;
	}

}
