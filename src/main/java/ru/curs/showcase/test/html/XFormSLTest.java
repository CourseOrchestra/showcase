package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для шлюзов XForms.
 * 
 * @author den
 * 
 */
public class XFormSLTest extends AbstractTest {
	private static final String SHOWCASE_DATA_XML = "Showcase_Data.xml";
	private static final String DATA_XFORMS = "data/xforms/";
	private static final String SHOWCASE_DATA_COPY_XML = "Showcase_Data_Copy.xml";
	private static final String XFORMS_SUBMISSION1 = "xforms_submission1";
	private static final String TEST_DATA_TAG = "<data>test</data>";

	@Test
	public void xformCanBeCreatedWithDataFromSPCall() {
		XFormContext xcontext = new XFormContext(getTestContext1());
		DataPanelElementInfo element = new DataPanelElementInfo("1", DataPanelElementType.XFORMS);
		element.setProcName("xforms_proc_test");
		element.setTemplateName(SHOWCASE_TEMPLATE_XML);
		generateTestTabWithElement(element);

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xform = command.execute();

		assertNotNull(xcontext.getSession());
		Action action = xform.getActionForDependentElements();
		assertNotNull(action);
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("xforms default action", action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());

		assertEquals(2, xform.getEventManager().getEvents().size());
		action = xform.getEventManager().getEvents().get(0).getAction();
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertEquals("62", action.getDataPanelLink().getElementLinks().get(0).getId().getString());
		assertEquals("save click on xforms (with filtering)", action.getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());

		checkForEmptyMainInstance(xform);
	}

	@Test
	public void xformCanBeCreatedWithDataFromSQLScript() {
		XFormContext xcontext = new XFormContext(getTestContext1());
		DataPanelElementInfo element = new DataPanelElementInfo("1", DataPanelElementType.XFORMS);
		element.setProcName("xform/procTest.sql");
		element.setTemplateName(SHOWCASE_TEMPLATE_XML);
		generateTestTabWithElement(element);

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xform = command.execute();

		assertNotNull(xform);
		assertEquals(2, xform.getEventManager().getEvents().size());
		assertNotNull(xform.getDefaultAction());
		assertEquals("1", xform.getId().toString());

		checkForEmptyMainInstance(xform);
	}

	protected void checkForEmptyMainInstance(final XForm xform) {
		assertNotNull(xform.getXFormParts());
		final String controlWord = "/info/name";
		final int index = 0;
		assertTrue(xform.getXFormParts().get(index).contains(controlWord));
	}

	@Test
	@Ignore
	// !!!
			public
			void testSaveXForms() {
		String data =
			"<schema xmlns=\"\"><info><name/><growth/><eyescolour/><music/><comment/></info></schema>";
		XFormContext xcontext = new XFormContext(getTestContext1(), data);
		DataPanelElementInfo element = getTestXForms1Info();
		XFormSaveCommand command = new XFormSaveCommand(xcontext, element);
		command.execute();
	}

	@Test
	@Ignore
	// !!!
			public
			void xformAllowSaveByMSSQLScript() {
		String data =
			"<schema xmlns=\"\"><info><name/><growth/><eyescolour/><music/><comment/></info></schema>";
		XFormContext xcontext = new XFormContext(getTestContext1(), data);
		DataPanelElementInfo element = getTestXForms1Info();
		element.getSaveProc().setName("xform/saveproc1.sql");
		XFormSaveCommand command = new XFormSaveCommand(xcontext, element);
		command.execute();
	}

	@Test
	// @Ignore
	// !!!
			public
			void xformAllowSPScriptSubmission() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo(XFORMS_SUBMISSION1);
		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals(data, res);
	}

	@Test
	public void xformAllowMSSQLScriptSubmission() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo("xform/submission1.sql");
		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals(data, res);
	}

	/**
	 * Функция тестирования работы SQL Submission через ServiceLayer c передачей
	 * null в параметре content.
	 */
	@Test
	public void testSQLSubmissionBySLWithNullData() {
		XFormContext context = new XFormContext();
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo(XFORMS_SUBMISSION1);
		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals("handleSQLSubmission должен вернуть пустую строку в ответ на null", "", res);
	}

	/**
	 * Функция тестирования работы XSLT Submission через ServiceLayer.
	 */
	@Test
	public void testXSLTSubmissionByFile() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("xformsxslttransformation_test.xsl");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	@Test
	@Ignore
	// !!!
	public void testXSLSubmissionByJython() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("transform/test.py");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	@Test
	public void testXSLSubmissionBySP() {
		String data = TEST_DATA_TAG;
		XFormContext context =
			new XFormContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA), data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsTransformationInfo("xforms_transform_test");
		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();
		assertNotNull(res);
	}

	// !!! @Test
	public void testJythonSubmission() {
		String data = TEST_DATA_TAG;
		XFormContext context = new XFormContext();
		context.setFormData(data);
		DataPanelElementInfo elInfo =
			XFormInfoFactory.generateXFormsSQLSubmissionInfo("xform/submission.py");
		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();
		assertEquals("<data>test_handled</data>", res);
	}

	/**
	 * Проверка скачивания файла для XForms через ServiceLayer.
	 */
	@Test
	public void testXFormsFileDownloadBySL() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		ID linkId = new ID("proc4");
		XFormDownloadCommand command = new XFormDownloadCommand(context, elementInfo, linkId);
		OutputStreamDataFile file = command.execute();
		final int navigatorXMLLen = 231_478;
		assertNotNull(context.getSession());
		assertTrue(file.getData().size() > navigatorXMLLen);
		assertEquals(TextUtils.JDBC_ENCODING, file.getEncoding());
	}

	/**
	 * Проверка закачивания файла из XForms через ServiceLayer.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testXFormsFileUploadBySL() throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms2Info();
		ID linkId = new ID("proc5");
		final String fileName = TEST_XML_FILE;
		OutputStreamDataFile file = getTestFile(fileName);
		XFormUploadCommand command = new XFormUploadCommand(context, element, linkId, file);
		command.execute();
		assertNotNull(context.getSession());
	}

	/**
	 * Проверка загрузки на сервер правильного XML.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testXFormsXMLUploadWithFileXSLGood() throws IOException {
		uploadTestBase("proc7");
	}

	private void uploadTestBase(final String linkId) throws IOException {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo element = getTestXForms2Info();
		final String fileName = "ru/curs/showcase/test/util/TestTextSample.xml";
		OutputStreamDataFile file = getTestFile(fileName);
		XFormUploadCommand command =
			new XFormUploadCommand(context, element, new ID(linkId), file);
		command.execute();
		assertNotNull(context.getSession());
	}

	@Test
	@Ignore
	// !!!
	public void testXFormsXMLUploadWithXSLJythonGood() throws IOException {
		uploadTestBase("proc7jj");
	}

	@Test
	public void testXFormsXMLUploadWithXSLStoredProcGood() throws IOException {
		uploadTestBase("proc7spsp");
	}

	/**
	 * Проверка скачивания XML файла для XForms через ServiceLayer.
	 */
	@Test
	public void testXFormsXMLDownloadXSLFileGood() {
		downloadTestBase("proc6");
	}

	private void downloadTestBase(final String linkId) {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo = getTestXForms2Info();
		XFormDownloadCommand command =
			new XFormDownloadCommand(context, elementInfo, new ID(linkId));
		command.execute();
	}

	@Test
	@Ignore
	// !!!
	public void testXFormsXMLDownloadXSLJythonGood() {
		downloadTestBase("proc6jj");
	}

	@Test
	public void testXFormsXMLDownloadXSLStoredProcGood() {
		downloadTestBase("proc6spsp");
	}

	@Test
	public void testJythonGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("xform/GetJythonProc.py");
		elementInfo.setTemplateName(SHOWCASE_TEMPLATE_XML);
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertEquals(1, xforms.getEventManager().getEvents().size());
		assertNotNull(xforms.getDefaultAction());
		assertNotNull(xforms.getXFormParts().get(0));
		assertNotNull(xforms.getXFormParts().get(1));
	}

	@Test
	@Ignore
	// !!!
	public void testJythonTemplateGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("xform/GetJythonProc.py");
		elementInfo.setTemplateName("template/Base.py");
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertNotNull(xforms);
		assertNotNull(xforms.getXFormParts());
		final int numParts = 2;
		assertEquals(numParts, xforms.getXFormParts().size());
	}

	@Test
	@Ignore
	// !!!
			public
			void testSPTemplateGateway() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("xforms_proc_all");
		elementInfo.setTemplateName("xforms_template_uploaders_simple");
		generateTestTabWithElement(elementInfo);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		XForm xforms = command.execute();

		assertNotNull(xforms);
		assertNotNull(xforms.getXFormParts());
		final int numParts = 2;
		assertEquals(numParts, xforms.getXFormParts().size());
	}

	@Test(expected = GeneralException.class)
	public void testJythonNotExists() {
		XFormContext context = new XFormContext(getTestContext1());
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elementInfo.setProcName("__fake__proc__.py");
		elementInfo.setTemplateName(SHOWCASE_TEMPLATE_XML);
		XFormGetCommand command = new XFormGetCommand(context, elementInfo);
		command.execute();
	}

	@Test
	public void testSaveXFormByJython() throws IOException {
		AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
		String inputData =
			XMLUtils.streamToString(UserDataUtils.loadUserDataToStream(DATA_XFORMS
					+ SHOWCASE_DATA_XML));
		XFormContext context = new XFormContext(generateContextWithSessionInfo(), inputData);
		context.setAdditional(SHOWCASE_DATA_COPY_XML);
		File file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML);
		if (file.exists()) {
			file.delete();
		}
		DataPanelElementInfo elementInfo = getTestXForms1Info();
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("saveproc");
		proc.setName("xform/XFormSaveProc.py");
		proc.setType(DataPanelElementProcType.SAVE);
		elementInfo.getProcs().clear();
		elementInfo.getProcs().put(proc.getId(), proc);

		XFormSaveCommand command = new XFormSaveCommand(context, elementInfo);
		command.execute();

		file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML);
		assertTrue(file.exists());
		String outputData =
			XMLUtils.streamToString(UserDataUtils.loadUserDataToStream(DATA_XFORMS
					+ SHOWCASE_DATA_COPY_XML));
		assertEquals(inputData, outputData);
	}

	@Test
	@Ignore
	// !!!
			public
			void keepUserSettingsShouldControlPersistenceOfXFormData() {
		final int index = 0;

		XFormContext xcontext = new XFormContext(getTestContext1());
		xcontext.setKeepUserSettings(true);
		DataPanelElementInfo element = getTestXForms1Info();

		XFormGetCommand command = new XFormGetCommand(xcontext, element);
		XForm xforms = command.execute();

		final String controlWord = "/info/name";
		assertTrue(xforms.getXFormParts().get(index).contains(controlWord));

		xcontext = new XFormContext(getTestContext1());
		xcontext.setKeepUserSettings(true);
		final String formData =
			"<schema xmlns=\"\"><info><name></name><growth></growth><eyescolour>Зеленый</eyescolour>"
					+ "<music></music><comment></comment></info></schema>";
		xcontext.setFormData(formData);

		command = new XFormGetCommand(xcontext, element);
		xforms = command.execute();

		assertTrue(xforms.getXFormParts().get(index).contains(controlWord));

		xcontext = new XFormContext(getTestContext1());
		xcontext.setKeepUserSettings(false);
		xcontext.setFormData(formData);

		command = new XFormGetCommand(xcontext, element);
		xforms = command.execute();

		assertTrue(xforms.getXFormParts().get(index).contains(controlWord));
	}
}
