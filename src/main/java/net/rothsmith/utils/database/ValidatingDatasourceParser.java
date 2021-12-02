/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for parsing a datasource file. It will also validate each datasource in
 * the file. This class is a simple decorator that adds validation to the
 * {@link #parse(File)} method.
 * 
 * @version $Revision: 1705 $
 * 
 * @author $Author: drarch $
 * 
 */
public class ValidatingDatasourceParser implements DatasourceParser {

	/**
	 * SLF4J Logger for ValidatingDatasourceParser.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(ValidatingDatasourceParser.class);

	/**
	 * {@link DatasourceParser} to be decorated (i.e. wrapped).
	 */
	private final DatasourceParser datasourceParser;

	/**
	 * Constructor for setting the {@link DatasourceParser} object to be
	 * decorated.
	 * 
	 * @param datasourceParser
	 *            {@link DatasourceParser} to be decorated
	 */
	public ValidatingDatasourceParser(DatasourceParser datasourceParser) {
		this.datasourceParser = datasourceParser;
	}

	/**
	 * This method checks that a valid connection can be made using the given
	 * {@link BasicDataSource}.
	 * 
	 * @param dsName
	 *            Logical name of the datasource.
	 * @param bds
	 *            {@link BasicDataSource} object.
	 * @return true if can connect to the datasource, else false.
	 */
	private boolean isOk(String dsName, BasicDataSource bds) {

		boolean ok = true;

		Connection conn = null; // NOPMD:DbUtils.closeQuietly closes it

		LOGGER.info("Checking " + dsName + "...");

		try {
			conn = bds.getConnection();
			LOGGER.info(dsName + " OK.");
		} catch (SQLException e) {
			ok = false;
			LOGGER.error(String.format(
			    "Could not connect using properties: %s%n%n%s", bds, e));
		} finally {
			DbUtils.closeQuietly(conn);
		}

		return ok;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, BasicDataSource> parse(File datasourceFile) {

		List<String> errantDatasources = new ArrayList<String>();

		Map<String, BasicDataSource> dsMap =
		    datasourceParser.parse(datasourceFile);
		for (Entry<String, BasicDataSource> entry : dsMap.entrySet()) {
			String dsName = entry.getKey();
			if (!isOk(dsName, entry.getValue())) {
				errantDatasources.add(dsName);
			}
		}

		if (!CollectionUtils.isEmpty(errantDatasources)) {
			String errmsg = String.format(
			    "%nCould not connect to the following datasources:%n %s",
			    StringUtils.join(errantDatasources, "\n"));
			throw new DatasourceParserException(errmsg);
		}

		return dsMap;
	}

}
