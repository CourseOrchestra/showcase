package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.*;

import javax.servlet.ServletException;

import org.junit.*;
import org.springframework.mock.web.MockFilterChain;

import ru.curs.showcase.app.server.PreProcessFilter;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Тесты для наших фильтров сервлетов.
 * 
 * @author den
 * 
 */
public class FiltersTest extends AbstractServletTest {

	private PreProcessFilter filter;
	private MockFilterChain chain;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		filter = new PreProcessFilter();
		chain = new MockFilterChain();
	}

	@Test
	public void testWork() throws IOException, ServletException {
		filter.doFilter(request(), response(), chain);
	}

	@Test
	public void testMainPage1() throws IOException, ServletException {
		filter.doFilter(request(), response(), chain);

		preCheckSessionInfo();
	}

	@Test
	public void testMainPage2() throws IOException, ServletException {
		String sessionId = preCheckSessionInfo();

		request().setServletPath("/" + PreProcessFilter.INDEX_PAGE);
		filter.doFilter(request(), response(), chain);

		SessionInfo sessionInfo = AppInfoSingleton.getAppInfo().getSessionInfoMap().get(sessionId);
		assertNotNull(sessionInfo);
		assertFalse(sessionInfo.isAuthViaAuthServer());
	}

	@Test
	public void testNoCache1() throws IOException, ServletException {
		checkNoCache("/css/internalShowcase.css");
	}

	private void checkNoCache(final String path) throws IOException, ServletException {
		request().setServletPath(path);
		filter.doFilter(request(), response(), chain);

		BaseServletTest.checkForNoCashe(response());
	}

	@Test
	public void testNoCache2() throws IOException, ServletException {
		checkNoCache("/index.jsp");
	}

	@Test
	public void testNoCache3() throws IOException, ServletException {
		checkNoCache("/login.jsp");
	}

	@Test
	public void testNoCache4() throws IOException, ServletException {
		checkNoCache("/secured/data");
	}

	@Test
	public void testNoCache5() throws IOException, ServletException {
		checkNoCache("/secured/upload");
	}

	@Test
	public void testEncoding() throws IOException, ServletException {
		request().setServletPath("/secured/upload");
		filter.doFilter(request(), response(), chain);

		assertEquals(TextUtils.DEF_ENCODING, request().getCharacterEncoding());
	}

	@Test
	public void testGeoCheck1() throws IOException, ServletException {
		request().setRequestURI("js/course/geo.js");
		filter.doFilter(request(), response(), chain);

		assertEquals(0, response().getContentLength());
	}

	@Test
	public void testGeoCheck2() throws IOException, ServletException {
		request().setRequestURI("js/course/geo_non_exists_file.js");
		filter.setGeoCheckFile("geo_non_exists_file.js");
		filter.doFilter(request(), response(), chain);

		InputStream is =
			request().getSession().getServletContext()
					.getResourceAsStream("js/course/geo_fake.js");
		assertEquals(TextUtils.streamToString(is), response().getContentAsString());
	}

	private String preCheckSessionInfo() {
		String sessionId = request().getSession().getId();
		assertNotNull(sessionId);
		assertNull(AppInfoSingleton.getAppInfo().getSessionInfoMap().get(sessionId));
		return sessionId;
	}
}
