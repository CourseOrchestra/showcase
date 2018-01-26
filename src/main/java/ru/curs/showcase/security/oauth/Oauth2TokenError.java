package ru.curs.showcase.security.oauth;

import javax.xml.bind.annotation.*;

/**
 * Детали ошибки получения oauth токена.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
public class Oauth2TokenError {
	@XmlElement(name = "error")
	private String error;
	@XmlElement(name = "error_description")
	private String errorDescription;

	/**
	 * Функция-getter для переменной error.
	 * 
	 * @return error
	 */
	public String getError() {
		return error;
	}

	/**
	 * Функция-setter для переменной error.
	 * 
	 * @param sError
	 *            - константная строковая переменная
	 */
	public void setAccessToken(final String sError) {
		this.error = sError;
	}

	/**
	 * Функция-getter для переменной errorDescription.
	 * 
	 * @return errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * Функция-setter для переменной errorDescription.
	 * 
	 * @param sErrorDescription
	 *            - константная строковая переменная
	 */
	public void setErrorDescription(final String sErrorDescription) {
		this.errorDescription = sErrorDescription;
	}

}
