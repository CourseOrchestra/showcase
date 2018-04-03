package ru.curs.showcase.security;

import java.net.*;
import java.util.Arrays;

import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.DigestUtils;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;

public class IPTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IPTokenBasedRememberMeServices.class);

	private static final ThreadLocal<HttpServletRequest> requestHolder =
		new ThreadLocal<HttpServletRequest>();

	public HttpServletRequest getContext() {
		return requestHolder.get();
	}

	public void setContext(HttpServletRequest context) {
		requestHolder.set(context);
	}

	protected String getUserIPAddress(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	@Override
	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication) {
		try {
			setContext(request);
			String login = request.getParameter("j_username");
			String password = request.getParameter("j_password");
			String domain = request.getParameter("j_domain");
			SignedUsernamePasswordAuthenticationToken authRequest =
				new SignedUsernamePasswordAuthenticationToken(login, password);
			SecurityContextHolder.getContext().setAuthentication(authRequest);
			UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
			String sid =
				((UserAndSessionDetails) successfulAuthentication.getDetails()).getUserInfo()
						.getSid();
			String name =
				((UserAndSessionDetails) successfulAuthentication.getDetails()).getUserInfo()
						.getFullName();
			userAndSessionDetails.setUserInfo(new UserInfo(login, sid, name, null, null,
					(String) null));
			userAndSessionDetails.setOauth2Token(null);
			userAndSessionDetails.setAuthViaAuthServer(false);
			authRequest.setDetails(userAndSessionDetails);
			// SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
			request.getSession(false).setAttribute("remembermeAuthenticated", "true");
			AppInfoSingleton.getAppInfo().setSesid(request.getSession(false).getId());
			// try {
			// Celesta.getInstance().login(request.getSession(false).getId(),
			// ((UserAndSessionDetails)
			// authRequest.getDetails()).getUserInfo().getSid());
			// } catch (CelestaException e) {
			// e.printStackTrace();
			// if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
			// LOGGER.error("Ошибка привязки сессии приложения к пользователю в
			// celesta",
			// e);
			// }
			// }

			super.onLoginSuccess(request, response, authRequest);
		} finally {
			setContext(null);
		}
	}

	@Override
	protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		String signature =
			DigestUtils.md5DigestAsHex((username + ":" + tokenExpiryTime + ":" + password + ":"
					+ getKey() + ":" + getUserIPAddress(getContext())).getBytes());
		return signature;
		// SignedUsernamePasswordAuthenticationToken authToken =
		// new SignedUsernamePasswordAuthenticationToken(username, password);
		// SecurityContextHolder.getContext().setAuthentication(authToken);
		//
		// return super.makeTokenSignature(tokenExpiryTime, username, password);
	}

	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request,
			HttpServletResponse response) {
		if (tokens.length < 6) {
			String pwd = request.getParameter("j_password");
			String sid =
				((UserAndSessionDetails) (SecurityContextHolder.getContext().getAuthentication())
						.getDetails()).getUserInfo().getSid();
			String name =
				((UserAndSessionDetails) (SecurityContextHolder.getContext().getAuthentication())
						.getDetails()).getUserInfo().getFullName();

			String[] tokensWithPassword = Arrays.copyOf(tokens, tokens.length + 3);
			tokensWithPassword[tokensWithPassword.length - 3] = pwd;
			tokensWithPassword[tokensWithPassword.length - 2] = sid;
			tokensWithPassword[tokensWithPassword.length - 1] = name;
			// getUserIPAddress(request);
			super.setCookie(tokensWithPassword, maxAge, request, response);
		} else
			super.setCookie(tokens, maxAge, request, response);
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			setContext(request);
			SignedUsernamePasswordAuthenticationToken authToken =
				new SignedUsernamePasswordAuthenticationToken(cookieTokens[0], cookieTokens[3]);
			UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
			userAndSessionDetails.setUserInfo(new UserInfo(cookieTokens[0], cookieTokens[4],
					cookieTokens[5], null, null, (String) null));
			userAndSessionDetails.setOauth2Token(null);
			// userAndSessionDetails.setAuthViaAuthServer(false);
			authToken.setDetails(userAndSessionDetails);
			SecurityContextHolder.getContext().setAuthentication(authToken);
			request.getSession().setAttribute("remembermeAuthenticated", "true");

			HttpURLConnection c = null;
			try {
				AppInfoSingleton
						.getAppInfo()
						.getCelestaInstance()
						.login(AppInfoSingleton.getAppInfo().getSesid(),
								((UserAndSessionDetails) authToken.getDetails()).getUserInfo()
										.getSid());

				String url = SecurityParamsFactory.getLocalAuthServerUrl();
				URL server =
					new URL(url
							+ String.format("/login?sesid=%s&login=%s&pwd=%s",
									request.getSession(false).getId(),
									AuthServerAuthenticationProvider.encodeParam(cookieTokens[0]),
									AuthServerAuthenticationProvider.encodeParam(cookieTokens[3])));

				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				c.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta", e);
				}
			} finally {
				if (c != null)
					c.disconnect();
			}
		} finally {
			// setContext(null);
		}
		return super.processAutoLoginCookie(Arrays.copyOf(cookieTokens, cookieTokens.length - 3),
				request, response);

	}
}
