package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.core.command.ShowcaseExportException;
import ru.curs.showcase.core.external.*;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.test.ws.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты WS (веб-сервиса).
 * 
 * @author den
 * 
 */
public class WSTest extends AbstractTestWithDefaultUserData {

	private static final String WRONG_COMMAND = "<command type=\"getНав\" param=\"a.xml\"/>";
	private static final String RU_AD_RESULT = "RU-AD";
	private static final String WS_HANDLE_PROC = "wsHandle";
	private static final String WS_GET_FILE_PY = "ws/GetFile.py";
	private static final String COMMAND_TYPE_GET_DP_PARAM_A_XML =
		"<command type=\"getDP\" param=\"a.xml\"/>";
	private static final String NS_COMMAND_TYPE_GET_DP_PARAM_A_XML =
		"<show:requestAnyXML xmlns:show=\"http://showcase.curs.ru\">"
				+ "<command type=\"getDP\" param=\"b.xml\"/></show:requestAnyXML>";
	private static final String COMMAND_GET_ARERA_CODE =
		"<command type=\"select\" table=\"GeoObjects\" id=\"1\" column=\"Code\" />";

	@BeforeClass
	public static void beforeClass() {
		AbstractTestWithDefaultUserData.beforeClass();
		Properties localprops = new Properties();
		try (InputStream is = new FileInputStream("local.properties")) {
			localprops.load(is);
		} catch (IOException e) {
			fail();
		}
		Endpoint.publish(localprops.getProperty("sc.webapp") + "/forall/webservices",
				new ru.curs.showcase.app.server.ws.ShowcaseExternals());
	}

	// @Test
	public void testJythonGateway() {
		ExternalCommandGateway gateway = new JythonExternalCommandGateway();
		String res = gateway.handle(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);
		assertTrue(res.indexOf("<sc:element id=\"1\" type=\"webtext\" transform=\"bal.xsl\" />") > -1);
	}

	// @Test
	public void testDBGateway() {
		ExternalCommandGateway gateway = new DBExternalCommandGateway();
		String res = gateway.handle(COMMAND_GET_ARERA_CODE, WS_HANDLE_PROC);
		assertEquals(RU_AD_RESULT, res);
	}

	// @Test
	public void testJythonCommand() {
		ExternalCommand command =
			new ExternalCommand(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);
		String res = command.execute();
		assertTrue(res.indexOf("<sc:element id=\"6\" type=\"webtext\" transform=\"bal.xsl\" />") > -1);
	}

	// @Test(expected = ShowcaseExportException.class)
	public void testJythonCommandException() throws ShowcaseExportException {
		ExternalCommand command = new ExternalCommand(WRONG_COMMAND, WS_GET_FILE_PY);
		command.executeForExport();
	}

	// !!! @Test
	public void testJythonCommandException2() {
		ru.curs.showcase.app.server.ws.ShowcaseExternals ws =
			new ru.curs.showcase.app.server.ws.ShowcaseExternals();
		try {
			ws.handleXML(new ru.curs.showcase.app.server.ws.RequestAnyXML(), "ws/GetNotXMLFile.py");
			fail();
		} catch (ShowcaseExportException e) {
			assertEquals(ru.curs.showcase.app.server.ws.ShowcaseExternals.NOT_XML_OUTPUT_ERROR,
					e.getLocalizedMessage());
		}
	}

	// @Test
	public void testSPCommand() {
		ExternalCommand command = new ExternalCommand(COMMAND_GET_ARERA_CODE, WS_HANDLE_PROC);
		String res = command.execute();
		assertEquals(RU_AD_RESULT, res);
	}

	// @Test
	public void testWSClientWithJython() throws ShowcaseExportException_Exception {
		ShowcaseExternals port = prepareWS();
		String response = port.handle(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);

		assertTrue(response.indexOf("<sc:tab id=\"6\" name=\"XForms как фильтр\">") > -1);
	}

	// @Test
	public void testWSClientWithJythonByXML() throws IOException,
			ShowcaseExportException_Exception, SAXException {
		ShowcaseExternals port = prepareWS();
		Document doc = XMLUtils.stringToDocument(NS_COMMAND_TYPE_GET_DP_PARAM_A_XML);
		RequestAnyXML request = (RequestAnyXML) XMLUtils.xmlToObject(doc, RequestAnyXML.class);
		ResponseAnyXML response = port.handleXML(request, WS_GET_FILE_PY);

		assertNotNull(response);
	}

	// @Test
	public void testWSClientWithSP() throws ShowcaseExportException_Exception {
		ShowcaseExternals port = prepareWS();
		String res = port.handle(COMMAND_GET_ARERA_CODE, WS_HANDLE_PROC);

		assertEquals(RU_AD_RESULT, res);
	}

	private ShowcaseExternals prepareWS() {
		ShowcaseExternalsService service = new ShowcaseExternalsService();
		ShowcaseExternals port = service.getPort(ShowcaseExternals.class);
		return port;
	}

	/**
	 * TODO: выяснить почему не проходит Eclemma.
	 */
	// @Test
	// @Ignore
	public void testWSClientExceptioninJython() {
		ShowcaseExternals port = prepareWS();
		try {
			port.handle(WRONG_COMMAND, WS_GET_FILE_PY);
			fail();
		} catch (ShowcaseExportException_Exception e) {
			assertEquals(
					"При вызове Jython процедуры 'ws/GetFile.py' произошла ошибка: getНав не реализовано !",
					e.getMessage());
		}
	}

	/**
	 * TODO: выяснить почему не проходит Eclemma.
	 */
	// @Test
	// @Ignore
	public void testWSClientExceptionInDB() {
		ShowcaseExternals port = prepareWS();
		try {
			port.handle("<command type=\"delete\"/>", WS_HANDLE_PROC);
			fail();
		} catch (ShowcaseExportException_Exception e) {
			assertEquals(
					"Процесс: Выполнение внешней команды. Произошла ошибка при выполнении хранимой процедуры wsHandle.",
					e.getMessage());
		}
	}
}
