/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.jboss;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.DatasourceParserException;

/**
 * Class for parsing and unmarshalling (deserializing) a JBoss datasource XML
 * file into a {@link Map} of logical datasource names and their associated
 * {@link BasicDataSource} object. The map is stuctured as follows:
 * <ul>
 * <li>key = logical datasource name
 * <li>value = {@link BasicDataSource} object that contains datasource
 * connection properties.
 * </ul>
 * 
 * @author drothauser
 * 
 */
public class JBossDatasourceParser implements DatasourceParser {

	/**
	 * SLF4J Logger for JBossDatasourceParser.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(JBossDatasourceParser.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, BasicDataSource> parse(File datasourceFile) {

		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			InputSource source =
			    new InputSource(new FileInputStream(datasourceFile));

			String xpathExpr = "/datasources/local-tx-datasource";
			Object result =
			    xpath.evaluate(xpathExpr, source, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			int nodeCount = nodes.getLength();

			Map<String, BasicDataSource> dsMap =
			    new HashMap<String, BasicDataSource>(nodeCount);

			for (int i = 0; i < nodeCount; i++) {
				Node node = nodes.item(i);
				NodeList childNodes = node.getChildNodes();

				Map<String, String> dsPropertyMap =
				    new HashMap<String, String>(nodeCount);

				String dsName = null;
				int childNodeCount = childNodes.getLength();
				for (int j = 0; j < childNodeCount; j++) {
					Node childNode = childNodes.item(j);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						String propName = childNode.getNodeName();
						String propValue =
						    StringUtils.trim(childNode.getTextContent());
						if ("jndi-name".equalsIgnoreCase(propName)) {
							dsName = propValue;
						} else if ("connection-property"
						    .equalsIgnoreCase(propName)) {
							String connPropName = childNode.getAttributes()
							    .getNamedItem("name").getTextContent();
							if ("autoCommit".equalsIgnoreCase(connPropName)) {
								dsPropertyMap.put("defaultAutoCommit",
								    propValue);
							}
						} else {
							dsPropertyMap.put(propName, propValue);
						}
					}
				}
				if (dsName != null) {
					dsMap.put(dsName, JBossBasicDataSourceFactory.INSTANCE
					    .createBasicDataSource(dsPropertyMap));
				}
			}

			return dsMap;

		} catch (Exception e) {
			String errmsg = String.format("Error processing %s file: %s",
			    datasourceFile.getAbsoluteFile(), e.toString());
			LOGGER.error(errmsg, e);
			throw new DatasourceParserException(errmsg, e);
		}
	}
}
