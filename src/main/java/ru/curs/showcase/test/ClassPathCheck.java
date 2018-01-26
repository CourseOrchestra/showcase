package ru.curs.showcase.test;

import static org.junit.Assert.fail;

import java.io.*;
import java.sql.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.*;

/**
 * Проверка правильного порядка и наличия нужных элементов в Classpath.
 * 
 * @author den
 * 
 */
public class ClassPathCheck extends AbstractTestWithDefaultUserData {

	@Test
	public void webAppLibsShouldBeBeforeGWTandJRELibs() throws SAXException {
		try {
			XMLUtils.createSchemaForFile(new File(AppInfoSingleton.getAppInfo().getWebAppPath()
					+ "/WEB-INF/classes/schemas/action.xsd"));
		} catch (NoSuchFieldError e) {
			if ("W3C_XML_SCHEMA11_NS_URI".equals(e.getMessage())) {
				fail();
			}
		}
	}

	@Test(expected = XSLTTransformException.class)
	public final void saxonShouldBeMainXSLTransformer() throws SAXException, IOException,
			SQLException, TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(ClassPathCheck.class.getResourceAsStream("util/" + TEST_TEXT_SAMPLE_XML));

		Connection connection = ConnectionFactory.getInstance().acquire();
		try {
			SQLXML sqlxmlIn = XMLUtils.domToSQLXML(doc, connection);
			try (CallableStatement cs = getOutputByInputSQLXML(connection, sqlxmlIn);) {
				SQLXML sqlxmlOut = cs.getSQLXML(2);
				String xsltFileName = "test_bad.xsl";
				XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);
			}
		} finally {
			ConnectionFactory.getInstance().release(connection);
		}
	}
}
