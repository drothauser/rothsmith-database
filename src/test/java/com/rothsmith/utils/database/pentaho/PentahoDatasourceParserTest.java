/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database.pentaho;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.rothsmith.utils.database.DatasourceParser;
import com.rothsmith.utils.database.DatasourceParserException;
import com.rothsmith.utils.database.ValidatingDatasourceParser;
import com.rothsmith.utils.database.tomcat.TomcatDatasourceParser;

/**
 * Tests for {@link PentahoDatasourceParser}.
 * 
 * @author drothauser drothauser
 * 
 */
@Ignore
public class PentahoDatasourceParserTest {

	/**
	 * {@link DatasourceParser} to test.
	 */
	private DatasourceParser datasourceParser;

	/**
	 * Finds datasources.xml in the classpath (in src/test/resources).
	 */
	@Before
	public void setUp() {

		datasourceParser = new PentahoDatasourceParser();
	}

	/**
	 * Test method for {@link PentahoDatasourceParser#parse(File)}.
	 */
	@Test
	public void testParse() {

		URL url = ClassLoader.getSystemResource("jdbc.properties");
		File datasourceFile = new File(url.getFile());

		Map<String, BasicDataSource> dsMap =
			datasourceParser.parse(datasourceFile);

		assertFalse(MapUtils.isEmpty(dsMap));

	}

	/**
	 * Test method for {@link ValidatingDatasourceParser#parse(File)}.
	 */
	@Test
	public void testValidatingParse() {

		DatasourceParser validatingParser =
			new ValidatingDatasourceParser(datasourceParser);

		URL url = ClassLoader.getSystemResource("jdbc.properties");
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

		URL url = ClassLoader.getSystemResource("bad-jdbc.properties");
		File datasourceFile = new File(url.getFile());

		validatingParser.parse(datasourceFile);

	}

	/**
	 * Test method for {@link TomcatDatasourceParser#parse(File)} using a file
	 * that doesn't exist.
	 */
	@Test(expected = DatasourceParserException.class)
	public void testParseBadFile() {

		DatasourceParser datasourceParser = new TomcatDatasourceParser();

		datasourceParser.parse(new File("bogus.properties"));

	}

}
