package ru.curs.showcase.security.logging;




/**
 * Интерфейс шлюза логирование события.
 * 
 * @author bogatov
 * 
 */
public interface SecurityLoggingGateway {

	/**
	 * Обработать событие.
	 * 
	 * @param event
	 *            - событие.
	 * @throws Exception 
	 */
	void doLogging(final Event event) throws Exception;
}
