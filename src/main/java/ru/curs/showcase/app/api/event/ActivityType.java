package ru.curs.showcase.app.api.event;

/**
 * Тип действия на сервере или на клиенте.
 * 
 * @author den
 * 
 */
public enum ActivityType {
	/**
	 * Вызов хранимой процедуры SQL.
	 */
	SP,
	/**
	 * Вызов процедуры JS в браузере.
	 */
	JS,
	/**
	 * Вызов Jython скрипта.
	 */
	JYTHON
}
