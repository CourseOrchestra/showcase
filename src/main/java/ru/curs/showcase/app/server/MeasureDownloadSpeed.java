package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Сервлет поддержки скорости загрузки контента.
 * 
 */
public class MeasureDownloadSpeed extends HttpServlet {
	private static final long serialVersionUID = -4705379989767318039L;

	@Override
	protected void service(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String stringContentSize = hreq.getParameter("contentSize");
		long contentSize = Long.parseLong(stringContentSize);

		String str = "12345678";
		String content = "";
		final int i128 = 128;
		for (int k = 0; k < i128; k++) {
			content = content + str;
		}

		try (PrintWriter writer = hresp.getWriter()) {
			final int l1024 = 1024;
			contentSize = contentSize * l1024;
			for (long i = 0; i < contentSize; i++) {
				writer.print(content);
			}
		}

		hresp.setStatus(HttpServletResponse.SC_OK);
		// hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

	}
}
