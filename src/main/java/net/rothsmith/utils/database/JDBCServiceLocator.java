package net.rothsmith.utils.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Class for efficiently locating JDBC DataSource instances. It follows the
 * Service Locator pattern as defined in the
 * <a href="http://www.corej2eepatterns.com/">Core J2EE Patterns</a>. Because
 * JNDI lookups are potentially expensive operations, the class uses a HashMap
 * to store previously retrieved datasources.
 * 
 * @author Doug Rothauser
 * 
 */
public final class JDBCServiceLocator {

	/**
	 * Cache used for storing previously retrieved datasources
	 */
	private final Map<String, DataSource> cache =
	    new ConcurrentHashMap<String, DataSource>();

	/**
	 * Singleton instance of this {@link JDBCServiceLocator}
	 */
	private static final JDBCServiceLocator INSTANCE = new JDBCServiceLocator();

	/**
	 * Program for ensuring that a JNDI name is suitable to be lookup up in the
	 * &quot;java:/comp/env/jdbc/&quot; context.
	 */
	private static final JndiNameNormalizer NORMALIZER =
	    new JDBCJndiNameNormalizer();

	/**
	 * @return the single instance of JDBCServiceLocator.
	 */
	public static JDBCServiceLocator getInstance() {
		return INSTANCE;
	}

	/**
	 * Singleton constructor.
	 */
	private JDBCServiceLocator() {

	}

	/**
	 * This method returns a JDBC DataSource instance. It first tries to
	 * retrieve it from cache. If it is not found there, it will get it via a
	 * JNDI lookup.
	 * 
	 * @param jndiDsname
	 *            JNDI datasource name. If the full path isn't given, this
	 *            method will try to derive it based on the JNDI service
	 *            provider.
	 * @return JDBC DataSource object
	 * @throws NamingException
	 *             thrown if JNDI name is not found
	 */
	public DataSource getDataSource(final String jndiDsname)
	        throws NamingException {

		DataSource dataSource = null;

		String dsName = NORMALIZER.normalize(jndiDsname);

		if (cache.containsKey(dsName)) {
			dataSource = cache.get(dsName);
		} else {
			dataSource = (DataSource) InitialContext.doLookup(dsName);
			cache.put(dsName, dataSource);
		}

		return dataSource;
	}
}
