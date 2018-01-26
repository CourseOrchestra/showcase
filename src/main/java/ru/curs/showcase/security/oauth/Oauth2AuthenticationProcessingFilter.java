package ru.curs.showcase.security.oauth;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.AuthFailureHandler;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Фильтр Oauth2 авторизации.
 * 
 * @author bogatov
 * 
 */
public class Oauth2AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected Oauth2AuthenticationProcessingFilter() {
		super("/oauth");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		String auth = request.getParameter("auth");
		if (auth != null && !auth.isEmpty()) {
			Properties oauth2Properties = UserDataUtils.getGeneralOauth2Properties();
			response.sendRedirect(oauth2Properties.getProperty(UserDataUtils.OAUTH_AUTHORIZE_URL)
					+ "?client_id=" + oauth2Properties.getProperty(UserDataUtils.OAUTH_CLIENT_ID)
					+ "&client_secret="
					+ oauth2Properties.getProperty(UserDataUtils.OAUTH_CLIENT_SECRET)
					+ "&response_type=code");
			return null;
		}
		final String code = request.getParameter("code");
		Oauth2AuthenticationToken authRequest = new Oauth2AuthenticationToken(code);
		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		java.security.Principal principal = request.getUserPrincipal();
		userAndSessionDetails.setUserInfo(new UserInfo(principal != null ? principal.toString()
				: null, null, null, null, null, (String) null));
		authRequest.setDetails(userAndSessionDetails);

		AuthFailureHandler authFailureHandler = new AuthFailureHandler("OAUTH2");
		authFailureHandler.add("code", code);
		setAuthenticationFailureHandler(authFailureHandler);

		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		return authentication;
	}

}
