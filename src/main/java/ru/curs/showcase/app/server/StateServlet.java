package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ServerState;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.ServerStateGetCommand;
import ru.curs.showcase.runtime.ClientState;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLObjectSerializer;

/**
 * Сервлет для возврата состояния сервера.
 * 
 * @author den
 * 
 */
public final class StateServlet extends HttpServlet {

	private static final long serialVersionUID = -3101461389195836031L;

	public StateServlet() {
		super();
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		CompositeContext context = ServletUtils.prepareURLParamsContext(request);
		String userAgent = ServletUtils.getUserAgent(request);
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState serverState = command.execute();
		ClientState sessionState = new ClientState(serverState, userAgent);

		ObjectSerializer serializer = new XMLObjectSerializer();
		String message = serializer.serialize(sessionState);

		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeXMLResponseFromString(response, message);
	}

}
