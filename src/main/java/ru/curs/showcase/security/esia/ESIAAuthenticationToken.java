package ru.curs.showcase.security.esia;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * ESIA токен аутентификации.
 * 
 */
public class ESIAAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;
	private final String username;

	public ESIAAuthenticationToken(final String sUsername) {
		super(null);

		username = sUsername;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.username;
	}
}
