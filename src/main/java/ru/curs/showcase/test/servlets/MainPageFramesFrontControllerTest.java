package ru.curs.showcase.test.servlets;

import static org.junit.Assert.assertEquals;

import java.io.*;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.server.MainPageFramesFrontController;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для MainPageFramesControllerTest.
 * 
 * @author den
 * 
 */
public class MainPageFramesFrontControllerTest extends AbstractServletTest {

	private MainPageFramesFrontController controller;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		controller = new MainPageFramesFrontController();
	}

	@Test
	public void testWelcome() throws ServletException, IOException {
		request().setServletPath("/secured/welcome");

		controller.doGet(request(), response());

		checkOkResponse(TEXT_HTML);
		assertEquals("", response().getContentAsString());
	}

	@Test
	public void testWelcome2() throws ServletException, IOException {
		request().setServletPath("/secured/welcome");
		request().addParameter(ExchangeConstants.URL_PARAM_USERDATA, TEST1_USERDATA);

		controller.doGet(request(), response());

		checkOkResponse(TEXT_HTML);
		InputStream is =
			MainPageFramesFrontControllerTest.class.getResourceAsStream("welcome.html");
		assertEquals(TextUtils.streamToString(is), response().getContentAsString());
	}

	// !!! @Test
	public void testHeader() throws ServletException, IOException {
		request().setServletPath("/secured/header");
		request().addParameter(ExchangeConstants.URL_PARAM_USERDATA, TEST1_USERDATA);

		controller.doGet(request(), response());

		checkOkResponse(TEXT_HTML);
		InputStream is =
			MainPageFramesFrontControllerTest.class.getResourceAsStream("header.html");
		assertEquals(TextUtils.streamToString(is), response().getContentAsString());
	}

	// !!! @Test
	public void testFooter() throws ServletException, IOException {
		request().setServletPath("/secured/footer");
		request().addParameter(ExchangeConstants.URL_PARAM_USERDATA, TEST1_USERDATA);

		controller.doGet(request(), response());

		checkOkResponse(TEXT_HTML);
		InputStream is =
			MainPageFramesFrontControllerTest.class.getResourceAsStream("footer.html");
		assertEquals(TextUtils.streamToString(is), response().getContentAsString());
	}

}
