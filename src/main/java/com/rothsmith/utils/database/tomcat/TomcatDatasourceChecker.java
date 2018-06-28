/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.tomcat;

import java.io.File;

import com.rothsmith.utils.database.DatasourceParser;
import com.rothsmith.utils.database.ValidatingDatasourceParser;

/**
 * Class to check a Tomcat Datasource file for valid connections.
 * 
 * @version $Id: TomcatDatasourceChecker.java 1160 2013-06-19 14:36:14Z drarch $
 * 
 * @author drothauser
 * 
 */
public final class TomcatDatasourceChecker {

	/**
	 * @param args
	 *            Expecting Tomcat Datasource file name. Be sure to include the
	 *            full path.
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			throw new IllegalArgumentException("File name missing.");
		}

		File datasourceFile = new File(args[0]);

		DatasourceParser validatingParser =
		    new ValidatingDatasourceParser(new TomcatDatasourceParser());

		validatingParser.parse(datasourceFile);

	}

	/**
	 * Private constructor to thwart instantiation.
	 */
	private TomcatDatasourceChecker() {
	}

}
