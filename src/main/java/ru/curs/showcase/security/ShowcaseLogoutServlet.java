package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.showcase.app.server.AppAndSessionEventsListener;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.logging.SecurityLoggingCommand;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseLogoutServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseLogoutServlet.class);
	private static final String LOGOUT_INFO = "Сессия %s закрыта";
	private static final String ERROR_LOGOUT_INFO =
		"Сессия %s не была закрыта на сервере аутентификафии. AuthServer недоступен.";

	private static final long serialVersionUID = -2981309424890139659L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String webAppName = request.getContextPath();
		if (webAppName.contains("/")) {
			webAppName = webAppName.replace("/", "");
		}

		if (request.getSession(false) != null) {
			String attr =
				(String) (request.getSession(false).getAttribute("queryString"
						+ request.getServerPort() + webAppName));
			Cookie cookie = new Cookie("queryString" + request.getServerPort() + webAppName, attr);
			cookie.setPath(AppAndSessionEventsListener.getContextPath());
			response.addCookie(cookie);
		}

		// HttpSession oldSession =
		// (HttpSession) request.getSession(false).getAttribute("newSession");
		// String oldSesid = request.getParameter("sesId");

		// if (oldSesid.equals(oldSession.getId())) {
		// AppAndSessionEventsListener.decrement();
		// }

		String sesid = null;
		HttpSession session = request.getSession();

		// sesid = session.getId();
		sesid = request.getParameter("sesId");

		// Признак пользовательского выхода из системы
		session.setAttribute(SecurityLoggingCommand.IS_CLICK_LOGOUT, Boolean.TRUE);

		// if (!((UserAndSessionDetails)
		// SecurityContextHolder.getContext().getAuthentication()
		// .getDetails()).isAuthViaAuthServer()) {

		if (!(AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(sesid))) {
			return;
		}

		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR);
		}

		if (url != null) {
			URL server = new URL(url + String.format("/logout?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			try {
				c.connect();

				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
						LOGGER.info(String.format(LOGOUT_INFO, sesid));
					}
				}

			} catch (IOException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info(String.format(ERROR_LOGOUT_INFO, sesid));
				}
			} finally {
				if (c != null) {
					c.disconnect();
				}

			}

		}

		String esiaAuthenticated =
			(String) (request.getSession(false).getAttribute("esiaAuthenticated"));
		if ((esiaAuthenticated != null) && ("true".equals(esiaAuthenticated))) {
			response.sendRedirect(request.getContextPath() + "/logout");
		}

	}
}
