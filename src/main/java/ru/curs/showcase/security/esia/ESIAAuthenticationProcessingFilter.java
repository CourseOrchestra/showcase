package ru.curs.showcase.security.esia;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.app.server.AppAndSessionEventsListener;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.AuthFailureHandler;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.security.logging.SecurityLoggingCommand;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.xml.CompositeContextOnBasisOfUserAndSessionDetails;

/**
 * Фильтр ESIA авторизации.
 * 
 */
public class ESIAAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER =
		LoggerFactory.getLogger(ESIAAuthenticationProcessingFilter.class);

	protected ESIAAuthenticationProcessingFilter() {
		super("/esia");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {

		String auth = request.getParameter("auth");
		if (auth != null && !auth.isEmpty()) {
			response.sendRedirect(ESIAManager.getAuthorizationURL());
			return null;
		}

		String code = request.getParameter("code");

		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		ESIAAuthenticationToken authRequest = null;
		boolean esiaAuthenticated = false;
		if (code != null) {

			ESIAUserInfo esiaUI = ESIAManager.getUserInfo(code);

			UserInfo ui = new UserInfo(esiaUI.getLogin(), String.valueOf(esiaUI.getOid()),
					esiaUI.getLastName() + " " + esiaUI.getFirstName() + " "
							+ esiaUI.getMiddleName(),
					esiaUI.getEmail(), esiaUI.getPhone(), (String) null);
			ui.setSnils(esiaUI.getSnils());
			ui.setGender(esiaUI.getGender());
			ui.setBirthDate(esiaUI.getBirthDate());
			ui.setBirthPlace(esiaUI.getBirthPlace());
			ui.setFirstName(esiaUI.getFirstName());
			ui.setLastName(esiaUI.getLastName());
			ui.setMiddleName(esiaUI.getMiddleName());
			ui.setTrusted(esiaUI.isTrusted());

			userAndSessionDetails.setUserInfo(ui);

			authRequest = new ESIAAuthenticationToken(esiaUI.getSnils());

			authRequest.setDetails(userAndSessionDetails);

			request.getSession(false).setAttribute("username", ui.getLogin());

			esiaAuthenticated =
				(!ESIAManager.isAllowAuthenticateOnlyTrustedUser()) || esiaUI.isTrusted();

		} else {

			authRequest = new ESIAAuthenticationToken("notAuthenticated");

			authRequest.setDetails(userAndSessionDetails);

			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				String error = request.getParameter("error");
				String errorDescription = request.getParameter("error_description");
				LOGGER.error(
						"Ошибка аутентификации через ESIA: " + error + ", " + errorDescription);
			}

			esiaAuthenticated = false;

		}

		AuthFailureHandler authFailureHandler = new AuthFailureHandler("ESIA");
		authFailureHandler.add("code", "notAuthenticated");
		setAuthenticationFailureHandler(authFailureHandler);

		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

		if (esiaAuthenticated) {
			request.getSession(false).setAttribute("newSession", request.getSession(false));

			request.getSession(false).setAttribute("esiaAuthenticated", "true");
			authentication.setAuthenticated(true);

			AppInfoSingleton.getAppInfo()
					.getOrInitSessionInfoObject(request.getSession(false).getId())
					.setAuthViaESIA(true);

		} else {
			request.getSession(false).setAttribute("esiaAuthenticated", "false");
			authentication.setAuthenticated(false);
		}

		if (authentication.isAuthenticated()) {
			AppAndSessionEventsListener.incrementingAuthenticatedSessions();
			SecurityLoggingCommand logCommand = new SecurityLoggingCommand(
					new CompositeContextOnBasisOfUserAndSessionDetails(userAndSessionDetails),
					request, request.getSession(), TypeEvent.LOGIN);
			logCommand.execute();
		}

		return authentication;

	}

}
