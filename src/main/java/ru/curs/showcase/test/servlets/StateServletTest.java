package ru.curs.showcase.test.servlets;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.server.StateServlet;

/**
 * Тесты для StateServlet.
 * 
 * @author den
 * 
 */
public class StateServletTest extends AbstractServletTest {

	private StateServlet servlet;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		servlet = new StateServlet();
	}

	@Test
	public void testDoGet() throws ServletException, IOException {
		servlet.doGet(request(), response());

		checkOkResponse("text/xml");

		assertTrue(response().getContentAsString().contains("<isNativeUser>false</isNativeUser>"));
		assertTrue(response().getContentAsString().contains("<browserType>FIREFOX</browserType>"));
		assertTrue(response().getContentAsString().contains(
				"<javaVersion>" + System.getProperty("java.version") + "</javaVersion>"));
	}

}
