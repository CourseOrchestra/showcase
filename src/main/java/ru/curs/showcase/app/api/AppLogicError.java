package ru.curs.showcase.app.api;

/**
 * Ошибка в логике приложения. Суть ошибки должна быть передана в виде сообщения
 * в конструктор.
 * 
 * @author den
 * 
 */
public class AppLogicError extends RuntimeException {

	private static final long serialVersionUID = -6492472604858673829L;

	public AppLogicError() {
		super();
	}

	public AppLogicError(final String aMessage) {
		super(aMessage);
	}
}
