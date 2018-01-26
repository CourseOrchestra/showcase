package ru.curs.showcase.security.oauth;

import java.io.*;
import java.net.*;
import java.util.Properties;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Oauth2 провайдер авторизации.
 * 
 * @author bogatov
 * 
 */
public class Oauth2AuthenticationProvider implements AuthenticationProvider {
	private final int startErrorCode = 400;

	@Override
	public Authentication authenticate(final Authentication auth) {
		Properties oauth2Properties = UserDataUtils.getGeneralOauth2Properties();
		if (oauth2Properties == null) {
			throw new BadCredentialsException("Oauth2 setting in app.properties not found");
		}
		String code = (String) auth.getPrincipal();
		if (code == null || code.isEmpty()) {
			throw new BadCredentialsException("Code is null or empty.");
		}
		try {
			URL url = new URL(oauth2Properties.getProperty(UserDataUtils.OAUTH_TOLEN_URL));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");

			String parameters = "grant_type=authorization_code&client_id="
					+ oauth2Properties.getProperty(UserDataUtils.OAUTH_CLIENT_ID)
					+ "&client_secret="
					+ oauth2Properties.getProperty(UserDataUtils.OAUTH_CLIENT_SECRET) + "&code="
					+ code;

			OutputStream cOut = conn.getOutputStream();
			try {
				cOut.write(parameters.getBytes());
				cOut.flush();
				ObjectMapper mapper = new ObjectMapper();
				JaxbAnnotationIntrospector introspector = new JaxbAnnotationIntrospector(
						TypeFactory.defaultInstance());
				mapper.setAnnotationIntrospector(introspector);
				if (conn.getResponseCode() >= startErrorCode) {
					InputStream errorIn = conn.getErrorStream();
					String errorMsg;
					MediaType responceMediaType = conn.getContentType() != null ? MediaType
							.valueOf(conn.getContentType()) : null;
					if (responceMediaType != null
							&& MediaType.APPLICATION_JSON.isCompatibleWith(responceMediaType)) {
						Oauth2TokenError oauth2TokenError = mapper.readValue(conn.getErrorStream(),
								Oauth2TokenError.class);
						errorMsg = "error: " + oauth2TokenError.getError() + ", description:"
								+ oauth2TokenError.getErrorDescription();
					} else {
						errorMsg = TextUtils.streamToString(errorIn);
					}
					throw new BadCredentialsException("Bad credentials. Detail: " + errorMsg);
				} else {
					Oauth2Token oauth2Token = mapper.readValue(conn.getInputStream(),
							Oauth2Token.class);
					auth.setAuthenticated(true);
					((UserAndSessionDetails) auth.getDetails()).setOauth2Token(oauth2Token);
				}
			} finally {
				cOut.close();
			}

			return auth;
		} catch (Exception ex) {
			throw new BadCredentialsException("Authentication oauth2 server is not available: "
					+ ex.getMessage(), ex);
		}
	}

	@Override
	public boolean supports(final Class<?> arg0) {
		return Oauth2AuthenticationToken.class.isAssignableFrom(arg0);
	}

}
