package ru.curs.showcase.security.spnego;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.sourceforge.spnego.*;

import org.ietf.jgss.GSSException;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.AuthFailureHandler;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Фильтр Spnego авторизации.
 * 
 * @author bogatov
 * 
 */
public class SpnegoAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final String pathPref = "WEB-INF" + File.separator + "classes" + File.separator;
	private SpnegoAuthenticator authenticator;

	protected SpnegoAuthenticationProcessingFilter() {
		super("/spnego");
		AuthFailureHandler authFailureHandler = new AuthFailureHandler("SPNEGO");
		setAuthenticationFailureHandler(authFailureHandler);
	}

	private void init() throws Exception {
		String realPath = getServletContext().getRealPath("/") + pathPref;
		Properties properties = UserDataUtils.getGeneralSpnegoProperties();
		if (properties == null) {
			throw new Exception("Spnego properties not found in " + UserDataUtils.PROPFILENAME);
		}
		Map<String, String> config = new HashMap<String, String>();
		config.put("spnego.logger.level",
				properties.getProperty(UserDataUtils.SPNEGO_LOGGER_LEVEL, "6"));
		config.put(SpnegoHttpFilter.Constants.ALLOW_BASIC,
				properties.getProperty(UserDataUtils.SPNEGO_ALLOW_BASIC, "true"));
		config.put(SpnegoHttpFilter.Constants.ALLOW_DELEGATION, "false");
		config.put(SpnegoHttpFilter.Constants.ALLOW_LOCALHOST, "false");
		config.put(SpnegoHttpFilter.Constants.ALLOW_UNSEC_BASIC, "true");
		config.put(SpnegoHttpFilter.Constants.CLIENT_MODULE, "spnego-client");
		config.put(SpnegoHttpFilter.Constants.SERVER_MODULE, "spnego-server");
		config.put(SpnegoHttpFilter.Constants.KRB5_CONF, realPath + "krb5.conf");
		config.put(SpnegoHttpFilter.Constants.LOGIN_CONF, realPath + "login.conf");
		config.put(SpnegoHttpFilter.Constants.PREAUTH_USERNAME,
				properties.getProperty(UserDataUtils.SPNEGO_PREAUTH_USERNAME));
		config.put(SpnegoHttpFilter.Constants.PREAUTH_PASSWORD,
				properties.getProperty(UserDataUtils.SPNEGO_PREAUTH_PASSWORD));
		config.put(SpnegoHttpFilter.Constants.PROMPT_NTLM, "true");
		// pre-authenticate
		this.authenticator = new SpnegoAuthenticator(config);

	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse responce) throws ServletException, IOException {
		final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(responce);
		try {
			if (this.authenticator == null) {
				try {
					init();
				} catch (final Exception ex) {
					failure(request, spnegoResponse, "Pre-authenticate is failure.", ex);
					return null;
				}
			}
			SpnegoPrincipal principal = null;
			try {
				principal = this.authenticator.authenticate(request, spnegoResponse);
			} catch (GSSException gsse) {
				throw new ServletException(gsse);
			}

			if (spnegoResponse.isStatusSet()) {
				return null;
			}

			if (principal == null) {
				failure(request, spnegoResponse, "Authenticate is failure.",
						new AuthenticationException("Principal was null.") {
							private static final long serialVersionUID = 1L;
						});
				return null;
			}

			SpnegoAuthenticationToken token = new SpnegoAuthenticationToken(principal);
			token.setAuthenticated(true);
			UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
			final String[] username = principal.getName().split("@", 2);
			userAndSessionDetails.setUserInfo(new UserInfo(principal.getName(), null, username[0],
					null, null, (String) null));
			token.setDetails(userAndSessionDetails);
			return token;

		} catch (Exception ex) {
			failure(request, spnegoResponse, "Authenticate is failure.", ex);
		}
		return null;
	}

	private void failure(final HttpServletRequest request,
			final SpnegoHttpServletResponse response, final String msg, final Exception ex)
			throws IOException, ServletException {
		if (getFailureHandler() != null) {
			AuthenticationException authEx;
			if (ex == null || !(ex instanceof AuthenticationException)) {
				authEx =
					new AuthenticationException(msg
							+ (ex != null ? " Detail: " + ex.getMessage() : ""), ex) {
						private static final long serialVersionUID = 1L;
					};
			} else {
				authEx = (AuthenticationException) ex;
			}
			getFailureHandler().onAuthenticationFailure(request, response, authEx);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true);
		}
	}
}