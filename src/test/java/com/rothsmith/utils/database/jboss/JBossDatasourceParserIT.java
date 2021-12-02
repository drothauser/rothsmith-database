/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.jboss;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.DatasourceParserException;
import net.rothsmith.utils.database.ValidatingDatasourceParser;
import net.rothsmith.utils.database.jboss.JBossDatasourceParser;

/**
 * Tests for {@link JBossDatasourceParser}.
 * 
 * @author drothauser
 * 
 */
public class JBossDatasourceParserIT {

	/**
	 * {@link DatasourceParser} to test.
	 */
	private DatasourceParser datasourceParser;

	/**
	 * Finds datasources.xml in the classpath (in src/test/resources).
	 */
	@Before
	public void setUp() {

		datasourceParser = new JBossDatasourceParser();
	}

	/**
	 * Test method for {@link JBossDatasourceParser#parse(File)}.
	 */
	@Test
	public void testParse() {

		URL url = ClassLoader.getSystemResource("datasources.xml");
		File datasourceFile = new File(url.getFile());

		Map<String, BasicDataSource> dsMap =
		    datasourceParser.parse(datasourceFile);

		assertFalse(dsMap == null || dsMap.isEmpty());
	}

	/**
	 * Test method for {@link ValidatingDatasourceParser#parse(File)}.
	 */
	@Test
	public void testValidatingParse() {

		DatasourceParser validatingParser =
		    new ValidatingDatasourceParser(datasourceParser);

		URL url = ClassLoader.getSystemResource("datasources.xml");
		File datasourceFile = new File(url.getFile());

		Map<String, BasicDataSource> dsMap =
		    validatingParser.parse(datasourceFile);

		assertFalse(dsMap == null || dsMap.isEmpty());
	}

	/**
	 * Test method for {@link ValidatingDatasourceParser#parse(File)} with bad
	 * datasource file.
	 */
	@Test(expected = DatasourceParserException.class)
	public void testValidatingParseError() {

		DatasourceParser validatingParser =
		    new ValidatingDatasourceParser(datasourceParser);

		URL url = ClassLoader.getSystemResource("bad-datasources.xml");
		File datasourceFile = new File(url.getFile());

		validatingParser.parse(datasourceFile);

	}

	/**
	 * Test method for {@link JBossDatasourceParser#parse(File)} using a file
	 * that doesn't exist.
	 */
	@Test(expected = DatasourceParserException.class)
	public void testParseBadFile() {

		DatasourceParser datasourceParser = new JBossDatasourceParser();

		datasourceParser.parse(new File("bogus.xml"));

	}

}
