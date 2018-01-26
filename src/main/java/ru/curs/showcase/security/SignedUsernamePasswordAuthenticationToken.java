package ru.curs.showcase.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

//imports omitted
/**
 * AuthenticationToken, содержащий логин, пароль и requestSignature.
 * 
 * @author den
 * 
 */
public class SignedUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3145548673810647886L;

	/**
	 * Construct a new token instance with the given principal, credentials, and
	 * signature.
	 * 
	 * @param principal
	 *            the principal to use
	 * @param credentials
	 *            the credentials to use
	 * @param signature
	 *            the signature to use
	 */
	public SignedUsernamePasswordAuthenticationToken(final String aPrincipal,
			final String aCredentials) {
		super(aPrincipal, aCredentials);
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}
}