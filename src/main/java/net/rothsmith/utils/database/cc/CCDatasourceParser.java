/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database.cc;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.rothsmith.encrypt.text.CCTextEncryptor;

import net.rothsmith.utils.database.DatasourceParser;
import net.rothsmith.utils.database.DatasourceParserException;

/**
 * Class for parsing and unmarshalling (deserializing) a Claim Center
 * gw_server\protected datasource XML file into a {@link Map} of logical
 * datasource names and their associated {@link BasicDataSource} object. The map
 * is stuctured as follows:
 * <ul>
 * <li>key = logical datasource name
 * <li>value = {@link BasicDataSource} object that contains datasource
 * connection properties.
 * </ul>
 * 
 * @author drothauser
 * 
 */
public class CCDatasourceParser implements DatasourceParser {

	/**
	 * SLF4J Logger for JBossDatasourceParser.
	 */
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(CCDatasourceParser.class);

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

			String xpathExpr = "/authentication/*";
			Object result =
			    xpath.evaluate(xpathExpr, source, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			int nodeCount = nodes.getLength();
			Map<String, String> dsPropertyMap =
			    new HashMap<String, String>(nodeCount);

			for (int i = 0; i < nodeCount; i++) {
				Node node = nodes.item(i);
				String propName = node.getNodeName();

				String nodeValue = StringUtils.trim(node.getTextContent());
				boolean isBase64 =
				    Base64.isArrayByteBase64(nodeValue.getBytes("UTF-8"));
				String propValue = isBase64
				    ? CCTextEncryptor.INSTANCE.decrypt(nodeValue) : nodeValue;

				dsPropertyMap.put(propName, propValue);
			}

			Map<String, BasicDataSource> dsMap =
			    new HashMap<String, BasicDataSource>(1);

			dsMap.put("ccdb", CCBasicDataSourceFactory.INSTANCE
			    .createBasicDataSource(dsPropertyMap));

			return dsMap;

		} catch (Exception e) {
			String errmsg = String.format("Error processing %s file: %s",
			    datasourceFile.getAbsoluteFile(), e.toString());
			LOGGER.error(errmsg, e);
			throw new DatasourceParserException(errmsg, e);
		}
	}
}
