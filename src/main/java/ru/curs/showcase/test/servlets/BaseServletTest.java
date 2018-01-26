package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import ru.curs.showcase.util.*;

/**
 * Тестирование базовых функций для работы сервлетов.
 * 
 * @author den
 * 
 */
public class BaseServletTest extends AbstractServletTest {

	@Test
	// !!! corrected
			public
			void testFillErrorResponce() throws IOException {
		final String message = "ошибка";
		preCheckResponse();
		ServletUtils.fillErrorResponce(response(), message, false);

		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response().getStatus());
		assertEquals("text/html", response().getContentType());
		assertEquals(TextUtils.DEF_ENCODING, response().getCharacterEncoding());
		assertEquals(message, response().getContentAsString());
		assertTrue(response().isCommitted());
		checkForNoCashe(response());
	}

	private void preCheckResponse() {
		assertNull(response().getContentType());
		assertFalse(response().isCommitted());
	}

	@Test
	public void testFillHTMLResponce() throws IOException {
		final String message = "сообщение";
		preCheckResponse();
		ServletUtils.makeResponseFromString(response(), message);

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
		assertEquals("text/html", response().getContentType());
		assertEquals(TextUtils.DEF_ENCODING, response().getCharacterEncoding());
		assertEquals(message, response().getContentAsString());
		assertTrue(response().isCommitted());
	}

	@Test
	public void testFillXMLResponce() throws IOException {
		final String message = "сообщение";
		preCheckResponse();
		ServletUtils.makeXMLResponseFromString(response(), message);

		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
		assertEquals("text/xml", response().getContentType());
		assertEquals(TextUtils.DEF_ENCODING, response().getCharacterEncoding());
		assertEquals(message, response().getContentAsString());
		assertTrue(response().isCommitted());
	}

	@Test
	// !!! corrected
			public
			void testDoNoCasheResponse() {
		assertNull(response().getHeader("Pragma"));
		ServletUtils.doNoCasheResponse(response());
		checkForNoCashe(response());
	}

	public static void checkForNoCashe(final MockHttpServletResponse response) {
		assertEquals("no-cache", response.getHeader("Pragma"));
		assertEquals("must-revalidate,no-store,no-cache", response.getHeader("Cache-Control"));
		// assertEquals(Long.valueOf(0), response.getHeader("Expires"));
		assertEquals(String.valueOf(0), response.getHeader("Expires"));
	}
}
