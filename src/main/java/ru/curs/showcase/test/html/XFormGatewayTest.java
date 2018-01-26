package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.junit.*;
import org.w3c.dom.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;
import ru.curs.showcase.util.xml.*;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormGatewayTest extends AbstractTestWithDefaultUserData {
	private static final String ELEMENT_0205 = "0205";
	private static final String XFORMS_SUBMISSION1 = "xforms_submission1";
	private static final String TEST_DATA_TAG = "<data>test</data>";

	/**
	 * Тест для чтения из файла.
	 */
	@Test
	public void testFileGateWay() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);
		ElementInfoChecker checker = new ElementInfoChecker();
		checker.check(element, DataPanelElementType.XFORMS);
		HTMLGateway gateway = new HTMLFileGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Тестируем обновление через FileGateway.
	 * 
	 */
	@Test
	public void testFileGatewayUpdate() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);

		HTMLAdvGateway gateway = new XFormFileGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new XFormFileGateway();
		gateway.saveData(context, element, content);
		File file =
			new File(String.format(XFormFileGateway.TMP_TEST_DATA_DIR + "/%s_updated.xml",
					element.getProcName()));
		assertTrue(file.exists());
	}

	/**
	 * Тест на трансформацию шаблона XForms с данными.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFileGateWayWithTransform() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST1_1_XML, "2", ELEMENT_0205);

		HTMLGateway gateway = new HTMLFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);

		DocumentBuilder db = XMLUtils.createBuilder();
		InputStream stream =
			UserDataUtils.loadUserDataToStream(String.format("%s/%s",
					SettingsFileType.XFORM.getFileDir(), element.getTemplateName()));
		Document doc = db.parse(stream);
		XFormProducer.getHTML(doc, raw.getData());
	}

	/**
	 * Проверка шлюза к БД.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testDBGateway() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		ElementInfoChecker checker = new ElementInfoChecker();
		checker.check(element, DataPanelElementType.XFORMS);

		HTMLAdvGateway gateway = new HtmlDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testDBGatewayUpdate() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms1Info();

		HTMLAdvGateway gateway = new HtmlDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new HtmlDBGateway();
		gateway.saveData(context, element, content);
	}

	/**
	 * Тест на сохранение данных, который должен вернуть ошибку.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testDBGatewayUpdateWithError() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getTestXForms2Info();

		ElementInfoChecker checker = new ElementInfoChecker();
		checker.check(element, DataPanelElementType.XFORMS);

		HTMLAdvGateway gateway = new HtmlDBGateway();
		String content = getNewContentBasedOnExisting(context, element, gateway);
		gateway = new HtmlDBGateway();
		try {
			gateway.saveData(context, element, content);
		} catch (ValidateException e) {
			assertEquals("1", e.getUserMessage().getId());
			assertEquals("Неуловимая ошибка из БД, связанная с триггерами и блокировками (1)", e
					.getUserMessage().getText());
			return;
		}
		fail();
	}

	private String getNewContentBasedOnExisting(final CompositeContext context,
			final DataPanelElementInfo element, final HTMLGateway gateway) {
		HTMLBasedElementRawData raw = gateway.getRawData(context, element);
		Element newChild = raw.getData().createElementNS("", "new");
		raw.getData().getDocumentElement().appendChild(newChild);
		String content = XMLUtils.documentToString(raw.getData());
		return content;
	}

	/**
	 * Функция тестирования работы XFormsDBGateway.handleSubmission.
	 * 
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testSQLTransform() {
		XFormContext context = new XFormContext();
		context.setFormData(TEST_DATA_TAG);
		HTMLAdvGateway gateway = new HtmlDBGateway();
		String res = gateway.scriptTransform(XFORMS_SUBMISSION1, context);
		assertEquals(TEST_DATA_TAG, res);
	}

	/**
	 * Проверка файлового шлюза для скачивания данных.
	 * 
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testXFormsFileGatewayDownload() {
		HTMLAdvGateway gateway = new XFormFileGateway();
		final ID linkId = new ID(TEST_XML_FILE);
		XFormContext context = new XFormContext(getTestContext1());
		OutputStreamDataFile file = gateway.downloadFile(context, null, linkId);
		assertNotNull(file);
		assertNotNull(file.getData());
		assertEquals(linkId, file.getName());
		assertEquals(TextUtils.JDBC_ENCODING, file.getEncoding());
	}

	/**
	 * Проверка файлового шлюза для закачивания данных.
	 * 
	 */
	@Test
	// @Ignore
	// !!!
			public
			void testXFormsFileGatewayUpload() {
		HTMLAdvGateway gateway = new XFormFileGateway();
		final String linkId = TEST_XML_FILE;
		DataFile<InputStream> file =
			new DataFile<InputStream>(FileUtils.loadClassPathResToStream(linkId), linkId);

		assertEquals(TextUtils.DEF_ENCODING, file.getEncoding());
		gateway.uploadFile(new XFormContext(), null, new ID(linkId), file);
	}

	/**
	 * Проверка загрузки на сервер не соответствующего схеме XML.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLUploadBad() throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		ID linkId = new ID("proc8");
		final String fileName = "ru/curs/showcase/test/util/TestTextSample.xml";
		OutputStreamDataFile file = getTestFile(fileName);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(file, proc, context, elementInfo);
		transformer.transform();

		HTMLAdvGateway gateway = new HtmlDBGateway();
		gateway.uploadFile(context, elementInfo, linkId, transformer.getInputStreamResult());
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 * 
	 * @throws IOException
	 */
	@Test(expected = XSDValidateException.class)
	public void testXFormsXMLDownloadBad() throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		ID linkId = new ID("proc10");

		HTMLAdvGateway gateway = new HtmlDBGateway();
		OutputStreamDataFile file = gateway.downloadFile(context, elementInfo, linkId);

		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(file, proc, context, elementInfo);
		transformer.transform();
	}

	/**
	 * Тест сохранения данных через XFormsDBGateway c проверкой схемы и
	 * трансформацией.
	 * 
	 */
	@Test
	@Ignore
	// !!!
			public
			void testDBGatewayUpdateWithTransform() throws IOException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0208");

		HTMLAdvGateway gateway = new HtmlDBGateway();
		String content = getNewContentBasedOnExisting(context, elementInfo, gateway);

		DataPanelElementProc proc = elementInfo.getSaveProc();
		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(content, proc, context, elementInfo);
		transformer.transform();
		content = transformer.getStringResult();

		gateway = new HtmlDBGateway();
		gateway.saveData(context, elementInfo, content);
	}

	@Test(expected = XSDValidateException.class)
	public void testDBUpdateWithInvalidXML() throws IOException {
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0209");

		String content = "<test/>";

		DataPanelElementProc proc = elementInfo.getSaveProc();
		CompositeContext context = new CompositeContext();
		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(content, proc, context, elementInfo);
		transformer.transform();
	}

	@Test(expected = NotXMLException.class)
	public void testDBUpdateWithNotXML() throws IOException {
		DataPanelElementInfo elementInfo = getDPElement(TEST1_1_XML, "2", "0209");

		String content = "<test>";

		DataPanelElementProc proc = elementInfo.getSaveProc();
		CompositeContext context = new CompositeContext();

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(content, proc, context, elementInfo);
		transformer.transform();
	}

	@Test
	public void testHTMLFileGateway() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("Showcase_Data.xml");
		elementInfo.setTemplateName("some.xml");
		HTMLFileGateway gateway = new HTMLFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, elementInfo);

		assertNotNull(raw.getData());
		assertNotNull(raw.getSettings());
	}
}
