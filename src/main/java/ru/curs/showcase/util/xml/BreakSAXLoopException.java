package ru.curs.showcase.util.xml;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение для прерывания работы обработчика SAX.
 * 
 * @author den
 * 
 */
public class BreakSAXLoopException extends BaseException {

	private static final long serialVersionUID = 5726882721367127175L;

	public BreakSAXLoopException() {
		super(ExceptionType.CONTROL);
	}

}
