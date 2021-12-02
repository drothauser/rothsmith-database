/*
 * (c) 2013 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.cc;

import java.io.File;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.ValidatingDatasourceParser;

/**
 * Class to check a Claim Center Datasource file for valid connections.
 * 
 * @author drothauser
 * 
 */
public final class CCDatasourceChecker {

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
		    new ValidatingDatasourceParser(new CCDatasourceParser());

		validatingParser.parse(datasourceFile);

	}

	/**
	 * Private constructor to thwart instantiation.
	 */
	private CCDatasourceChecker() {
	}

}
