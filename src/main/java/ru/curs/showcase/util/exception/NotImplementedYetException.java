package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Исключение для вызова в случае, если функция еще не реализована.
 * 
 * @author den
 * 
 */
public final class NotImplementedYetException extends BaseException {

	private static final long serialVersionUID = 4439432562578743266L;

	public NotImplementedYetException() {
		super(ExceptionType.SOLUTION, "Функционал еще не реализован");
	}

}
