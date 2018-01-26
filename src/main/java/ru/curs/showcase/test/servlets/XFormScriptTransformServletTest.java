package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.server.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для XFormScriptTransformServlet.
 * 
 * @author den
 * 
 */
public class XFormScriptTransformServletTest extends AbstractServletTest {

	private XFormScriptTransformServlet servlet;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		servlet = new XFormScriptTransformServlet();
	}

	// !!! @Test
	public void testDoPost() throws ServletException, IOException {
		request().addParameter(XFormScriptTransformServlet.PROC_PARAM, "xform/submission.py");
		request().setContent("<data>test</data>".getBytes(TextUtils.DEF_ENCODING));

		servlet.doPost(request(), response());

		checkOkResponse("text/html");
		assertEquals("<data>test_handled</data>", response().getContentAsString());
	}

	@Test
	public void testNoSourceParam() throws ServletException, IOException {
		try {
			servlet.doPost(request(), response());
			fail();
		} catch (HTTPRequestRequiredParamAbsentException e) {
			assertEquals(HTTPRequestRequiredParamAbsentException.ERROR_MES
					+ XFormScriptTransformServlet.PROC_PARAM, e.getLocalizedMessage());
		}
	}
}
