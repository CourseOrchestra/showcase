package ru.curs.showcase.core.sp;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Ошибка при работе с полученным из БД ResultSet. Может быть вызвана потерей
 * соединения с сервером.
 * 
 * @author den
 * 
 */
public class ResultSetHandleException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка при работе с полученными из БД данными";

	private static final long serialVersionUID = -4089202125257954531L;

	public ResultSetHandleException(final Throwable aCause) {
		super(ExceptionType.SOLUTION, ERROR_MES, aCause);
	}

	public ResultSetHandleException(final String aString) {
		super(ExceptionType.SOLUTION, aString);
	}

}
