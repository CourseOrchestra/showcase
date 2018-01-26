package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;

import ru.curs.showcase.app.server.ControlMemoryServlet;

/**
 * Тесты для ControlMemoryServlet.
 * 
 * @author den
 * 
 */
public class ControlMemoryServletTest extends AbstractServletTest {

	private ControlMemoryServlet servlet;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		servlet = new ControlMemoryServlet();
	}

	@Test
	public void testPollJdbc() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.POOL_PARAM, "jdbc");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testPollJython() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.POOL_PARAM, "jython");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testPollXSL() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.POOL_PARAM, "xsl");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testPollAll() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.POOL_PARAM, "all");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testPollUnknown() {
		request().addParameter(ControlMemoryServlet.POOL_PARAM, "unknown");

		try {
			servlet.service(request(), response());
			fail();
		} catch (ServletException | IOException e) {
			assertEquals(ControlMemoryServlet.UNKNOWN_PARAM_ERROR, e.getLocalizedMessage());
		}
	}

	@Test
	public void testGcClear() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.GC_PARAM, "");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testUserdataCopy() throws ServletException, IOException {
		request().addParameter(ControlMemoryServlet.USERDATA_PARAM, "");

		servlet.service(request(), response());

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
	}

	@Test
	public void testNoParams() {
		try {
			servlet.service(request(), response());
			fail();
		} catch (ServletException | IOException e) {
			assertEquals(ControlMemoryServlet.NO_PARAMS_ERROR, e.getLocalizedMessage());
		}
	}

}
