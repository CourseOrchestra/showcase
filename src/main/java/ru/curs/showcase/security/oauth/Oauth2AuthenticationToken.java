package ru.curs.showcase.security.oauth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Oauth2 токен авторизаци.
 * 
 * @author bogatov
 * 
 */
public class Oauth2AuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;
	private final String code;

	public Oauth2AuthenticationToken(final String sCode) {
		super(null);
		this.code = sCode;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.code;
	}
}
