/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.jboss;

import java.io.File;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.ValidatingDatasourceParser;

/**
 * Class to check a JBoss Datasource file for valid connections.
 * 
 * @author drothauser
 * 
 */
public final class JBossDatasourceChecker {

	/**
	 * @param args
	 *            Expecting JBoss Datasource file name. Be sure to include the
	 *            full path.
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			throw new IllegalArgumentException("File name missing.");
		}

		File datasourceFile = new File(args[0]);

		DatasourceParser validatingParser =
		    new ValidatingDatasourceParser(new JBossDatasourceParser());

		validatingParser.parse(datasourceFile);

	}

	/**
	 * Private constructor to thwart instantiation.
	 */
	private JBossDatasourceChecker() {
	}

}
