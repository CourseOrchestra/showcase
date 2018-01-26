package ru.curs.showcase.security.esia;

import java.io.*;
import java.net.*;

import javax.xml.stream.*;

import org.slf4j.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * ESIA провайдер авторизации.
 * 
 */
public class ESIAAuthenticationProvider implements AuthenticationProvider {

	private static final String UTF8 = "UTF-8";

	private static final Logger LOGGER = LoggerFactory.getLogger(ESIAAuthenticationProvider.class);

	@Override
	public Authentication authenticate(final Authentication auth) {

		UserAndSessionDetails userAndSessionDetails = (UserAndSessionDetails) auth.getDetails();

		UserInfo userInfo = userAndSessionDetails.getUserInfo();

		if (userInfo != null) {
			try {
				AppInfoSingleton.getAppInfo().getCelestaInstance()
						.login(userAndSessionDetails.getSessionId(), userInfo.getSid());
			} catch (Exception e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta", e);
				}
			}

			// ----------------------------------------------

			String url = null;
			try {
				url = SecurityParamsFactory.getLocalAuthServerUrl();
			} catch (SettingsFileOpenException e) {
				LOGGER.error(SecurityParamsFactory.APP_PROP_READ_ERROR, e);
			}

			if (url != null) {
				HttpURLConnection conn = null;
				try {
					try {
						StringWriter sw = new StringWriter();
						XMLStreamWriter xw =
							XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
						xw.writeStartDocument("utf-8", "1.0");
						xw.writeEmptyElement("user");
						writeXMLAttr(xw, "login", userInfo.getLogin());
						writeXMLAttr(xw, "SID", userInfo.getSid());
						writeXMLAttr(xw, "name", userInfo.getFullName());
						writeXMLAttr(xw, "email", userInfo.getEmail());
						writeXMLAttr(xw, "phone", userInfo.getPhone());
						writeXMLAttr(xw, "snils", userInfo.getSnils());
						writeXMLAttr(xw, "gender", userInfo.getGender());
						writeXMLAttr(xw, "firstname", userInfo.getFirstName());
						writeXMLAttr(xw, "lastname", userInfo.getLastName());
						writeXMLAttr(xw, "middlename", userInfo.getMiddleName());
						writeXMLAttr(xw, "birthdate", userInfo.getBirthDate());
						writeXMLAttr(xw, "birthplace", userInfo.getBirthPlace());
						writeXMLAttr(xw, "trusted", String.valueOf(userInfo.isTrusted()));
						xw.writeEndDocument();
						xw.flush();

						String sesid = userAndSessionDetails.getSessionId();
						String login = userInfo.getLogin();
						String userinfo = sw.toString();

						StringBuilder postData = new StringBuilder();
						postData.append(URLEncoder.encode("sesid", UTF8));
						postData.append("=");
						postData.append(URLEncoder.encode(sesid, UTF8));
						postData.append("&");
						postData.append(URLEncoder.encode("login", UTF8));
						postData.append("=");
						postData.append(URLEncoder.encode(login, UTF8));
						postData.append("&");
						postData.append(URLEncoder.encode("userinfo", UTF8));
						postData.append("=");
						postData.append(URLEncoder.encode(userinfo, UTF8));
						byte[] postDataBytes = postData.toString().getBytes(UTF8);

						URL server = new URL(url + "/loginesiauser");

						conn = (HttpURLConnection) server.openConnection();
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						conn.setRequestProperty("Content-Length",
								String.valueOf(postDataBytes.length));
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.getOutputStream().write(postDataBytes);

						conn.connect();

						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid,
									true);
						} else {
							LOGGER.warn("Error calling /loginesiauser, conn.getResponseCode()="
									+ conn.getResponseCode());
						}

					} catch (IOException | XMLStreamException | FactoryConfigurationError e) {
						LOGGER.error("Error calling /loginesiauser", e);
					}
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}

		return auth;

	}

	static void writeXMLAttr(final XMLStreamWriter xw, final String attrName, final String value)
			throws XMLStreamException {
		if (value != null) {
			xw.writeAttribute(attrName, value);
		}
	}

	@Override
	public boolean supports(final Class<?> arg0) {
		return ESIAAuthenticationToken.class.isAssignableFrom(arg0);
	}

}
