package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.frame.*;
import ru.curs.showcase.util.ServletUtils;

/**
 * Front controller для получения "статических" фреймов, которые будут включены
 * в главную страницу приложения.
 */
public final class MainPageFramesFrontController extends HttpServlet {

	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String servlet = request.getServletPath();
		servlet =
			servlet.replace("/" + ExchangeConstants.SECURED_SERVLET_PREFIX + "/", "")
					.toUpperCase();
		MainPageFrameType type = MainPageFrameType.valueOf(servlet);
		CompositeContext context = ServletUtils.prepareURLParamsContext(request);
		MainPageFrameGetCommand command = new MainPageFrameGetCommand(context, type);
		String html = command.execute();
		if (html == null) {
			html = "";
		}
		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(response, html);
	}

}
