package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelFactory;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.ReflectionUtils;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;

/**
 * Тесты класса XMLProcessor.
 */
public class XMLUtilsTest extends AbstractTestWithDefaultUserData {

	private static final String TEST_DOC = "<test/>";
	private static final String TEST_GOOD_XSD = "test_good.xsd";
	private static final String TEST_BAD_XSD = "test_bad.xsd";
	private static final String TEST_GOOD_XSL = "test_good.xsl";
	private static final String TEST_STR2 = "учреждениях";
	private static final String TEST_STR1 = ">II. Индикаторы задач проекта</td>";

	@Test
	public final void testXSLTransformForSQLXML() throws SAXException, IOException, SQLException,
			TransformerException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		Connection connection = ConnectionFactory.getInstance().acquire();
		try {
			SQLXML sqlxmlIn = XMLUtils.domToSQLXML(doc, connection);
			try (CallableStatement cs = getOutputByInputSQLXML(connection, sqlxmlIn);) {
				SQLXML sqlxmlOut = cs.getSQLXML(2);
				String xsltFileName = TEST_GOOD_XSL;
				String out = XMLUtils.xsltTransform(sqlxmlOut, xsltFileName);

				assertTrue(out.indexOf(TEST_STR1) > -1);
				assertTrue(out.indexOf(TEST_STR2) > -1);
			}
		} finally {
			ConnectionFactory.getInstance().release(connection);
		}
	}

	@Test
	public final void testXSLTransformForDocument() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsltFileName = TEST_GOOD_XSL;

		String out = XMLUtils.xsltTransform(doc, xsltFileName);

		assertTrue(out.indexOf(TEST_STR1) > -1);

		assertTrue(out.indexOf(TEST_STR2) > -1);

	}

	@Test
	public final void testXSLTransformForInputStream() {

		String xsltFileName = TEST_GOOD_XSL;

		String out =
			XMLUtils.xsltTransform(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
					xsltFileName);

		assertTrue(out.indexOf(TEST_STR1) > -1);

		assertTrue(out.indexOf(TEST_STR2) > -1);
	}

	@Test
	public final void testXSLTransformForGrid() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream("TestGridSample.xml"));

		ByteArrayOutputStream out = XMLUtils.xsltTransformForGrid(doc);
		assertNotNull(out);

		doc = db.parse(new ByteArrayInputStream(out.toByteArray()));

		NodeList nl = doc.getElementsByTagName("Workbook");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Worksheet");
		assertEquals(1, nl.getLength());

		nl = doc.getElementsByTagName("Styles");
		assertEquals(1, nl.getLength());

	}

	@Test
	public final void test11ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	@Test(expected = XSDValidateException.class)
	public final void test12ValidateXSD() throws SAXException, IOException {
		DocumentBuilder db = XMLUtils.createBuilder();

		org.w3c.dom.Document doc =
			db.parse(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML));

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(doc, xsdFileName);

	}

	@Test
	public final void test13ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML), xsdFileName);

	}

	@Test(expected = XSDValidateException.class)
	public final void test14ValidateXSD() {
		SAXParser saxParser = XMLUtils.createSAXParser();

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(saxParser,
				XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML), xsdFileName);
	}

	@Test
	public final void test15ValidateXSD() {

		String xsdFileName = TEST_GOOD_XSD;

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
				xsdFileName);

	}

	@Test(expected = XSDValidateException.class)
	public final void test16ValidateXSD() {

		String xsdFileName = TEST_BAD_XSD;

		XMLUtils.xsdValidateUserData(XMLUtilsTest.class.getResourceAsStream(TEST_TEXT_SAMPLE_XML),
				xsdFileName);

	}

	@Test
	public final void testDPGoodValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(), TEST_XML)
				.getData(), TEST_XML, xsdFileName));
	}

	@Test(expected = XSDValidateException.class)
	public final void testDPBadValidateXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(),
				"test.bad1.xml").getData(), "test.bad1.xml", xsdFileName));
	}

	/**
	 * Проверка функции
	 * {@link ru.curs.showcase.util.xml.XMLUtils#xmlServiceSymbolsToNormal
	 * XMLUtils.testXmlServiceSymbolsToNormal}.
	 * 
	 */
	@Test
	public void testXmlServiceSymbolsToNormal() {
		String original = "&amp;&quot;&apos;&gt;&lt; какой-то текст";
		String result = XMLUtils.xmlServiceSymbolsToNormal(original);
		assertEquals("&\"'>< какой-то текст", result);
	}

	/**
	 * Проверяет сериализацию текущего контекста в XML.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testContextToXML() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		CompositeContext context = CompositeContext.createCurrent();
		Document doc = XMLUtils.objectToXML(context);
		assertEquals(Action.CONTEXT_TAG, doc.getDocumentElement().getNodeName());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("additional").getLength());
		assertEquals(1, doc.getDocumentElement().getElementsByTagName("main").getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName(Action.FILTER_TAG)
				.getLength());
		assertEquals(0, doc.getDocumentElement().getElementsByTagName("session").getLength());
		CompositeContext context2 =
			(CompositeContext) XMLUtils.xmlToObject(doc.getDocumentElement(),
					CompositeContext.class);
		assertTrue(ReflectionUtils.equals(context, context2));
	}

	@Test
	public void testGridContextToXML() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		CompositeContext context = new CompositeContext();
		context.setMain(null);
		context.setSession(null);
		GridContext gc = getExtGridContext(context);
		Document doc = XMLUtils.objectToXML(context.getRelated().get(new ID("01")));
		GridContext gc2 =
			(GridContext) XMLUtils.xmlToObject(doc.getDocumentElement(), GridContext.class);

		assertTrue(ReflectionUtils.equals(gc, gc2));
	}

	@Test
	public void testXFormsContextToXML() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		CompositeContext context = new CompositeContext();
		XFormContext xc = new XFormContext();
		xc.setFormData("<schema/>");
		xc.setAdditional(ADD_CONDITION);
		context.addRelated("01", xc);
		Document doc = XMLUtils.objectToXML(context.getRelated().get(new ID("01")));
		XFormContext xc2 =
			(XFormContext) XMLUtils.xmlToObject(doc.getDocumentElement(), XFormContext.class);

		assertTrue(ReflectionUtils.equals(xc, xc2));
		xc2.setAdditional("F1281D4A-5D6E-4DAB-8A5A-2D87B59B6ED2");
		assertFalse(ReflectionUtils.equals(xc, xc2));
	}

	/**
	 * Тестирования сериализации информации об элементе панели в XML.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * 
	 */
	@Test
	public void testDPElementInfoToXML() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		DataPanelElementInfo element = getTestXForms1Info();
		Document doc = XMLUtils.objectToXML(element);
		assertEquals("element", doc.getDocumentElement().getNodeName());
		DataPanelElementInfo el2 =
			(DataPanelElementInfo) XMLUtils.xmlToObject(doc.getDocumentElement(),
					DataPanelElementInfo.class);
		assertTrue(ReflectionUtils.equals(element, el2));
		el2.setCacheData(!el2.getCacheData());
		assertFalse(ReflectionUtils.equals(element, el2));
		el2.setCacheData(!el2.getCacheData());
		el2.getHtmlAttrs().setStyle("6461216659708261808L");
		assertFalse(ReflectionUtils.equals(element, el2));
	}

	@Test
	public final void testDPTableLayoutXSD() {
		String xsdFileName = DataPanelFactory.DATAPANEL_XSD;

		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		XMLValidator validator = new XMLValidator(new ClassPathXSDSource());
		validator.validate(new XMLSource(gateway.getRawData(new CompositeContext(), RICH_DP)
				.getData(), RICH_DP, xsdFileName));
	}

	@Test
	public void testStringToDocument() throws SAXException, IOException {
		String input = TEST_DOC;
		assertNotNull(XMLUtils.stringToDocument(input));
		input = "";
		assertNull(XMLUtils.stringToDocument(input));
		input = null;
		assertNull(XMLUtils.stringToDocument(input));
	}

	@Test
	public void testDocumentToString() {
		Document doc = XMLUtils.createEmptyDoc("test");
		assertEquals(TEST_DOC, XMLUtils.documentToString(doc).trim());
		doc = null;
		assertNull(XMLUtils.documentToString(doc));
	}
}
