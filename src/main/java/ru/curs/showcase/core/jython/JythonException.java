package ru.curs.showcase.core.jython;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Оболочка для исключений в Jython.
 * 
 * @author den
 * 
 */
public class JythonException extends BaseException {

	private static final long serialVersionUID = -2506420292176752336L;

	public JythonException(final String mes, final Exception e) {
		super(ExceptionType.SOLUTION, mes, e);
	}

	public JythonException(final String mes) {
		super(ExceptionType.SOLUTION, mes);
	}

}
