package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.core.external.ExternalCommand;
import ru.curs.showcase.util.ServletUtils;

/**
 * Сервлет - аналог веб-сервисов.
 * 
 * @author den
 * 
 */
public class ExternalServlet extends HttpServlet {

	public static final String REQUEST_STRING = "requestString";

	private static final long serialVersionUID = -4937856990909960895L;

	public static final String PROC_PARAM = "proc";

	@Override
	protected void service(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {
		String procName = hreq.getParameter(PROC_PARAM);
		if (procName == null) {
			throw new HTTPRequestRequiredParamAbsentException(PROC_PARAM);
		}
		String request = hreq.getParameter(REQUEST_STRING);
		if (request == null) {
			throw new HTTPRequestRequiredParamAbsentException(REQUEST_STRING);
		}

		ExternalCommand command = new ExternalCommand(request, procName);
		String response = command.execute();

		hresp.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeXMLResponseFromString(hresp, response);

	}
}
