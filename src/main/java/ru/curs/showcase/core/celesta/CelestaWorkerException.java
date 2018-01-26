package ru.curs.showcase.core.celesta;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключения возникающие в классах при работе с celesta.
 * 
 * @author bogatov
 * 
 */
public class CelestaWorkerException extends BaseException {

	private static final long serialVersionUID = -6575081557900188883L;

	public CelestaWorkerException(final String mes, final Exception e) {
		super(ExceptionType.SOLUTION, mes, e);
	}

	public CelestaWorkerException(final String mes) {
		super(ExceptionType.SOLUTION, mes);
	}

}
