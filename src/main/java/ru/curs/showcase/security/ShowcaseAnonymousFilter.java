package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.*;

public class ShowcaseAnonymousFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseAnonymousFilter.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void
			doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
					throws IOException, ServletException {

		// DefaultSavedRequest defaultSavedRequest =
		// (DefaultSavedRequest) (new HttpSessionRequestCache().getRequest(
		// (HttpServletRequest) request, (HttpServletResponse) response));
		//
		// Map<String, String> map = ((HttpServletRequest)
		// request).getParameterMap();

		// String port = "" + ((HttpServletRequest) request).getServerPort();
		// String webAppName = ((HttpServletRequest) request).getContextPath();
		// if (webAppName.contains("/")) {
		// webAppName = webAppName.replace("/", "");
		// }
		// Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		// if (cookies != null && cookies.length > 0) {
		// for (Cookie cookie : cookies) {
		// if (cookie.getName().equals("queryString" + port + webAppName)
		// && cookie.getValue() != null
		// && !cookie.getValue().contains("guestAccess=true")
		// && !CustomAccessProvider.getAccess().equals("fullyAuthenticated")) {
		// CustomAccessProvider.setAccess("fullyAuthenticated");
		// }
		// }
		// }

		String guestAccess = ((HttpServletRequest) request).getParameter("guestAccess");
		if ("true".equals(guestAccess)) {
			// CustomAccessProvider.setGuestAccess(true);
			// request.getRequestDispatcher("/secured/data").forward(request,
			// response);
		}

		String s = CustomAccessProvider.getAccess();
		// boolean b = CustomAccessProvider.getGuestAccess();

		filterChain.doFilter(request, response);

		// boolean bool = false;
		// Enumeration<String> en = ((HttpServletRequest)
		// request).getParameterNames();
		// while (en.hasMoreElements()) {
		// if (en.nextElement().equals("guestAccess")) {
		// bool = true;
		// break;
		// }
		// }
		// if (guestAccess != null && !"true".equals(guestAccess) || !bool) {
		// CustomAccessProvider.setAccess("fullyAuthenticated");
		// }

		// UserAndSessionDetails userAndSessionDetails =
		// new UserAndSessionDetails((HttpServletRequest) request);
		// userAndSessionDetails.setUserInfo(new UserInfo("guest", "guest",
		// "guest", null, null,
		// (String) null));
		// userAndSessionDetails.setOauth2Token(null);
		// userAndSessionDetails.setAuthViaAuthServer(false);
		// SessionUtils.setAnonymousUserAndSessionDetails(userAndSessionDetails);
		// String sesid = userAndSessionDetails.getSessionId();
		// try {
		// AppInfoSingleton.getAppInfo().getCelestaInstance()
		// .login(sesid, userAndSessionDetails.getUserInfo().getSid());
		// } catch (Exception e) {
		// if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
		// LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta",
		// e);
		// }
		// }
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
