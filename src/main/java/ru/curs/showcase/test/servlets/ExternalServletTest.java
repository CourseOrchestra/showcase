package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.server.*;

/**
 * Тесты для ExternalServlet.
 * 
 * @author den
 * 
 */
public class ExternalServletTest extends AbstractServletTest {

	private ExternalServlet servlet;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		servlet = new ExternalServlet();
	}

	@Test
	public void testGetData() throws ServletException, IOException {
		request().addParameter(ExternalServlet.PROC_PARAM, "ws/GetFile.py");
		request().addParameter(ExternalServlet.REQUEST_STRING,
				"<command type=\"getDP\" param=\"a.xml\"/>");

		servlet.service(request(), response());

		checkOkResponse("text/xml");

		assertTrue(response().getContentAsString().startsWith(
				"<responseAnyXML xmlns:sc=\"http://showcase.curs.ru\">"));
		assertTrue(response().getContentAsString().endsWith("</responseAnyXML>"));
		assertTrue(response().getContentAsString().contains("</sc:datapanel>"));
		assertTrue(response().getContentAsString().contains("name=\"XForms как фильтр\""));

	}

	@Test
	public void testNoParams() throws ServletException, IOException {
		request().addParameter(ExternalServlet.REQUEST_STRING,
				"<command type=\"getDP\" param=\"a.xml\"/>");
		try {
			servlet.service(request(), response());
			fail();
		} catch (HTTPRequestRequiredParamAbsentException e) {
			assertEquals("Не передан обязательный параметр: proc", e.getLocalizedMessage());
		}
	}

}
