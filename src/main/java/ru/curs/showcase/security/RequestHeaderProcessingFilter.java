package ru.curs.showcase.security;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.app.server.AppAndSessionEventsListener;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.security.logging.*;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.xml.CompositeContextOnBasisOfUserAndSessionDetails;

//imports omitted
/**
 * Фильтр аутентификации Spring Security.
 * 
 * @author den
 * 
 */

public class RequestHeaderProcessingFilter extends AbstractAuthenticationProcessingFilter {

	/**
	 * Параметр HttpServletRequest с именем пользователя.
	 */
	private static final String USERNAME_HEADER = "j_username";

	/**
	 * Параметр HttpServletRequest с паролем пользователя.
	 */
	private static final String PASS_HEADER = "j_password";

	private static final String DOMAIN = "j_domain";

	protected RequestHeaderProcessingFilter() {
		super("/j_spring_security_check");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		ShowcaseAuthenticationSuccessHandler successHandler =
			ApplicationContextProvider.getApplicationContext().getBean(
					"customAuthenticationSuccessHandler",
					ShowcaseAuthenticationSuccessHandler.class);
		setAuthenticationSuccessHandler(successHandler);

		IPTokenBasedRememberMeServices rememberMeServiceHandler =
			ApplicationContextProvider.getApplicationContext().getBean(
					"ipTokenBasedRememberMeServicesBean", IPTokenBasedRememberMeServices.class);
		setRememberMeServices(rememberMeServiceHandler);

		String username = request.getParameter(USERNAME_HEADER);
		String password = request.getParameter(PASS_HEADER);
		String domain = request.getParameter(DOMAIN);
		SignedUsernamePasswordAuthenticationToken authRequest =
			new SignedUsernamePasswordAuthenticationToken(username, password);

		HttpSession session = request.getSession();
		// AppInfoSingleton.getAppInfo().setSesid(session.getId());
		AppInfoSingleton.getAppInfo().getRemoteAddrSessionMap()
				.put(request.getRemoteAddr(), request.getSession(false).getId());

		@SuppressWarnings("unchecked")
		Enumeration<String> en = session.getAttributeNames();
		Map<String, Object> values = new HashMap<String, Object>();
		while (en.hasMoreElements()) {
			String s = en.nextElement();
			values.put(s, session.getAttribute(s));
		}

		session.invalidate();

		HttpSession newSession = request.getSession(true);
		for (String str : values.keySet()) {
			newSession.setAttribute(str, values.get(str));
		}

		request.getSession(false).setAttribute("username", username);
		// request.getSession(false).setAttribute("password", password);

		request.getSession(false).setAttribute("newSession", request.getSession(false));

		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		// установка деталей внутреннего пользователя
		userAndSessionDetails.setUserInfo(new UserInfo(username, null, username, null, null,
				domain));

		authRequest.setDetails(userAndSessionDetails);

		// обработчик устанавливающий что будет происходить в случае когда в
		// процессе аутентификации произошла ошибка
		AuthFailureHandler authFailureHandler = new AuthFailureHandler();
		authFailureHandler.add("username", username);
		authFailureHandler.add("password", password);
		authFailureHandler.add("domain", domain);
		setAuthenticationFailureHandler(authFailureHandler);
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

		if (authentication.isAuthenticated()) {
			AppAndSessionEventsListener.incrementingAuthenticatedSessions();
			SecurityLoggingCommand logCommand =
				new SecurityLoggingCommand(new CompositeContextOnBasisOfUserAndSessionDetails(
						userAndSessionDetails), request, request.getSession(), TypeEvent.LOGIN);
			logCommand.execute();
		}

		AppInfoSingleton.getAppInfo().getSessionAuthenticationMapForCrossDomainEntrance()
				.put(newSession.getId(), authentication);

		return authentication;
	}
}