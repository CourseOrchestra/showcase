package ru.curs.showcase.security.oauth;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

/**
 * Данные Oauth2 токена.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement(name = "oauth2token")
public class Oauth2Token implements Serializable {
	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String tokenType;
	private int expiresIn;
	private String scope;
	private String refreshToken;

	/**
	 * Функция-getter для переменной accessToken.
	 * 
	 * @return accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Функция-setter для переменной accessToken.
	 * 
	 * @param sAccessToken
	 *            - строковая переменная
	 */
	@XmlElement(name = "access_token")
	public void setAccessToken(final String sAccessToken) {
		this.accessToken = sAccessToken;
	}

	/**
	 * Функция-getter для переменной tokenType.
	 * 
	 * @return tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Функция-setter для переменной tokenType.
	 * 
	 * @param sTokenType
	 *            - строковая переменная
	 */
	@XmlElement(name = "token_type")
	public void setTokenType(final String sTokenType) {
		this.tokenType = sTokenType;
	}

	/**
	 * Функция-getter для переменной expiresIn.
	 * 
	 * @return expiresIn
	 */
	public Integer getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Функция-setter для переменной expiresIn.
	 * 
	 * @param iExpiresIn
	 *            - константная целочисленная переменная
	 */
	@XmlElement(name = "expires_in")
	public void setExpiresIn(final int iExpiresIn) {
		this.expiresIn = iExpiresIn;
	}

	/**
	 * Функция-getter для переменной scope.
	 * 
	 * @return scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Функция-setter для переменной scope.
	 * 
	 * @param sScope
	 *            - константная строковая переменная
	 */
	@XmlElement(name = "scope")
	public void setScope(final String sScope) {
		this.scope = sScope;
	}

	/**
	 * Функция-getter для переменной refreshToken.
	 * 
	 * @return refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Функция-setter для переменной refreshToken.
	 * 
	 * @param sRefreshToken
	 *            - константная строковая переменная
	 */
	@XmlElement(name = "refresh_token")
	public void setRefreshToken(final String sRefreshToken) {
		this.refreshToken = sRefreshToken;
	}

}
