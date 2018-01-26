package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import ru.curs.showcase.app.server.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для XFormXSLTransformServlet.
 * 
 * @author den
 * 
 */
public class XFormXSLTransformServletTest extends AbstractServletTest {

	private XFormXSLTransformServlet servlet;

	@Override
	public void setUp() {
		super.setUp();
		servlet = new XFormXSLTransformServlet();
	}

	@Test
	public void testDoPost() throws ServletException, IOException {
		request().addParameter(XFormXSLTransformServlet.XSLTFILE_PARAM,
				"xformsxslttransformation_test.xsl");
		request().setContent("<test/>".getBytes(TextUtils.DEF_ENCODING));

		servlet.doPost(request(), response());

		checkOkResponse("text/html");
		assertTrue(response().getContentAsString().contains(
				"<name>Отработка сервлета XFormsTransformationServlet</name>"));
	}

	@Test
	public void testNoSourceParam() throws ServletException, IOException {
		try {
			servlet.doPost(request(), response());
			fail();
		} catch (HTTPRequestRequiredParamAbsentException e) {
			assertEquals(HTTPRequestRequiredParamAbsentException.ERROR_MES
					+ XFormXSLTransformServlet.XSLTFILE_PARAM, e.getLocalizedMessage());
		}
	}
}
