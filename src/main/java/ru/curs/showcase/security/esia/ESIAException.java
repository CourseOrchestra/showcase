package ru.curs.showcase.security.esia;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * ESIA - исключение.
 * 
 */
public class ESIAException extends BaseException {
	private static final long serialVersionUID = -2051866084221760857L;

	public ESIAException(final Throwable aCause) {
		super(ExceptionType.SOLUTION, aCause);
	}

	public ESIAException() {
		super(ExceptionType.SOLUTION);
	}

	public ESIAException(final String aMessage) {
		super(ExceptionType.SOLUTION, aMessage);
	}

	public ESIAException(final String aMessage, final Throwable aCause) {
		super(ExceptionType.SOLUTION, aMessage, aCause);
	}

}
