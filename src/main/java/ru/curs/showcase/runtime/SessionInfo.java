package ru.curs.showcase.runtime;

/**
 * Информация о сессии пользователя.
 * 
 * @author den
 * 
 */
public class SessionInfo {
	/**
	 * Признак того, что авторизация была произведена через AuthServer.
	 */
	private boolean authViaAuthServer = false;

	/**
	 * Признак того, что авторизация была произведена через ESIA.
	 */
	private boolean authViaESIA = false;

	/**
	 * Уникальный временный пароль, сгенерированный для данной сессии при
	 * аутентификации через AuthServer.
	 */
	private String authServerCrossAppPassword = null;

	public String getAuthServerCrossAppPassword() {
		return authServerCrossAppPassword;
	}

	public void setAuthServerCrossAppPassword(final String aAuthServerCrossAppPassword) {
		authServerCrossAppPassword = aAuthServerCrossAppPassword;
	}

	public boolean isAuthViaAuthServer() {
		return authViaAuthServer;
	}

	public void setAuthViaAuthServer(final boolean aAuthViaAuthServer) {
		authViaAuthServer = aAuthViaAuthServer;
	}

	public boolean isAuthViaESIA() {
		return authViaESIA;
	}

	public void setAuthViaESIA(final boolean aAuthViaESIA) {
		authViaESIA = aAuthViaESIA;
	}
}
