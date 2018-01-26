package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Класс внутренней ошибки серверной части Showcase. Используется для перехвата
 * исключений, которых "не должно быть".
 * 
 * @author den
 * 
 */
public class ServerLogicError extends BaseException {

	private static final long serialVersionUID = 4193893671079202405L;

	public ServerLogicError(final String aMessage) {
		super(ExceptionType.APP, aMessage);
	}

	public ServerLogicError(final Exception ex) {
		super(ExceptionType.APP, ex);
	}

}
