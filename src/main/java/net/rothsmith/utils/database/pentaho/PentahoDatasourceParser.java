/*
 * (c) 2016 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.pentaho;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.DatasourceParserException;

/**
 * @author drothauser
 *
 */
public class PentahoDatasourceParser implements DatasourceParser {

	/**
	 * SLF4J Logger for PentahoDatasourceParser.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(PentahoDatasourceParser.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, BasicDataSource> parse(File datasourceFile) {

		Properties props = new Properties();
		try {
			props.load(FileUtils.openInputStream(datasourceFile));

			Map<String, BasicDataSource> dsMap =
			    new HashMap<String, BasicDataSource>();

			// Group properties by JNDI portion of the property key:
			Map<Object, List<Entry<Object, Object>>> jndiPropMap =
			    props.entrySet().stream()
			        .collect(Collectors.groupingBy(e -> StringUtils
			            .substringBefore((String) e.getKey(), "/")));

			for (Map.Entry<Object, List<Entry<Object, Object>>> jndiEntry : jndiPropMap
			    .entrySet()) {
				String dsName = (String) jndiEntry.getKey();
				Map<String, String> dsPropertyMap =
				    new HashMap<String, String>();
				for (Entry<Object, Object> dsProps : jndiEntry.getValue()) {
					String propName = StringUtils
					    .substringAfter(dsProps.getKey().toString(), "/");
					String propValue = dsProps.getValue().toString();
					dsPropertyMap.put(propName, propValue);
				}
				dsMap.put(dsName, PentahoDataSourceFactory.INSTANCE
				    .createBasicDataSource(dsPropertyMap));
			}

			return dsMap;

		} catch (IOException e) {
			String errmsg = String.format("Error processing %s file: %s",
			    datasourceFile.getAbsoluteFile(), e.toString());
			LOGGER.error(errmsg, e);
			throw new DatasourceParserException(errmsg, e);
		}

	}

}
