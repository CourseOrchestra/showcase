package ru.curs.showcase.app.server.rest;

/**
 * Тип аутентификации пользователя для celesta-скриптов.
 * 
 * @author s.borodanev
 *
 */

public enum RestAuthenticationType {
	/**
	 * Аутентифиция от имени пользователя userCelestaSid.
	 */
	SIMPLE,

	/**
	 * Аутентифиция от имени пользователя, залогиненного в текущей http-сессии
	 * для скриптов celesta.
	 */
	CELESTA
}
