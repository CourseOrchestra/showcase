package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты для фабрики XForms.
 * 
 * @author den
 * 
 */
public class XFormFactoryTest extends AbstractTestWithDefaultUserData {

	/**
	 * Тест на создание фабрики.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testXFormsFactory() {
		createFactory();
	}

	/**
	 * Test method for
	 * {@link ru.curs.showcase.core.html.xform.XFormFactory#build()} .
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	// !!!
			public
			void testBuild() throws Exception {
		XFormFactory factory = createFactory();
		factory.build();
	}

	private XFormFactory createFactory() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		HTMLAdvGateway gateway = new HtmlDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		return new XFormFactory(raw);
	}

	@Test
	@Ignore
	// !!!
			public
			void testDBTemplate() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		generateTestTabWithElement(element);
		element.setProcName("xforms_proc_all");
		element.setTemplateName("xforms_template_uploaders_simple");
		HTMLAdvGateway gateway = new HtmlDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		XFormFactory factory = new XFormFactory(raw);
		XForm xform = factory.build();

		assertNotNull(xform);
		final int num = 2;
		assertEquals(num, xform.getXFormParts().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void testUserdataAddToXForms() throws Exception {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elInfo.setProcName("xforms_proc_all");
		elInfo.setTemplateName("Showcase_Template_all.xml");
		XFormContext context = new XFormContext(getTestContext1());
		generateTestTabWithElement(elInfo);
		HTMLAdvGateway gateway = new HtmlDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, elInfo);
		XFormFactory factory = new XFormFactory(raw);
		XForm result = factory.build();

		final int index = 0;
		assertTrue(result
				.getXFormParts()
				.get(index)
				.indexOf(
						String.format("%s/%s?%s=%s&", ExchangeConstants.SECURED_SERVLET_PREFIX,
								ExchangeConstants.SUBMIT_SERVLET,
								ExchangeConstants.URL_PARAM_USERDATA,
								ExchangeConstants.DEFAULT_USERDATA)) > 0);
	}

	@Test
	// @Ignore
	// !!!
			public
			void testGenerateUploaders() throws SAXException, IOException {
		DocumentBuilder builder = XMLUtils.createBuilder();
		Document doc =
			builder.parse(UserDataUtils.loadUserDataToStream(SettingsFileType.XFORM.getFileDir()
					+ "/Showcase_Template_all.xml"));
		DataPanelElementInfo dpei = new DataPanelElementInfo("01", DataPanelElementType.XFORMS);
		generateTestTabWithElement(dpei);
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("upload1");
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setName("upload1_proc");
		dpei.getProcs().put(proc.getId(), proc);
		XFormTemplateModificator.generateUploaders(doc, dpei, "testSubformId");
	}

	// @Test(expected = ValidateException.class)
	public void testJythonSubmissionException() {
		XFormContext context = new XFormContext();
		HTMLAdvGateway gateway = new XFormJythonGateway();
		gateway.scriptTransform("xform/submission.py", context);
	}

	@Test
	// @Ignore
	// !!!
			public
			void testSimpleSelectors() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		generateTestTabWithElement(element);
		element.setProcName("xforms_proc_no_data");
		final String templateName = "Showcase_Template_multiselector_simple.xml";
		element.setTemplateName(templateName);
		HTMLAdvGateway gateway = new HtmlDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);

		String input =
			TextUtils.streamToString(UserDataUtils.loadUserDataToStream(SettingsFileType.XFORM
					.getFileDir() + "/" + templateName));
		assertTrue(input.contains("xf:selector"));
		assertTrue(input.contains("xf:multiselector"));

		XFormFactory factory = new XFormFactory(raw);
		XForm xform = factory.build();

		assertFalse(xform.getXFormParts().get(0).contains("xf:selector"));
		assertFalse(xform.getXFormParts().get(0).contains("xf:multiselector"));
	}
}
