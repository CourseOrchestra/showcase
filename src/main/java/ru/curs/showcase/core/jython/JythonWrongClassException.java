package ru.curs.showcase.core.jython;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее в случае несовпадения имени Jython файла и класса
 * наследника JythonProc.
 * 
 * @author den
 * 
 */
public class JythonWrongClassException extends BaseException {

	private static final String ERROR_MES =
		"Имя Jython класса-обработчика команд Showacase должно совпадать с именем файла '%s'";

	private static final long serialVersionUID = -2007919430381052492L;

	public static JythonWrongClassException checkForImportError(final String message,
			final String className) {
		if (message.contains("ImportError: cannot import name " + className)) {
			return new JythonWrongClassException(className);
		}
		return null;
	}

	public JythonWrongClassException(final String className) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, className));
	}
}
