package ru.curs.showcase.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.security.oauth.Oauth2Token;

/**
 * 
 * A holder of selected HTTP details and UserInfo related to a web
 * authentication request.
 * 
 * @author anlug
 * 
 */
public class UserAndSessionDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 8550679539357144098L;

	private UserInfo userInfo = null;
	private Oauth2Token oauth2Token = null;

	private Boolean authViaAuthServer = false;

	public UserAndSessionDetails(final HttpServletRequest request) {
		super(request);
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(final UserInfo auserInfo) {
		this.userInfo = auserInfo;
	}

	public Boolean isAuthViaAuthServer() {
		return authViaAuthServer;
	}

	public void setAuthViaAuthServer(final Boolean aauthViaAuthServer) {
		this.authViaAuthServer = aauthViaAuthServer;
	}

	public Oauth2Token getOauth2Token() {
		return oauth2Token;
	}

	public void setOauth2Token(final Oauth2Token oOauth2Token) {
		this.oauth2Token = oOauth2Token;
	}

}
