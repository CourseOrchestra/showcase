package ru.curs.showcase.test.html;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.core.ValidateException;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextGatewayAndFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String WEB_TEXT_GET_JYTHON_PROC_PY = "webtext/GetJythonProc.py";

	/**
	 * Тест на случай, когда не задано преобразование.
	 * 
	 */
	@Test
	@Ignore
	// !!!
	public void testGetStaticDataBySP() {
		String prefix = "<root>";
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "2");

		HTMLGateway wtgateway = new HtmlDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(context, element);
		String out = XMLUtils.documentToString(rawWT.getData());
		new WebText(out);
		assertTrue(out.startsWith(prefix));
	}

	// !!! @Test(expected = SettingsFileOpenException.class)
	public void testNotExistsJython() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName("webtext_pas.py");
		CompositeContext context = new CompositeContext();
		HTMLJythonGateway gateway = new HTMLJythonGateway();
		gateway.getRawData(context, elInfo);
	}

	@Test
	@Ignore
	// !!!
	public void testValidateExceptionInJython() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		CompositeContext context = new CompositeContext();
		context.setMain("плохой");
		context.setSession("<sessioninfo/>");
		HTMLJythonGateway gateway = new HTMLJythonGateway();
		try {
			gateway.getRawData(context, elInfo);
		} catch (ValidateException e) {
			assertEquals("проверка на ошибку сработала (1)", e.getLocalizedMessage());
			return;
		}
		fail();
	}

	@Test
	@Ignore
	// !!!
	public void testJythonGetData() {
		final String region = "Алтайский край";

		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		elInfo.setTransformName("pas.xsl");
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(region);
		context.setAdditional(ADD_CONDITION);
		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertNotNull(webtext);
		assertNotNull(webtext.getData());
		assertTrue(webtext.getData().startsWith("<div>"));
		assertTrue(webtext.getData().indexOf(region) > -1);
		assertEquals(0, webtext.getEventManager().getEvents().size());
		assertNull(webtext.getDefaultAction());
	}

	@Test
	@Ignore
	// !!!
	public void testJythonGetSettings() {
		final String region = "Алтайский край";

		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elInfo.setProcName(WEB_TEXT_GET_JYTHON_PROC_PY);
		elInfo.setTransformName("pas.xsl");
		CompositeContext context = generateContextWithSessionInfo();
		context.setMain(region);
		context.setAdditional("withsettings");
		WebTextGetCommand command = new WebTextGetCommand(context, elInfo);
		WebText webtext = command.execute();

		assertNotNull(webtext);
		assertNotNull(webtext.getDefaultAction());
		assertEquals("я оригинальный", webtext.getDefaultAction().getDataPanelLink()
				.getElementLinkById("d2").getContext().getAdditional());
		assertEquals(1, webtext.getEventManager().getEvents().size());
		assertEquals("я оригинальный", webtext.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinkById("d2").getContext().getAdditional());
	}

	@Test
	public void testHTMLFileGateway() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elementInfo.setProcName("3buttons_enh.xml");
		HTMLFileGateway gateway = new HTMLFileGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(context, elementInfo);

		assertNotNull(raw.getData());
		assertNotNull(raw.getSettings());
	}

	@Test(expected = ValidateException.class)
	public void webtextSQLScriptsCanReturnErrorCodeAndMessage() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elementInfo.setProcName("webtext/testErrorReturn.sql");
		HTMLGateway gateway = new HtmlMSSQLExecGateway();
		gateway.getRawData(context, elementInfo);

	}

	@Test(expected = ValidateException.class)
	public void webtextSQLScriptsCanReturnScecialExceptionWithCode() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo elementInfo =
			new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		elementInfo.setProcName("webtext/testRaiseException.sql");
		HTMLGateway gateway = new HtmlMSSQLExecGateway();
		gateway.getRawData(context, elementInfo);

	}
}
