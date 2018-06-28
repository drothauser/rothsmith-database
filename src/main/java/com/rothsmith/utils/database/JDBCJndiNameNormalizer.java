/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for making sure a JNDI/JDBC lookup name is suitable to be looked up in
 * the &quot;java:/comp/env/jdbc/&quot; context. The resulting name will use to
 * the following structure:
 * <p>
 * &quot;java:/comp/env/jdbc/&quot; + [Base JNDI name]
 * </p>
 * For example:
 * <ul>
 * <li>java:/jdbc/Ew3OracleDS --> java:/comp/env/jdbc/Ew3OracleDS</li>
 * <li>SharedUserOracleDS --> java:/comp/env/jdbc/SharedUserOracleDS</li>
 * </ul>
 * 
 * @version $Revison:$
 * 
 * @author drothauser
 * 
 */
public final class JDBCJndiNameNormalizer implements JndiNameNormalizer {

	/**
	 * Method for ensuring the given JNDI name suitable for a JNDI lookup that
	 * expects searches in the JDBC context (&quot;java:/comp/env/jdbc/&quot;).
	 * 
	 * @param jndiName
	 *            Raw JNDI name
	 * @return &quot;java:/comp/env/jdbc/&quot; + [Base JNDI name]
	 */
	public String normalize(String jndiName) {

		if (StringUtils.isBlank(jndiName)) {
			throw new IllegalArgumentException(
			    "jndiName argument is NULL or blank.");
		}

		String normalized =
		    jndiName.replaceFirst("(.*?)(?=[^/|:]+$)", "java:/comp/env/jdbc/");

		return normalized;
	}
}
