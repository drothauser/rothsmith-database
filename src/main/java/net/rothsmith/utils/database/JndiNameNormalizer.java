/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database;

/**
 * A JNDI name normalizer.
 * 
 * @author drothauser
 * 
 */
public interface JndiNameNormalizer {

	/**
	 * Method for ensuring that the given JNDI name is suitable for a JNDI
	 * lookup.
	 * 
	 * @param jndiName
	 *            Raw JNDI name
	 * @return normalized JNDI name.
	 */
	String normalize(String jndiName);

}