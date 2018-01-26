package ru.curs.showcase.security;

import java.io.*;
import java.net.*;
import java.util.IllegalFormatException;

import javax.net.ssl.*;

import org.slf4j.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * @author anlug
 * 
 *         Класс, реализующий провайдер аутентификации с помощью AuthServer.
 * 
 */
public class AuthServerAuthenticationProvider implements AuthenticationProvider {

	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				// ip address of the service URL(like.23.28.244.244)
				// if (hostname.equals("23.28.244.244"))
				return true;
				// return false;
			}
		});
	}

	private static final Logger LOGGER =
		LoggerFactory.getLogger(AuthServerAuthenticationProvider.class);

	private String innerMessage = null;

	@Override
	public Authentication authenticate(final Authentication arg1) {

		// TODO Auto-generated method stub
		// arg0.getAuthorities().iterator().next().
		// if (SecurityContextHolder.getContext().getAuthentication() != null) {
		// if
		// (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
		// {
		// return arg0;
		// }
		// }
		// authentication.
		// UsernamePasswordAuthenticationToken arg0 = arg1;

		String ipAddresOfRemouteHost =
			((UserAndSessionDetails) arg1.getDetails()).getRemoteAddress();

		if (ipAddresOfRemouteHost == null) {
			ipAddresOfRemouteHost = "";
		}

		String url = "";
		String login = arg1.getPrincipal().toString();
		String pwd = arg1.getCredentials().toString();
		String sesid = ((UserAndSessionDetails) arg1.getDetails()).getSessionId();
		String oldSesid = AppInfoSingleton.getAppInfo().getSesid();
		String groupProviders =
			((UserAndSessionDetails) arg1.getDetails()).getUserInfo().getGroupProviders();

		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e1) {
			throw new AuthenticationServiceException(SecurityParamsFactory.APP_PROP_READ_ERROR,
					e1);
		}

		// if ("9152046062107176349L_default_value".equals(pwd)) {

		// для тестирования - надо удалить. начало.
		if (AppInfoSingleton.getAppInfo().getSessionInfoMap().containsKey(oldSesid)
				&& (AppInfoSingleton.getAppInfo().getSessionInfoMap().get(oldSesid)
						.getAuthServerCrossAppPassword() != null)) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.info("Попытка аутентификации сессии " + sesid
						+ " через механизм кроссдоменной аутентификации. Пользователь: " + login
						+ " Пароль: " + pwd);
			}
		}

		// для тестирования - надо удалить. конец.

		if (AppInfoSingleton.getAppInfo().getSessionInfoMap().containsKey(oldSesid)
				&& (AppInfoSingleton.getAppInfo().getSessionInfoMap().get(oldSesid)
						.getAuthServerCrossAppPassword() != null)
				&& AppInfoSingleton.getAppInfo().getSessionInfoMap().get(oldSesid)
						.getAuthServerCrossAppPassword().equals(pwd)) {

			try {
				// AppCurrContext.getInstance().setAuthViaAuthServ(true);
				AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
				((UserAndSessionDetails) arg1.getDetails()).setAuthViaAuthServer(true);

				if (AuthServerUtils.getTheAuthServerAlias() == null) {
					AuthServerUtils.init(url);
				}

				((UserAndSessionDetails) arg1.getDetails()).setUserInfo(
						AuthServerUtils.getTheAuthServerAlias().isAuthenticated(oldSesid));

			} finally {
				AppInfoSingleton.getAppInfo().getSessionInfoMap().get(oldSesid)
						.setAuthServerCrossAppPassword(null);
			}

		} else {
			AuthServerUtils.init(url);

			// UserData ud =
			// AuthServerUtils.getTheAuthServerAlias().isAuthenticated(sesid);
			// if (ud == null) {
			try {
				URL server;
				if (groupProviders == null) {
					server = new URL(url + String.format("/login?sesid=%s&login=%s&pwd=%s&ip=%s",
							sesid, encodeParam(login), encodeParam(pwd), ipAddresOfRemouteHost));
				} else {
					server =
						new URL(url + String.format("/login?sesid=%s&login=%s&pwd=%s&gp=%s&ip=%s",
								sesid, encodeParam(login), encodeParam(pwd),
								encodeParam(groupProviders), ipAddresOfRemouteHost));
				}
				HttpURLConnection c = null;
				try {
					c = (HttpURLConnection) server.openConnection();
					c.setRequestMethod("GET");
					c.connect();
					// Thread.sleep(1000);
					if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// AppCurrContext.getInstance();
						AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
						((UserAndSessionDetails) arg1.getDetails()).setAuthViaAuthServer(true);
						((UserAndSessionDetails) arg1.getDetails()).setUserInfo(
								AuthServerUtils.getTheAuthServerAlias().isAuthenticated(sesid));

						// AppCurrContext.getInstance().setAuthViaAuthServ(true);
					} else {
						if (AppInfoSingleton.getAppInfo().getIsCelestaInitialized()) {
							AppInfoSingleton.getAppInfo().getCelestaInstance().failedLogin(login);
						}

						String servletResponseMessage = "";
						try {
							servletResponseMessage = TextUtils.streamToString(c.getErrorStream());
						} catch (Exception e) {
						}

						if (UserDataUtils.getGeneralOptionalProp(
								"mellophone.show.reason.for.blocked.user") != null
								&& "true".equalsIgnoreCase(UserDataUtils
										.getGeneralOptionalProp(
												"mellophone.show.reason.for.blocked.user")
										.trim())) {
							if (servletResponseMessage.contains(
									"locked out for too many unsuccessful login attempts")
									&& servletResponseMessage.contains("Резюме:")) {
								LOGGER.info("Пользователь " + login + " заблокирован меллофоном");
								String time_to_unlock = servletResponseMessage.substring(
										servletResponseMessage.indexOf("Time to unlock"));
								throw new BadCredentialsException("User '" + login
										+ "' is blocked by mellophone. " + time_to_unlock);
							}

							if (servletResponseMessage.contains(
									"locked out for too many unsuccessful login attempts")
									&& !servletResponseMessage.contains("Резюме:")) {
								LOGGER.info("Пользователь " + login + " заблокирован меллофоном");
								String time_to_unlock = servletResponseMessage.substring(
										servletResponseMessage.indexOf("Time to unlock"));
								throw new BadCredentialsException("User '" + login
										+ "' is already blocked by mellophone. " + time_to_unlock);
							}
						}

						if (servletResponseMessage.contains("is blocked permanently")) {
							LOGGER.info("Пользователь " + login
									+ " заблокирован на постоянной основе");
							throw new BadCredentialsException(
									"User '" + login + "' is blocked by administrator");
						}

						if (servletResponseMessage.contains("Stored procedure message begin:")) {
							int indexBegin =
								servletResponseMessage.indexOf("Stored procedure message begin:");
							int beginMessageLength = "Stored procedure message begin:".length();
							int indexEnd =
								servletResponseMessage.indexOf("Stored procedure message end.");

							innerMessage = servletResponseMessage
									.substring(indexBegin + beginMessageLength, indexEnd).trim();

							LOGGER.info(innerMessage);
							throw new BadCredentialsException(innerMessage);
						}

						LOGGER.info("Пользователю " + login
								+ " не удалось войти в систему: Bad credentials");
						throw new BadCredentialsException("Bad credentials");

					}
				} finally {
					if (c != null)
						c.disconnect();
				}

			} catch (BadCredentialsException | IllegalStateException | SecurityException
					| IllegalFormatException | NullPointerException | IOException
					| IndexOutOfBoundsException e) {

				LOGGER.error("", e);

				if ("Bad credentials".equals(e.getMessage())) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else if (e.getMessage().contains("User")
						&& e.getMessage().contains("is blocked by mellophone")) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else if (e.getMessage().contains("User")
						&& e.getMessage().contains("is already blocked by mellophone")) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else if (e.getMessage().contains("User")
						&& e.getMessage().contains("is blocked by administrator")) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else if (innerMessage != null && e.getMessage().contains(innerMessage)) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else {
					throw new BadCredentialsException(
							"Authentication server is not available: " + e.getMessage(), e);
				}
			} catch (Exception err) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка фиксации неудачного логина в коде celesta", err);
				}
			}
			// }
		}

		// привязки сессии приложения к пользователю celesta
		// if (AppInfoSingleton.getAppInfo().getIsCelestaInitialized()) {
		try {
			AppInfoSingleton.getAppInfo().getCelestaInstance().login(sesid,
					((UserAndSessionDetails) arg1.getDetails()).getUserInfo().getSid());
		} catch (Exception e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta", e);
			}
		}
		// }

		// Authentication g = new Authentication.;

		// try {
		// arg0.setAuthenticated(true);
		// } catch (Exception e) {
		// }
		// SecurityContextHolder.getContext().setAuthentication(arg0);

		return arg1;

	}

	@Override
	public boolean supports(final Class<? extends Object> arg0) {
		return SignedUsernamePasswordAuthenticationToken.class.isAssignableFrom(arg0);
	}

	public static String encodeParam(final String param) throws UnsupportedEncodingException {
		String s = param;
		s = s.replace("%", "AB4AFD63A4C");
		s = s.replace("+", "D195B4C989F");
		s = URLEncoder.encode(s, "ISO8859_1");
		return s;
	}

}