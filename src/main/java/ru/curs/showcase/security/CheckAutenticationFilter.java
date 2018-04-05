package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.server.AppAndSessionEventsListener;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Фильтр, контролирующий доступ к данным системы.
 * 
 */
public class CheckAutenticationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckAutenticationFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		// if (request instanceof HttpServletRequest) {
		String url1 = ((HttpServletRequest) request).getRequestURL().toString();
		// String queryString = ((HttpServletRequest) request).getQueryString();
		// System.out.println(url1 + "?" + queryString);

		if (!url1.contains("secured/data")) {
			filterChain.doFilter(request, response);
		} else {

			if (httpReq.getSession(false) == null) {
				httpReq.getSession();
				response.reset();
				response.setContentType("text/html");
				response.setCharacterEncoding(TextUtils.DEF_ENCODING);
				response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
				response.getWriter().close();

			} else {

				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.debug(httpReq.getSession().getId());
				}

				String url = null;
				try {
					url = SecurityParamsFactory.getLocalAuthServerUrl();
				} catch (SettingsFileOpenException e) {
					throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR, e);
				}
				AuthServerUtils.init(url);

				if (AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
						httpReq.getSession().getId())) {
					UserInfo ud =
						AuthServerUtils.getTheAuthServerAlias().isAuthenticated(
								httpReq.getSession().getId());
					// if
					// (SecurityContextHolder.getContext().getAuthentication()
					// !=
					// null) {
					// LOGGER.debug("RequestContextHolder = "
					// + ((WebAuthenticationDetails)
					// SecurityContextHolder.getContext()
					// .getAuthentication().getDetails()).getSessionId());
					// }

					if (ud == null) {

						response.reset();
						response.setContentType("text/html");
						response.setCharacterEncoding(TextUtils.DEF_ENCODING);
						response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
						response.getWriter().close();
						((HttpServletRequest) request).getSession().invalidate();
						return;

						// HttpServletResponse httpResponse =
						// (HttpServletResponse)
						// response;
						// httpResponse.sendRedirect(httpReq.getContextPath() +
						// "/login.jsp");

					} else {
						Authentication auth =
							AppInfoSingleton.getAppInfo()
									.getSessionAuthenticationMapForCrossDomainEntrance()
									.get(httpReq.getSession().getId());
						if (auth != null) {
							if (((UserAndSessionDetails) auth.getDetails()) != null) {
								String sid =
									((UserAndSessionDetails) auth.getDetails()).getUserInfo()
											.getSid();
								if (!ud.getSid().equals(sid)) {
									((UserAndSessionDetails) auth.getDetails()).setUserInfo(ud);
									SecurityContextHolder.getContext().setAuthentication(auth);
								}
							}
						}

						filterChain.doFilter(request, response);
					}
				} else {

					String esiaAuthenticated =
						(String) (httpReq.getSession(false).getAttribute("esiaAuthenticated"));
					if ((esiaAuthenticated != null)
							&& ("true".equals(esiaAuthenticated))
							&& AppInfoSingleton.getAppInfo()
									.getOrInitSessionInfoObject(httpReq.getSession().getId())
									.isAuthViaESIA()) {
						filterChain.doFilter(request, response);
						return;
					}

					String remembermeAuthenticated =
						(String) (httpReq.getSession(false)
								.getAttribute("remembermeAuthenticated"));
					if ((remembermeAuthenticated != null)
							&& ("true".equals(remembermeAuthenticated))) {
						boolean presented = false;
						Cookie remembermecookie =
							(Cookie) httpReq.getSession(false).getAttribute("remembermecookie");
						if (remembermecookie != null) {
							remembermecookie.setPath(AppAndSessionEventsListener.getContextPath());
							remembermecookie.setMaxAge(1209600);
							Cookie[] cookies = httpReq.getCookies();
							if (cookies != null && cookies.length > 0) {
								for (Cookie cookie : cookies) {
									if (cookie.getName().equals("remembermecookie")) {
										presented = true;
									}
								}
							}
							if (!presented)
								httpResp.addCookie(remembermecookie);
						}
						filterChain.doFilter(request, response);
						return;
					}

					String username =
						(String) (httpReq.getSession(false).getAttribute("username"));
					// String password = (String)
					// (httpReq.getSession(false).getAttribute("password"));

					if ("master".equals(username)
					// && "master".equals(password)
					) {
						filterChain.doFilter(request, response);
					} else {
						try {
							if (SecurityContextHolder.getContext().getAuthentication() != null) {
								if (SecurityContextHolder.getContext().getAuthentication()
										.getName().equals("guest"))
									filterChain.doFilter(request, response);
							}
							// Случай анонимного входа в приложение.
							if (CustomAccessProvider.getAccess().equals("permitAll")) {
								UserAndSessionDetails userAndSessionDetails =
									new UserAndSessionDetails((HttpServletRequest) request);
								userAndSessionDetails.setUserInfo(new UserInfo("guest", "guest",
										"guest", null, null, (String) null));
								userAndSessionDetails.setOauth2Token(null);
								userAndSessionDetails.setAuthViaAuthServer(false);
								SessionUtils
										.setAnonymousUserAndSessionDetails(userAndSessionDetails);
								String sesid = userAndSessionDetails.getSessionId();
								try {
									AppInfoSingleton
											.getAppInfo()
											.getCelestaInstance()
											.login(sesid,
													userAndSessionDetails.getUserInfo().getSid());
									AppInfoSingleton
											.getAppInfo()
											.getSessionSidsMap()
											.put(sesid,
													userAndSessionDetails.getUserInfo().getSid());
								} catch (Exception e) {
									if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
										LOGGER.error(
												"Ошибка привязки сессии приложения к пользователю в celesta",
												e);
									}
								}
								filterChain.doFilter(request, response);
							} else {

								response.reset();
								response.setContentType("text/html");
								response.setCharacterEncoding(TextUtils.DEF_ENCODING);
								response.getWriter().append(
										ExchangeConstants.SESSION_NOT_AUTH_SIGN);
								response.getWriter().close();

							}
						} catch (Exception e) {
							response.reset();
							response.setContentType("text/html");
							response.setCharacterEncoding(TextUtils.DEF_ENCODING);
							response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
							response.getWriter().close();
						}
					}
				}
			}
		}
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}

}
