package ru.curs.showcase.app.server.rest;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.slf4j.*;
import org.springframework.security.authentication.AuthenticationServiceException;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.*;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * @author a.lugovtsov
 *
 */
public final class ShowcaseRestServlet extends HttpServlet {

	private static final long serialVersionUID = 1311685218914828051L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseRestServlet.class);

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage()
				.contains("Не удаётся подключиться к указанной базе данных")) {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("При запуске сервера приложений произошла ошибка.");
			response.setStatus(500);
			response.getWriter().close();
			return;
		}

		// rest.authentication.type
		String sesId = request.getSession().getId();
		String requestUrl = request.getRequestURL().toString();

		if ((requestUrl.endsWith("restlogin")) || requestUrl.endsWith("restlogin/")) {
			addAccessControlAllowOriginPropertyToResponceHeader(response);

			String userSid = null;

			String usr = request.getParameter("user");

			String pwd = request.getParameter("password");

			// взаимоействие с мелофоном

			URL server;
			String url = "";
			try {
				url = SecurityParamsFactory.getLocalAuthServerUrl();
			} catch (SettingsFileOpenException e1) {
				throw new AuthenticationServiceException(
						SecurityParamsFactory.APP_PROP_READ_ERROR, e1);
			}

			server =
				new URL(url
						+ String.format("/checkcredentials?login=%s&pwd=%s",
								AuthServerAuthenticationProvider.encodeParam(usr),
								AuthServerAuthenticationProvider.encodeParam(pwd)));

			HttpURLConnection c = null;

			try {
				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					UserInfo ud = null;
					try {
						List<UserInfo> l = UserInfoUtils.parseStream(c.getInputStream());
						ud = l.get(0);
						ud.setResponseCode(c.getResponseCode());
					} catch (TransformerException e) {
						throw new ServletException(AuthServerUtils.AUTH_SERVER_DATA_ERROR
								+ e.getMessage(), e);
					}
					userSid = ud.getSid();
				} else {
					userSid = null;
				}

			} finally {
				if (c != null) {
					c.disconnect();
				}
			}

			if (userSid != null) {
				try {
					AppInfoSingleton.getAppInfo().getCelestaInstance().login(sesId, userSid);
					AppInfoSingleton.getAppInfo().getSessionSidsMap().put(sesId, userSid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(
						"ОШИБКА выполнения REST запроса restlogin: Логин пользователя ''" + usr
								+ "'' неуспешен. Неверная пара логин-пароль.");

				response.setStatus(403);
				response.getWriter().close();

			}

			return;
		}

		if ((requestUrl.endsWith("restlogout")) || requestUrl.endsWith("restlogout/")) {
			addAccessControlAllowOriginPropertyToResponceHeader(response);
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance().logout(sesId, false);
				AppInfoSingleton.getAppInfo().getSessionSidsMap().remove(sesId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		String requestType = request.getMethod();
		String userToken = request.getHeader("user-token");
		String clientIP = request.getRemoteAddr();

		String acceptLanguage = request.getHeader("Accept-Language");
		if (acceptLanguage == null || acceptLanguage.isEmpty()) {
			acceptLanguage = "en";
		}

		String requestURLParams = request.getQueryString();

		String requestData = "";
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		requestData = buffer.toString();

		String restProc = UserDataUtils.getGeneralOptionalProp("rest.entry.proc");

		if ((restProc == null) || restProc.isEmpty()) {
			// System.out.println("restProc: null");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		JythonRestResult responcseData = null;
		if (restProc.endsWith(".cl") || restProc.endsWith(".celesta"))

			try {
				responcseData =
					RESTGateway.executeRESTcommand(requestType, truncateRequestUrl(requestUrl),
							requestData, getHeadersJson(request), getUrlParamsJson(request),
							sesId, restProc, clientIP);
			} catch (RESTGateway.ShowcaseRESTUnauthorizedException e) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;

			}
		if (restProc.endsWith(".py"))
			responcseData =
				RESTGateway.executeRESTcommandFromJythonProc(requestType,
						truncateRequestUrl(requestUrl), requestData, getHeadersJson(request),
						getUrlParamsJson(request), restProc, clientIP);

		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(responcseData.getResponseData());

		response.setStatus(responcseData.getResponseCode());
		response.setHeader("Content-Type", responcseData.getContentType());
		addAccessControlAllowOriginPropertyToResponceHeader(response);
		for (Iterator<Map.Entry<String, String>> iter =
			responcseData.getResponseHttpParametersMap().entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> entry = iter.next();
			if ("Access-Control-Allow-Origin".equals(entry.getKey())) {
				continue;
			}

			response.setHeader(entry.getKey(), entry.getValue());

		}

		LOGGER.info("Using Rest WebService. \nCalled procedure: " + restProc + "\nRequest Type: "
				+ requestType + "\nRequest URL: " + requestUrl + "\nClient IP: " + clientIP
				+ "\nUser Token: " + userToken + "\nAccept Language: " + acceptLanguage
				+ "\nRequest Data: " + requestData + "\nRequest URL Params: " + requestURLParams
				+ "\nResponse Code: " + responcseData.getResponseCode() + "\nResponse Data: "
				+ StringEscapeUtils.unescapeJava(responcseData.getResponseData()));

		response.getWriter().close();
	}

	private String getFullURL(final HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	private String getUrlParamsJson(final HttpServletRequest request) {
		SortedMap<String, List<String>> urlParamsMap = null;
		try {
			urlParamsMap = ServletUtils.prepareURLParamsMap(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (urlParamsMap == null)
			return "{}";

		org.json.JSONObject jsonObj = new JSONObject(urlParamsMap);
		return jsonObj.toString();
	}

	private String getHeadersJson(final HttpServletRequest request) {
		Map<String, String> headersMap = new HashMap<>();
		@SuppressWarnings("unchecked")
		Enumeration<String> iterator = request.getHeaderNames();
		while (iterator.hasMoreElements()) {
			String headerName = iterator.nextElement();
			String headerValue = request.getHeader(headerName);
			headersMap.put(headerName, headerValue);
		}
		org.json.JSONObject jsonObj = new JSONObject(headersMap);
		return jsonObj.toString();
	}

	private String truncateRequestUrl(final String url) {
		// String[] parts = url.split("api");
		// return parts[1];
		return url.substring(url.indexOf("api") + 3);
	}

	private void addAccessControlAllowOriginPropertyToResponceHeader(
			final HttpServletResponse aresponse) {
		String rach = UserDataUtils.getGeneralOptionalProp("rest.allow.crossdomain.hosts");
		if (rach != null && "true".equalsIgnoreCase(rach.trim()))
			aresponse.setHeader("Access-Control-Allow-Origin", "*");
	}
}
