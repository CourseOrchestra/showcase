package ru.curs.showcase.app.server;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, возникающее при отсутствии необходимого параметра при GET или
 * POST запросе к серверу.
 * 
 * @author den
 * 
 */
public class HTTPRequestRequiredParamAbsentException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	public static final String ERROR_MES = "Не передан обязательный параметр: ";

	private static final long serialVersionUID = -3430283606302382887L;

	public HTTPRequestRequiredParamAbsentException(final String param) {
		super(ExceptionType.SOLUTION, ERROR_MES + param);
	}

}
