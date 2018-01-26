package ru.curs.showcase.security.spnego;

import net.sourceforge.spnego.SpnegoPrincipal;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Spnego токен авторизаци.
 * 
 * @author bogatov
 * 
 */
public class SpnegoAuthenticationToken extends AbstractAuthenticationToken {
	private final SpnegoPrincipal principal;

	public SpnegoAuthenticationToken(final SpnegoPrincipal oPrincipal) {
		super(null);
		this.principal = oPrincipal;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public Object getCredentials() {
		return this.principal != null ? this.principal.getDelegatedCredential() : null;
	}

	@Override
	public Object getPrincipal() {
		return this.principal != null ? this.principal.getName() : null;
	}

}
