package ru.curs.showcase.security;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.server.PreProcessFilter;
import ru.curs.showcase.security.logging.*;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.util.ServletUtils;

/**
 * Controller на который происходит перенаправление в случае ошибки авторзации
 * (see security.xml).
 */
public final class AuthFailureHandler implements AuthenticationFailureHandler {
	private final Map<String, String> attributes = new HashMap<String, String>();
	private static final String ERROR_DATA_TAG = "errorData";

	public AuthFailureHandler() {
		this.attributes.put("name", "FORM");
	}

	public AuthFailureHandler(final String sName) {
		this.attributes.put("name", sName);
	}

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request,
			final HttpServletResponse response, final AuthenticationException exception)
			throws IOException, ServletException {
		request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
		CompositeContext context = ServletUtils.prepareURLParamsContext(request);
		Event event = new Event(TypeEvent.LOGINERROR, context);
		this.attributes.put("Message", exception.getMessage());
		event.add(ERROR_DATA_TAG, this.attributes);

		try {
			SecurityLoggingCommand logCommand =
				new SecurityLoggingCommand(context, request, request.getSession(), event);
			logCommand.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"
				+ PreProcessFilter.LOGIN_PAGE + "?error=true"));
	}

	/**
	 * Добавление атрибута.
	 * 
	 */
	public void add(final String name, final String value) {
		attributes.put(name, value);
	}

}
